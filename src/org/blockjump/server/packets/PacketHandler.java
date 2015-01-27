package org.blockjump.server.packets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.script.Invocable;

import org.blockjump.server.Processing;
import org.blockjump.server.Server;
import org.blockjump.server.ServerState;
import org.blockjump.server.mysql.SQLManager;
import org.blockjump.server.objects.Connection;
import org.blockjump.server.objects.User;
import org.blockjump.server.scripts.HighscoreScript;
import org.blockjump.server.scripts.Script;

public class PacketHandler implements Runnable{
	
	private SQLManager sqlManager;
	private CopyOnWriteArrayList<Connection> processList = new CopyOnWriteArrayList<Connection>();
	
	public PacketHandler() throws SQLException {
		try {
			sqlManager = new SQLManager("root", password()); //change so that the password is read on load.
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void addProcess(Connection c) {
		processList.add(c);
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
	
	public void write(PacketBuffer packet, OutputStream out) throws IOException {
		out.write(packet.getBuffer());
	}	
	
	public void handle(Connection c) throws IOException {
		System.out.println(c.getPacket().getByte());
		
		int opCode = 1;
		
		switch(opCode) {
			case 0: //Get highscore of given username back
				ArrayList<User> userList = sqlManager.getHighscore(c.getPacket().getString());
				PacketBuffer sendPacket = new PacketBuffer(Server.PACKET_CAPACITY);
				sendPacket.setOpcode(10);
				sendPacket.addInt(userList.size());
				write(sendPacket, c.getOut());
				
				for(User user : userList) {
					sendPacket = new PacketBuffer(Server.PACKET_CAPACITY);
					sendPacket.setOpcode(11);
					sendPacket.addString(user.getName());
					sendPacket.addString(user.getEmail());
					sendPacket.addLong(user.getScore());
					sendPacket.addLong(user.getUserId());
					write(sendPacket, c.getOut());
				}
			break;
			
			case 2:
				sqlManager.addHighscore(c.getPacket().getString(), c.getPacket().getString(), c.getPacket().getLong());
			break;
			
			case 53: 
				HighscoreScript ex;
				Invocable tmp = Script.getInvocable("highscorescript/highscore.js");
				if (tmp != null) {
					ex = tmp.getInterface(HighscoreScript.class);
					ex.addHighscore("name", "example@example.co", 1242);
				} else {
					//Log.log("Failed to load+invoke test script!");
				}
			break;
				
		}
	}

	@Override
	public void run() {
		while(Server.state == ServerState.RUNNING) {
			if(!processList.isEmpty()) {
				for(Connection c : processList) {
					try {
						handle(c);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				processList.clear();
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}