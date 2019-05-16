package com.cryptogram.code;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class P2PServer implements Runnable{

	private int port = 0;
	private ServerSocket con = null;
	private boolean listen = true;
	private ArrayList<GUIChat> queue = new ArrayList<GUIChat>();
	
	public P2PServer(int port) {
		this.port = port;
	}
	
	public void stop() {
		listen = false;
	}

	public void close() {
		if(con!=null && !con.isClosed()) {
			try { con.close(); System.out.println("Closing server!"); }
			catch (IOException e) {System.err.println("Error while closing server instance"); }
		}
		
		for(GUIChat c : queue) 
			if(c != null) 
				c.stop();
	}
	
	public void run() {
		
		try {
			con = new ServerSocket(port);

			while(listen) {
				try {
					GUIChat chat = new GUIChat(con.accept());
					(new Thread(chat)).start();
					queue.add(chat);
				}
				catch(SocketException e) {
				}
			}
		} catch (IOException e) { e.printStackTrace(); }
		
		close();
	}
}
