package com.iqtransit.server;
import java.net.Socket;

public class HttpConnectionManager implements ConnectionManager {

	public void handleConnection(Socket sock) {
		new Thread(new HttpWorker(sock, "HTTP Server") ).start(); 
	}
	public void sendMessage(String message) {
		// not implemented. 
	}

}