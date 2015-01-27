package org.blockjump.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.blockjump.server.log.Log;
import org.blockjump.server.log.MessageState;
import org.blockjump.server.objects.Connection;
import org.blockjump.server.packets.PacketHandler;

public class Server {
	
	private ServerSocket serverSocket;
	private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<Connection>();
	public static ServerState state;	
	public static int connectionCount = 0;
	public final static boolean DEBUG = false;
	public final static int PACKET_CAPACITY = 512;

	public Server(int i) throws IOException, SQLException {
		Log.log("Server started on port " + i + ".", MessageState.ENGINE);
		serverSocket = new ServerSocket(i);
		PacketHandler packetHandler = new PacketHandler();
		new Thread(packetHandler).start();
		Processing proc = new Processing(connections, packetHandler);
		new Thread(proc).start();
		listen();
	}
	
	public void listen() throws IOException {
		long timer = System.currentTimeMillis();
		Log.log("Starting to listen for connections.", MessageState.ENGINE);
		while(state == ServerState.RUNNING) {
			if(System.currentTimeMillis() - timer > 10000) {
				Log.log("*************************************\nCurrently there are " + connectionCount + " connections!\n*************************************", MessageState.ENGINE);
				timer = System.currentTimeMillis();
			}
			try {
				Socket socket = serverSocket.accept();
				socket.setSoTimeout(500);
				connectionCount++;
				connections.add(new Connection(socket, connectionCount, socket.getInputStream(), socket.getOutputStream()));
				Log.log("Connection " + socket.getInetAddress() + " on port " + socket.getPort() + " connected.", MessageState.MESSAGE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		serverSocket.close();
		Log.log("Server ended...", MessageState.ENGINE);
	}

	public static void main(String[] args) throws SQLException {
		try {
			state = ServerState.RUNNING;
			new Server(49593);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
