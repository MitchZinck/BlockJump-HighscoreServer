package org.blockjump.server;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.blockjump.server.log.Log;
import org.blockjump.server.log.MessageState;
import org.blockjump.server.objects.Connection;

public class WorkerThread implements Runnable {
	
	private ArrayList<Connection> remove = new ArrayList<Connection>();
	private CopyOnWriteArrayList<Connection> connections;
	
	public WorkerThread(CopyOnWriteArrayList<Connection> connections) {
		this.connections = connections;
	}
	
	@Override
	public void run() {
		while(Server.state == ServerState.RUNNING) {
			if(!remove.isEmpty()) {
				connections.removeAll(remove);
				remove.clear();
				Log.log("Removed processes successfully.", MessageState.ENGINE);
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}

	public synchronized ArrayList<Connection> getToRemove() {
		return remove;
	}	
	
}
