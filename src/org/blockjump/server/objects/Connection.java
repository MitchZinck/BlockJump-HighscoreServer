package org.blockjump.server.objects;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.blockjump.server.packets.PacketBuffer;

public class Connection {
	
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private long userId;

	public Connection(Socket socket, long userId, InputStream in, OutputStream out) {
		this.socket = socket;
		this.userId = userId;
		this.in = in;
		this.out = out;
	}	

	public Socket getSocket() {
		return socket;
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
}
