package com.iqtransit.server;
import java.net.Socket;

public interface ConnectionManager {

	public void HandleConnection(Socket sock);

}