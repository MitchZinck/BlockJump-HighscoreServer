package org.blockjump.server.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Script {
	
	private static ScriptEngine engine;
	
	public static Invocable getInvocable(String path) {
		try {
			engine = null;
			File sf = new File(path);
			if (!sf.exists()) {
				throw new FileNotFoundException(path);
			}
			engine = new ScriptEngineManager().getEngineByName("JavaScript");
			engine.eval(new FileReader(path));
			return (Invocable) engine;
		} catch (Exception e) {
			System.out.println("Failed to invoke script: "+path);
			e.printStackTrace();
			return null;
		}
	}
	
}
