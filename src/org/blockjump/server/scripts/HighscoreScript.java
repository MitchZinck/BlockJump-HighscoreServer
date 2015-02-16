package org.blockjump.server.scripts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.blockjump.server.mysql.SQLManager;

public class HighscoreScript extends TimerTask {
	
	private SQLManager sqlManager;
	
	public HighscoreScript(SQLManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Override
	public void run() {
		//Invocable inv = Script.getInvocable("C:\\Users\\mitchell\\workspace\\BlockJump-Server\\src\\org\\blockjump\\server\\scripts\\highscorescript\\highscore.js");
		String text = "[";
		
		HashMap<String, Long> list = sqlManager.updateHighscores();
		
		for(String s : list.keySet()) {
			text += "{\"name\":\"" + s +"\",\"score\":" + list.get(s)+ "},";
		}
		
		text.substring(text.length() - 1, text.length());
		text += "]";
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:\\inetpub\\wwwroot\\highscores.json", false)))) {
			out.print(text);
		} catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
		
//		try {
//			inv.invokeFunction("addHighscore", text);
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ScriptException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("myfile.txt", true)))) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
		    out.println("Highscores update as of " + dateFormat.format(date));
		} catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
}
