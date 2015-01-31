package org.blockjump.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.Executors;

import org.blockjump.server.log.Log;
import org.blockjump.server.log.MessageState;
import org.blockjump.server.objects.Connection;
import org.blockjump.server.packets.PacketHandler;
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

	public Server(int i) throws IOException, SQLException {
		DEBUG = debug();
		packetHandler = new PacketHandler();
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
