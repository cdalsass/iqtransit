package com.iqtransit.socketio;
/*  
javac -d . -classpath .:../working/socket.io-java-client/jar/socketio.jar BasicExample.java
java -classpath .:../working/socket.io-java-client/jar/socketio.jar basic.BasicExample
*/

/*
 * socket.io-java-client Test.java
 *
 * Copyright (c) 2012, Enno Boland
 * socket.io-java-client is a implementation of the socket.io protocol in Java.
 * 
 * See LICENSE file for more information
 */
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.json.JSONException;
import org.json.JSONObject;

public class SocketIOListener extends Thread implements IOCallback  {

	private final Object lock = new Object();
	private SocketIO socket;
	private boolean firstConnectionSuccessful = false;
	protected String host = "";
	protected int port = 0;

	public SocketIOListener(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * @param args
	 */
	
	/* Using my own test for whether connection is available. Although it may be possible to use socket.isConnected() for this, I chose to use my own variable because, although you don't know immediately that disconnect occurred, you will get a callback when socket times out which is about 60 seconds. */

	public static boolean isConnectionEstablished = false;

	public  boolean isConnectionEstablished() {
		synchronized (lock) {
			return isConnectionEstablished;
		}
	}

	public  void setConnectionEstablished(boolean isEstablished) {
		synchronized (lock) {
			isConnectionEstablished = isEstablished;
		}
	}

	public void run() {

		synchronized(this) {

			while (isConnectionEstablished() == false) {
				
				try {
					System.out.println("Create new connection to " + "http://" + host + ":" + new Integer(port).toString() + "/");

					socket = new SocketIO();
					socket.connect("http://" + host + ":" + new Integer(port).toString() + "/", this);
					
					Thread.sleep(2000);

					while (isConnectionEstablished()) {
						System.out.println("now monitoring connection from thread " + Thread.currentThread().getId() + " with status of connection = " + socket.isConnected());
						Thread.sleep(2000);
					}

				} catch (Exception e) {
						System.out.println("Caught exception while monitoring" + e.toString());
				}
			}
		}	
	}

	
	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
		try {
			System.out.println("Server said:" + json.toString(2));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(String data, IOAcknowledge ack) {
		System.out.println("Server said: " + data);
	}

	@Override
	public void onError(SocketIOException socketIOException) {
		System.out.println("an Error occured in thread " + Thread.currentThread().getId());
		socketIOException.printStackTrace();
		setConnectionEstablished(false);
	}

	@Override
	public void onDisconnect() {
		System.out.println("Connection terminated.");
		setConnectionEstablished(false);
	}

	@Override
	public void onConnect() {
		System.out.println("Connection established " +Thread.currentThread().getId());
		setConnectionEstablished(true);
	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		System.out.println("Server triggered event '" + event + "' from thread " + Thread.currentThread().getId());
	}
}
