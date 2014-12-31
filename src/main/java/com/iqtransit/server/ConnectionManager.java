package com.iqtransit.server;
import java.net.Socket;

public interface ConnectionManager {

	public void handleConnection(Socket sock);
	public void sendMessage(String message);

}