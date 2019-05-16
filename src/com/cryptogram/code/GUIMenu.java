package com.cryptogram.code;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetSocketAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

public class GUIMenu extends JFrame implements Runnable{
	private static final long serialVersionUID = 1L;

	private User u = null;
	private P2PServer server;
	
	public GUIMenu(int port) {
		super();
		
		this.u = User.instance;
		
		server = new P2PServer(port);
		(new Thread(server)).start();
		
		/*Closing operation*/
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(server != null) server.stop();
				if(u != null) u.disconnect();
			}
		});
	}
	
	public void run() {
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(250, 160));
		setResizable(false);
		
		JPanel p = new JPanel();
		p.setBackground(Color.WHITE);
		getContentPane().add(p);

		SpringLayout l = new SpringLayout();
		p.setLayout(l);

		JLabel lblUsername = new JLabel("Provide user to search:");
			l.putConstraint(SpringLayout.NORTH, lblUsername, 10, SpringLayout.NORTH, p);
			l.putConstraint(SpringLayout.WEST, lblUsername, 10, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, lblUsername, -10, SpringLayout.EAST, p);
			lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		p.add(lblUsername);
		
		JTextField txtUsername = new JTextField();
			l.putConstraint(SpringLayout.NORTH, txtUsername, 5, SpringLayout.SOUTH, lblUsername);
			l.putConstraint(SpringLayout.WEST, txtUsername, 15, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, txtUsername, -15, SpringLayout.EAST, p);
			txtUsername.setHorizontalAlignment(SwingConstants.CENTER);
		p.add(txtUsername);
		
		JButton btnSearch = new JButton("Search");
			btnSearch.setBackground(Color.WHITE);
			btnSearch.setFocusPainted(false);
			btnSearch.setContentAreaFilled(false);
			l.putConstraint(SpringLayout.NORTH, btnSearch, 10, SpringLayout.SOUTH, txtUsername);
			l.putConstraint(SpringLayout.WEST, btnSearch, 15, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, btnSearch, -15, SpringLayout.EAST, p);
		p.add(btnSearch);
		
		JButton btnDisconnect = new JButton("Disconnect");
			btnDisconnect.setBackground(Color.WHITE);
			btnDisconnect.setFocusPainted(false);
			btnDisconnect.setContentAreaFilled(false);
			l.putConstraint(SpringLayout.NORTH, btnDisconnect, 10, SpringLayout.SOUTH, btnSearch);
			l.putConstraint(SpringLayout.WEST, btnDisconnect, 15, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, btnDisconnect, -15, SpringLayout.EAST, p);
			l.putConstraint(SpringLayout.SOUTH, btnDisconnect, -10, SpringLayout.SOUTH, p);
		p.add(btnDisconnect);
		
		btnDisconnect.addActionListener(listener -> {
			processWindowEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
		});
		
		btnSearch.addActionListener(listener -> {
			String queryUsername = txtUsername.getText().trim();
			
			if(queryUsername == null || queryUsername.equals("")) {
				JOptionPane.showMessageDialog(this, "Provide a valid username!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			InetSocketAddress userAddress = u.search(queryUsername);
			if(userAddress == null) {
				JOptionPane.showMessageDialog(this, "No user is connected with this username!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			txtUsername.setText("");
			
			(new Thread(new GUIChat(queryUsername, userAddress))).start();
		});
		
		pack();
		setVisible(true);
	}
	
	
}
