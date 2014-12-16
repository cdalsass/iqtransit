package com.iqtransit.server;
import java.net.Socket;

public class HttpConnectionManager implements ConnectionManager {

	public void HandleConnection(Socket sock) {
		new Thread(new HttpWorker(sock, "HTTP Server") ).start(); 
	}

}