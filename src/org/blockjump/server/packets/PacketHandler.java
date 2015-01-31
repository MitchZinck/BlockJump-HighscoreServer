package org.blockjump.server.packets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.script.Invocable;

import org.blockjump.server.Server;
import org.blockjump.server.ServerState;
import org.blockjump.server.WorkerThread;
import org.blockjump.server.log.Log;
import org.blockjump.server.log.MessageState;
import org.blockjump.server.mysql.SQLManager;
import org.blockjump.server.objects.Connection;
import org.blockjump.server.objects.User;
import org.blockjump.server.scripts.HighscoreScript;
import org.blockjump.server.scripts.Script;

public class PacketHandler implements Runnable{
	
	private SQLManager sqlManager;
	private CopyOnWriteArrayList<Connection> processList = new CopyOnWriteArrayList<Connection>();
	private WorkerThread wt;
	
	public PacketHandler() throws SQLException {
		try {
			sqlManager = new SQLManager("root", password()); //change so that the password is read on load.
			wt = new WorkerThread(processList);
			new Thread(wt).start();
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
		int opCode = Character.getNumericValue(c.getBuffer().charAt(0));
		
		switch(opCode) {
			case 0: //Get highscore of given username back				
				ArrayList<User> userList = sqlManager.getHighscore(c.getBuffer().substring(1));
				
				String hello = "1" + userList.size();				
				c.getChannel().write(hello);
				
				for(User user : userList) {
					String s = "2";
					s += "'" + user.getName();
					s += "'" + user.getEmail();
					s += "'" + user.getScore();
					s += "'" + user.getUserId();

					c.getChannel().write(s);
				}
				Log.log("Successfully returned highscores of " + c.getBuffer().substring(1) + " to: " + c.getChannel().getRemoteAddress(), MessageState.ENGINE);
			break;
			
			case 2:
				String all[] = c.getBuffer().substring(1).split("'");
				String name = all[0];
				String email = all[1];
				long score = Long.parseLong(all[2]);
				sqlManager.addHighscore(name, email, score);
				Log.log("Successfully added highscore of " + name + " from: " + c.getChannel().getRemoteAddress(), MessageState.ENGINE);
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
						wt.getToRemove().add(c);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}