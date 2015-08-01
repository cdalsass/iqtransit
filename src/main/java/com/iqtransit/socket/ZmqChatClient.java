// design: No network activity on UI thread. 

package com.iqtransit.socket;

import java.util.Scanner;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;

public class ZmqChatClient extends Thread {

  private Context context;
  private final static Scanner in = new Scanner(System.in);
  private ClientEventListener clientEventListener;
  private String send_host;
  private String receive_host;
  private String internal_sock;

  public ZmqChatClient(String send_host, String receive_host, String internal_sock) {
    this.send_host = send_host;
    this.receive_host = receive_host;
    this.internal_sock = internal_sock;
  }

  /* INTERFACE 1 */
  public interface ClientEventListener {
      void onClientEvent(String event);
  }

  public void setClientEventListener(ClientEventListener l) {
      this.clientEventListener = l;
  }

  public void on(String event) {
    if (this.clientEventListener != null) {
        this.clientEventListener.onClientEvent(event);
    }
  }

  private  class Receiver extends Thread {
    private final Poller poller;
    private final Socket receive;
    private final Socket internal_sending_sock;
    private final Socket send; 

    public Receiver(Poller poller, Socket receive, Socket internal_sending_sock, Socket send) {
      this.poller = poller;
      this.receive = receive;
      this.internal_sending_sock = internal_sending_sock;
      this.send = send; 
    }

    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        
        poller.poll();

        if (poller.pollin(0)) {
          // message arriving from network server. 
          String recvMessage = this.receive.recvStr(0);
          // call interface function.
          System.out.println("YAY! GOT MESSAGE OF TYPE 1" + recvMessage);
          ZmqChatClient.this.on(recvMessage);
        } 
        if (poller.pollin(1)) {
          // an internal send(), possibly from a UI or location update. This should be sent back to the server. 
          String recvMessage = this.internal_sending_sock.recvStr(0);
          // no need to call interface function.
          // ... but pass to network server. 
          this.send.send(recvMessage);
        }
      }
    }
  }

  public void connect() {
    this.start();
  }

  public boolean send(String message) {
    
    Context context = ZMQ.context(1);

    // need to create ipc socket and use that to send to running receiver thread as a form of 
    // inter-thread communication. do that without exposing underlying implementation.
    Socket api_client  = context.socket(ZMQ.PUSH);
    api_client.connect(this.internal_sock);
    boolean value = api_client.send(message);
    api_client.close();
    context.term();
    return true;
  }

  public void run() {

    this.context = ZMQ.context(1);

    Socket send = context.socket(ZMQ.PUSH);
    send.connect(this.send_host); /* configure */

    Socket receive = context.socket(ZMQ.SUB);
    receive.connect(this.receive_host); /* configure */
    receive.subscribe("".getBytes());

    Socket internal_sending_sock = context.socket(ZMQ.PULL);
    //Log.v("CMUTER","binding to socket "+ this.internal_sock);
    internal_sending_sock.bind(this.internal_sock);
    
    Poller poller = new Poller(2);
    poller.register(receive, Poller.POLLIN);
    poller.register(internal_sending_sock, Poller.POLLIN);

    try {
      
      Receiver receiver = new Receiver(poller, receive, internal_sending_sock, send);
      receiver.start(); 
      receiver.join();

    }

    catch (InterruptedException e) {}
    finally {
      receive.close();
      send.close();
      internal_sending_sock.close();
      context.term();
    }
  }
}