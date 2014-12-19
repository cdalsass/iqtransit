package com.iqtransit.server;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class ChatConnectionManager implements ConnectionManager {

    ArrayList <ChatWorker> connections;

    public ChatConnectionManager() {
        connections = new ArrayList < ChatWorker > ();
    }

    public void HandleConnection(Socket sock) {
        
        ChatWorker c = new ChatWorker(sock, "Chat Server");
        /* add chat worker to end of list of connections */
        connections.add(c);
        c.start();

        c.setMessageListener(new ChatWorker.MessageListener() {

            public void onMessage(String a) {

              for (int i = 0; i < connections.size(); i++) {
                ChatWorker element = connections.get(i);
                element.sendMessage(a + "\n");
              }   
            }

        });
       

        /*
         * Clean up. Set the current thread variable to null so that a new client
         * could be accepted by the server.
         */
        /* synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] == this) {
                    threads[i] = null;
                  }
                }
            } */
        /*
         * Close the output stream, close the input stream, close the socket.
         */
    }
}