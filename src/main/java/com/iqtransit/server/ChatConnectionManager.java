package com.iqtransit.server;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class ChatConnectionManager implements ConnectionManager {
    
    ArrayList < ChatWorker > connections;
    public ChatConnectionManager() {
        connections = new ArrayList < ChatWorker > ();
    }

    public void handleConnection(Socket sock) {
        ChatWorker c = new ChatWorker(sock, "Chat Server");
        /* add chat worker to end of list of connections */
        connections.add(c);
        c.start();
        c.setMessageListener(new ChatWorker.MessageListener() {
            public void onMessage(String a) {
                for (int i = 0; i < connections.size(); i++) {
                    ChatWorker element = connections.get(i);
                    System.out.println("sending message to client #" + i + " message: " + a);
                    element.sendMessage(a);
                }
            }
        });
    }

    public void sendMessage(String message) {
        for (int i = 0; i < connections.size(); i++) {
            ChatWorker element = connections.get(i);
            System.out.println("sending message to client #" + i + " message: " + message);
            element.sendMessage(message);
        }
    }
}