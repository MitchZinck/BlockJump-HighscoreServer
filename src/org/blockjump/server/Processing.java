package org.blockjump.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.blockjump.server.objects.Connection;
import org.blockjump.server.packets.PacketBuffer;
import org.blockjump.server.packets.PacketHandler;

public class Processing implements Runnable {
	
	private CopyOnWriteArrayList<Connection> connections;
	private InputStream in;
	private OutputStream out;
	private PacketBuffer readBuffer;
	private static long timer = 0;
	private PacketHandler packetHandler;
	private ArrayList<Connection> toRemove = new ArrayList<Connection>();
	
	public Processing(CopyOnWriteArrayList<Connection> connections) throws SQLException {
		this.connections = connections;
		readBuffer = new PacketBuffer(128);
		packetHandler = new PacketHandler(this);
	}

	@Override
	public void run() { //Add error logs
		while(Server.state == ServerState.RUNNING) {
			timer = System.currentTimeMillis();
			
			if(connections.size() > 0) {
				Iterator<Connection> it = connections.iterator();
				while(it.hasNext()) {
					Connection connection = it.next();
					try {
						in = connection.getSocket().getInputStream();
						out = connection.getSocket().getOutputStream();
						readData(connection.getSocket());
					} catch (SocketTimeoutException s) {
						System.out.println("Read timeout for user: " + connection.getSocket().getInetAddress());
					} catch (SocketException s) {
						System.out.println("Connection removed for user: " + connection.getSocket().getInetAddress());
						toRemove.add(connection);
						Server.connectionCount--;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				connections.removeAll(toRemove);
			}
			
			if(System.currentTimeMillis() - timer < 1000) {
				try {
					Thread.sleep(1000 - (System.currentTimeMillis() - timer));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("NOTIFICATION: LOOP HAS EXCEEDED 1 SECOND TO PROCESS.");
			}
		}
	}
	
	public boolean read() {
		return true;
	}
	
	public void write(PacketBuffer packet) throws IOException {
		out.write(packet.getBuffer());
	}
	
	public void readData(Socket socket) throws IOException {
		int packetSize = in.read();
		in.mark(packetSize);
		byte[] buffer = new byte[packetSize];
		in.read(buffer, 0, buffer.length);
		readBuffer.setBuffer(buffer);
		packetHandler.handle(readBuffer, socket);
	}
	
}
