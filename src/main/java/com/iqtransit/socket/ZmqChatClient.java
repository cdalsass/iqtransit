// reference
// http://stackoverflow.com/questions/22686651/jeromq-shutdown-correctly
// https://github.com/zeromq/jeromq/blob/master/src/test/java/guide/interrupt.java


// design: No network activity on UI thread. 

package com.iqtransit.socket;

import java.util.Scanner;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQException;
import org.zeromq.ZContext;
import java.security.SecureRandom;

public class ZmqChatClient extends Thread {

  private ZContext context;
  private final static Scanner in = new Scanner(System.in);
  private ClientEventListener clientEventListener;
  private String send_host;
  private String receive_host;
  private String internal_sock;

  /* 
    InternalCommunicator class used to send internal messages to ZMQ socket. 
    Used as a form of interthread communication.
    Passes ZMQ message to self using internal_sock.
    you should not access any socket from multiple threads, so use this method instead.
    internal messages are appended with a single, first character to determine types of messages. 
   */

  private class InternalCommunicator {

    public final static int STATUS_SEND = 1;
    public final static int STATUS_SHUTDOWN = 2;
    private String internal_sock;

    public InternalCommunicator(String internal_sock) {
      this.internal_sock = internal_sock;
    }

    public boolean send(String message, int message_type) {
      
        ZContext context = new ZContext();
        
        // need to create ipc socket and use that to send to running receiver thread as a form of 
        // inter-thread communication. do that without exposing underlying implementation.
        Socket api_client  = context.createSocket(ZMQ.PUSH);
        api_client.connect(this.internal_sock);
        // first char is a code telling what to do. 

        if ("".equals(null)) {
          message = "";
        }

        boolean value = api_client.send(Integer.toString(message_type) + message);
        try {
          // found this technique inside the unit test documentation. Seems that sleeping gives enough time to pass the internal message prior to closing. should be reliable, sicne it is internal afterall...
          Thread.sleep(30);
        }  catch (InterruptedException e3) {
          
        }
    
        context.destroy();
        
      return true;
    }

  }



  public void send(String message) {
          InternalCommunicator ic = new InternalCommunicator(this.internal_sock);
          ic.send(message, InternalCommunicator.STATUS_SEND);  
  }

  public void disconnect() {
          InternalCommunicator ic = new InternalCommunicator(this.internal_sock);
          ic.send(null, InternalCommunicator.STATUS_SHUTDOWN);  

  }

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



  public void connect() {
    this.start();
  }

  


  public void run() {

    this.context = new ZContext();

    Socket send = context.createSocket(ZMQ.PUSH);
    send.connect(this.send_host); /* configure */

    Socket receive = context.createSocket(ZMQ.SUB);
    receive.connect(this.receive_host); /* configure */
    receive.subscribe("".getBytes());

    Socket internal_sending_sock = context.createSocket(ZMQ.PULL);
    //Log.v("CMUTER","binding to socket "+ this.internal_sock);
    try {
      internal_sending_sock.bind(this.internal_sock);

    } catch (Exception e) {
      System.out.println(this.internal_sock + "already open from thread " + Thread.currentThread().getName());
    }
    
    Poller poller = new Poller(2);
    poller.register(receive, Poller.POLLIN);
    poller.register(internal_sending_sock, Poller.POLLIN);
    
 
 // receiver = new Receiver(poller, receive, internal_sending_sock, send);

    while (!Thread.currentThread().isInterrupted()) {

      // avoiding java.nio.channels.ClosedChannelException which occurs when client is disconnected. 
          try {
              poller.poll();
            

              if (poller.pollin(0)) {
                // message arriving from network server. 
                String recvMessage = receive.recvStr(0);
                // call interface function.
                ZmqChatClient.this.on(recvMessage);
              } 
              if (poller.pollin(1)) {
                // an internal send(), possibly from a UI or location update. This should be sent back to the server. 
                String recvMessage = internal_sending_sock.recvStr(0);
                if (Character.getNumericValue(recvMessage.charAt(0)) == InternalCommunicator.STATUS_SHUTDOWN) {
                  
                  break;

                } else {
                  send.send(recvMessage);
                }
                // no need to call interface function.
                // ... but pass to network server. 
              }
            
          } catch (Exception E) {
  
             break;
          }

      }
 
      /* close all sockets and exit the loop */
      internal_sending_sock.close();
      receive.close();
      send.close();
      context.destroy();

      return;
  } 
}