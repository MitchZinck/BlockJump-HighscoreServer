package org.blockjump.server.objects;

import java.net.Socket;

public class Connection {
	
	private Socket socket;
	private long userId;

	public Connection(Socket socket, long userId) {
		this.socket = socket;
		this.userId = userId;
	}	
	
	public Socket getSocket() {
		return socket;
	}
	
}
