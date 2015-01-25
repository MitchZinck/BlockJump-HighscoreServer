package org.blockjump.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.blockjump.server.objects.Connection;

public class Server {
	
	private ServerSocket serverSocket;
	private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<Connection>();
	public static ServerState state;	
	public static int connectionCount = 0;

	public Server(int i) throws IOException, SQLException {
		System.out.println("Server started on port " + i + ".");
		serverSocket = new ServerSocket(i);
		Processing proc = new Processing(connections);
		new Thread(proc).start();
		listen();
	}
	
	public void listen() throws IOException {
		System.out.println("Starting to listen for connections.");
		while(state == ServerState.RUNNING) {
			if(connectionCount % 10 == 0) {
				System.out.println("*************************************\nCurrently there are " + connectionCount + " connections!\n*************************************");
			}
			try {
				Socket socket = serverSocket.accept();
				socket.setSoTimeout(500);
				connectionCount++;
				connections.add(new Connection(socket, connectionCount));
				System.out.println("Connection " + socket.getInetAddress() + " on port " + socket.getPort() + " connected.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		serverSocket.close();
		System.out.println("Server ended...");
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
