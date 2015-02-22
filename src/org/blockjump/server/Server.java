package org.blockjump.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Timer;
import java.util.concurrent.Executors;

import org.blockjump.server.log.Log;
import org.blockjump.server.log.MessageState;
import org.blockjump.server.mysql.SQLManager;
import org.blockjump.server.packets.PacketHandler;
import org.blockjump.server.scripts.HighscoreScript;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Server {
	
	private PacketHandler packetHandler;
	
	public static ServerState state;	
	public static boolean DEBUG;
	public static int connectionCount = 0;
	public final static int PACKET_CAPACITY = 512;
	private Timer service;
	private SQLManager sqlManager;

	public Server(int i) throws IOException, SQLException {
		try {
			sqlManager = new SQLManager("root", password());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		DEBUG = debug();
		packetHandler = new PacketHandler(sqlManager);
		new Thread(packetHandler).start();
		
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(factory);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				return Channels.pipeline(new ServerHandler(packetHandler));
			}
		});

		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.bind(new InetSocketAddress(i));
		
		Log.log("Server started on port " + i + ".", MessageState.ENGINE);	
		
		new HighscoreScript(sqlManager);
	}	
	
	public String password() {
		String pass = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("password.txt"));
			pass = br.readLine();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return pass;
	}	
	
	public boolean debug() {
		String debug = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("password.txt"));
			br.readLine();
			debug = br.readLine();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if(debug.equals("true")) {
			return true;
		} else {
			return false;
		}
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
