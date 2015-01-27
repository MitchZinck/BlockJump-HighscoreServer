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

import org.blockjump.server.log.Log;
import org.blockjump.server.log.MessageState;
import org.blockjump.server.objects.Connection;
import org.blockjump.server.packets.PacketBuffer;
import org.blockjump.server.packets.PacketHandler;

public class Processing implements Runnable {
	
	private CopyOnWriteArrayList<Connection> connections;
	private PacketHandler packetHandler;
	private WorkerThread workThread;
	private long loopTime;
	private static long timer = 0;
	
	public Processing(CopyOnWriteArrayList<Connection> connections, PacketHandler packetHandler) throws SQLException {
		this.connections = connections;
		this.packetHandler = packetHandler;
		workThread = new WorkerThread(connections);
		new Thread(workThread).start();
		loopTime = System.currentTimeMillis();
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
						readData(connection);
					} catch (SocketTimeoutException s) {
						Log.log("Read timeout for user: " + connection.getUserId(), MessageState.MESSAGE);
					} catch (SocketException s) {
						Log.log("Connection removed for user: " + connection.getUserId(), MessageState.MESSAGE);
						workThread.getRemove().add(connection);
						Server.connectionCount--;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			if(System.currentTimeMillis() - loopTime > 10000) {
				loopTime = System.currentTimeMillis();
				long engineUsage = (loopTime - timer < 1000) ? (loopTime - timer) / 10 : 100;
				Log.log("Engine Usage: " + engineUsage + "%", MessageState.ENGINE);
			}
			if(System.currentTimeMillis() - timer < 1000) {
				try {
					Thread.sleep(1000 - (System.currentTimeMillis() - timer));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Log.log("LOOP HAS EXCEEDED 1 SECOND TO PROCESS.", MessageState.MESSAGE);
			}
		}
	}
	
	public boolean read() {
		return true;
	}

	public void readData(Connection c) throws IOException {
		PacketBuffer readBuffer = new PacketBuffer(Server.PACKET_CAPACITY);
		int packetSize = c.getIn().read();
		c.getIn().mark(packetSize);
		byte[] buffer = new byte[packetSize];
		c.getIn().read(buffer, 0, buffer.length);
		readBuffer.setBuffer(buffer);
		c.setPacket(readBuffer);
		packetHandler.addProcess(c);
	}
	
}
