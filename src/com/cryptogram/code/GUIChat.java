package com.cryptogram.code;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class GUIChat extends JFrame implements Runnable{
	
	private static final long serialVersionUID = 1L;

	private boolean alive = true;
	private String username = null;
	private String myUsername = null;
	private Socket socket = null;
	
	private BufferedReader in = null;
	private BufferedWriter out = null;
	
	public GUIChat(Socket socket) {
		super();
		
		this.socket = socket;
		this.myUsername = User.instance.getUsername();
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			username = in.readLine().trim();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "It's impossible to enstablish connection with the user", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public GUIChat(String username, InetSocketAddress connectTo) {
		super();
		
		this.username = username;
		this.myUsername = User.instance.getUsername();
		
		try {
			socket = new Socket(connectTo.getAddress(), connectTo.getPort());
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			out.write(myUsername + "\n"); out.flush();
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "It's impossible to enstablish connection with the user", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void stop() {
		alive = false;
	}

	public void close() {
		try {
			if (socket != null) socket.close();
			this.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		if(socket == null) return;
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stop();
			}
		});
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(400, 500));
		setResizable(false);
		
		JPanel p = new JPanel();
		p.setBackground(Color.WHITE);
		getContentPane().add(p);

		SpringLayout l = new SpringLayout();
		p.setLayout(l);
		
		JLabel lblUsername = new JLabel("Connected to: " + username);
			l.putConstraint(SpringLayout.NORTH, lblUsername, 10, SpringLayout.NORTH, p);
			l.putConstraint(SpringLayout.WEST, lblUsername, 10, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, lblUsername, -10, SpringLayout.EAST, p);
		p.add(lblUsername);
		
		JTextArea areaOut = new JTextArea();
		areaOut.setEditable(false);
		areaOut.setLineWrap(true);
		
		JScrollPane outPane = new JScrollPane(areaOut);
			l.putConstraint(SpringLayout.NORTH, outPane, 10, SpringLayout.SOUTH, lblUsername);
			l.putConstraint(SpringLayout.WEST, outPane, 10, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, outPane, -10, SpringLayout.EAST, p);
		p.add(outPane);
		
        JTextField fieldIn = new JTextField();
	        l.putConstraint(SpringLayout.SOUTH, outPane, -10, SpringLayout.NORTH, fieldIn);
	        l.putConstraint(SpringLayout.WEST,  fieldIn, 10, SpringLayout.WEST, p);
	        l.putConstraint(SpringLayout.SOUTH, fieldIn, -10, SpringLayout.SOUTH, p);
        p.add(fieldIn);
        
        JButton send = new JButton("Send");
	        l.putConstraint(SpringLayout.NORTH, send, 10, SpringLayout.SOUTH, outPane);
	        l.putConstraint(SpringLayout.EAST,  fieldIn, -10, SpringLayout.WEST, send);
	        l.putConstraint(SpringLayout.EAST,  send, -10, SpringLayout.EAST, p);
	        l.putConstraint(SpringLayout.SOUTH, send, -10, SpringLayout.SOUTH, p);
        p.add(send);
        
		pack();
		setVisible(true);
		
		send.addActionListener(listener ->{
			if(out == null || socket.isClosed()) {
				JOptionPane.showMessageDialog(this, "Impossible to deliver the message!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			String msg = fieldIn.getText().trim();
			fieldIn.setText("");
			
			if(!msg.equals(""))
				try {
					out.write(msg + "\n");
					out.flush();
					areaOut.append("\n" + myUsername + ": " + msg);
					
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Impossible to deliver the message!", "Error", JOptionPane.ERROR_MESSAGE);
				}
		});
		
		while(alive) {
			try {
				String readed = in.readLine();
				
				if(readed == null) stop();
				else {
					areaOut.append("\n" + username + ": " + readed);
				}
			} catch (IOException e) {
			}
		}
		
		close();
	}
}
