package com.iqtransit.socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
/* modified from http://cs.lmu.edu/~ray/notes/javanetexamples/ */

public class Client implements Runnable {

    DataInputStream in;
    DataOutputStream out;
    private Socket socket;
    private final Object lock = new Object();
    protected String host = "";
    protected int port = 0;
    private ClientEventListener clientEventListener;
    boolean isPaused = false;
    ArrayList<String> unsentMessages;

    public boolean isPaused() {
        return this.isPaused;
    }

    public void setPaused(boolean isPaused) {
        synchronized (lock) {
            this.isPaused = isPaused;
        }
    }

    public void on(String event) {
        if (this.clientEventListener != null) {
            this.clientEventListener.onClientEvent(event);
        }
    }

    /* INTERFACE 1 */
    public interface ClientEventListener {
        void onClientEvent(String event);
    }

    public void setClientEventListener(ClientEventListener l) {
        this.clientEventListener = l;
    }

    public Client(String host, int port) {
        this.unsentMessages = new ArrayList<String>();
        this.host = host;
        this.port = port;
    }

    public void run() {

        while (true) {

            if (isPaused == true) {

                try {
                    Thread.sleep(1000);
                } catch (java.lang.InterruptedException e1) {
                    System.out.println("InterruptedException e1");
                }

            } else {

                try {

                    socket = new Socket(host, port);
                    System.out.println("create new socket");

                } catch (java.net.UnknownHostException e2) {

                    System.out.println("UnknownHostException for host " + host + " and port " + port);

                    try {
                        Thread.sleep(2000);
                    } catch (java.lang.InterruptedException e3) {
                        System.out.println("InterruptedException raised");
                    }
                    // try again in a few seconds;
                    continue;

                } catch (IOException e) {
                    System.out.println("IOException. Wait a few seconds and try again.");

                    try {
                        Thread.sleep(2000);
                    } catch (java.lang.InterruptedException e4) {
                        System.out.println("InterruptedException raised");
                    }
                    // try again in a few seconds;
                    continue;
                }

                try {

                    /* Make connection and initialize streams
                    http://stackoverflow.com/questions/19383169/bufferedreader-blocking-at-read
                    Since you are using a String as your entire message. You can use
                    DataInputStream and DataOutputStream stream decorators to frame the message
                    for you with readUTF() and writeUTF(String). writeUTF(String) basically frames
                    the string by writing its length to stream before writing the string.
                    readUTF() then reads this length and then knows how much data it needs to read
                    off the stream before returning.
                    */

                    in = new DataInputStream(socket.getInputStream());

                    if (in == null) {
                        // server is probably not running.
                        break;
                    }

                    out = new DataOutputStream(socket.getOutputStream());

                } catch (java.io.IOException e2) {
                    System.out.println("IOException");
                    try {
                        System.out.println("wait 10 secs");
                        Thread.sleep(10000);
                    } catch (java.lang.InterruptedException e ) {
                        System.out.println("InterruptedException raised");
                    }
                }

                // Process all messages from server, according to the protocol.
                while (true) {
                    try {

                        // this function blocks until there is something to read.

                        synchronized (this) {
                            String s; 
                            while (unsentMessages.isEmpty() == false) {
                                
                                s = unsentMessages.remove(0);
                                try {
                                    System.out.println("just wrote line " + s + "\n");
                                    System.out.flush();
                                    out.writeUTF(s);
                                    out.flush();
                                } catch (IOException e) {
                                    System.out.println("just caught IOException");
                                }

                            }


                        }

                        String line = in.readUTF();

                        if (line == null) { // prevents broken connection from printing millions of nulls.
                            break;
                        }

                        this.on(line);

                        

                    } catch (java.io.IOException e3) {

                        System.out.println("wait 10 secs");

                        try {
                            Thread.sleep(10000);

                        } catch (java.lang.InterruptedException e ) {

                            System.out.println("InterruptedException 4 raised");

                        }

                        System.out.println("IOException3");
                        // i'll hit this when socket is externall.
                        break;
                    }
                }
            }
        }
    }


    public void sendMessage(String s) {

        synchronized (this) {
            unsentMessages.add(s);
        }

    }

    /*
     http://stackoverflow.com/a/9250117/1620112
     You can close the socket from the main thread. This will break the call to accept and cause it to throw an IOException. You can then exit the accept thread's run method from the catch of the IOException. -Tudor.
    */

    public void close () {
        synchronized (lock) {
            /* this could be called from another thread */
            try {
                // not quite sure hy sometimes socket is null. causes crash onPause();
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {

            }
        }
    }
}