/* original code taken from http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html */

package com.iqtransit.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
    Responsible for handling chat protocol. 
    Receives socket from calling function. 
    Cleans up socket and closes all file descriptors.
    Notifies callers when it's time to destroy.
    Notifies callers when a message comes in.
 */

public class ChatWorker extends Thread implements Worker {
    public boolean DEBUG = false; 
    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected MessageListener messageListener = null;
    protected DestroyListener destroyListener = null;
    protected DataOutputStream output;

    public ChatWorker(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    /** INTERFACE 1 **/
    public interface MessageListener {
        void onMessage(String event);
    }

    public void setMessageListener(MessageListener l) {
        this.messageListener = l;
    }

     /** INTERFACE 2 **/
    public interface DestroyListener {
        void onDestroy();
    }

    public void setDestroyListener(DestroyListener l) {
        this.destroyListener = l;
    }

    // add a message to the message queue. 
    public void sendMessage(String s) {
        try {
            if (DEBUG) System.out.println("sending bytes to output" + s);
            output.writeUTF(s);

        } catch (java.io.IOException e) {
            System.out.println("raised IOException");
        }
    }

    public void run() {

        try {

            DataInputStream input = new DataInputStream(clientSocket.getInputStream());

            output = new DataOutputStream(clientSocket.getOutputStream());
            long time = System.currentTimeMillis();
            output.writeUTF(("Welcome to the server! " + this.serverText + " - " + time + ""));
            if (DEBUG) System.out.println("New client added: " + time);

            while (true) {
                // readLine always returns whether there is something available or not. (Will be null if empty)
                String line = input.readUTF();
                
                if (line != null && line.startsWith("/quit")) {
                    break;
                } else if (line != null && line != "") {
                    if (DEBUG) System.out.println("SERVER: line " + line + " received from client.");
                    if (DEBUG) System.out.flush();
                    messageListener.onMessage(line);
                }

                // this thing goes wild if a print statement is there. therefore, cool down CPU a bit. 
                try {
                    this.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException raised");
                }
            }

            if (DEBUG) System.out.println("Client disconnected.");
            if (DEBUG) System.out.println("*** Bye ***");

            input.close();
            output.close();
            this.clientSocket.close();

            destroyListener.onDestroy();

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}