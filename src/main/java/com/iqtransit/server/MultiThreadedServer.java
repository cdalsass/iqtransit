/* original code taken from http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html */

package com.iqtransit.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class MultiThreadedServer implements Runnable{

    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread = null;
    protected final ConnectionManager connectionManager; 

    public MultiThreadedServer(int port, ConnectionManager connectionManager){
        this.serverPort = port;
        this.connectionManager = connectionManager;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }

        openServerSocket();

        while(! isStopped()){

            Socket clientSocket = null;

            try {
            
                clientSocket = this.serverSocket.accept();
            
            } catch (IOException e) {

                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }

            System.out.println("called handle connection");
            connectionManager.HandleConnection(clientSocket);
            
        }
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

}