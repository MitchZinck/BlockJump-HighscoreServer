package org.blockjump.server.packets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.script.Invocable;

import org.blockjump.server.Processing;
import org.blockjump.server.mysql.SQLManager;
import org.blockjump.server.objects.User;
import org.blockjump.server.scripts.HighscoreScript;
import org.blockjump.server.scripts.Script;

public class PacketHandler {
	
	private SQLManager sqlManager;
	private Processing pr;
	
	public PacketHandler(Processing pr) throws SQLException {
		this.pr = pr;
		
		try {
			sqlManager = new SQLManager("root", password()); //change so that the password is read on load.
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void handle(PacketBuffer packet, Socket socket) throws IOException {
		int opCode = packet.getByte();
		
		switch(opCode) {
			case 0: //Get highscore of given username back
				ArrayList<User> userList = sqlManager.getHighscore(packet.getString());
				PacketBuffer sendPacket = new PacketBuffer(128);
				sendPacket.setOpcode(10);
				sendPacket.addInt(userList.size());
				pr.write(sendPacket);
				
				for(User user : userList) {
					sendPacket = new PacketBuffer(128);
					sendPacket.setOpcode(11);
					sendPacket.addString(user.getName());
					sendPacket.addString(user.getEmail());
					sendPacket.addLong(user.getScore());
					sendPacket.addLong(user.getUserId());
					pr.write(sendPacket);
				}
			break;
			
			case 1:
				sqlManager.addHighscore(packet.getString(), packet.getString(), packet.getLong());
			break;
			
			case 53: 
				HighscoreScript ex;
				Invocable tmp = Script.getInvocable("highscorescript/highscore.js");
				if (tmp != null) {
					ex = tmp.getInterface(HighscoreScript.class);
					ex.addHighscore("name", "ass@ass.co", 1242);
				} else {
					System.out.println("Failed to load+invoke test script!");
				}
			break;
				
		}
	}
	
}