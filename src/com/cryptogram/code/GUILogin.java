package com.cryptogram.code;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

public class GUILogin extends JFrame implements Runnable{
	private static final long serialVersionUID = 1L;
	
	public GUILogin() {
		super();
	}

	public void run() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(200, 250));
		setResizable(false);
		
		JPanel p = new JPanel();
		p.setBackground(Color.WHITE);
		getContentPane().add(p);

		SpringLayout l = new SpringLayout();
		p.setLayout(l);
		
		JLabel lblServer = new JLabel("Server directory:");
			l.putConstraint(SpringLayout.NORTH, lblServer, 10, SpringLayout.NORTH, p);
			l.putConstraint(SpringLayout.WEST, lblServer, 10, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, lblServer, -10, SpringLayout.EAST, p);
			lblServer.setHorizontalAlignment(SwingConstants.CENTER);
		p.add(lblServer);
		
		JTextField txtServer = new JTextField();
			l.putConstraint(SpringLayout.NORTH, txtServer, 5, SpringLayout.SOUTH, lblServer);
			l.putConstraint(SpringLayout.WEST, txtServer, 15, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, txtServer, -15, SpringLayout.EAST, p);
			txtServer.setHorizontalAlignment(SwingConstants.CENTER);
		p.add(txtServer);
		
		JLabel lblUsername = new JLabel("Username:");
			l.putConstraint(SpringLayout.NORTH, lblUsername, 10, SpringLayout.SOUTH, txtServer);
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
		
		JLabel lblPort = new JLabel("Port: ");
			l.putConstraint(SpringLayout.NORTH, lblPort, 10, SpringLayout.SOUTH, txtUsername);
			l.putConstraint(SpringLayout.WEST, lblPort, 10, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, lblPort, -10, SpringLayout.EAST, p);
			lblPort.setHorizontalAlignment(SwingConstants.CENTER);
		p.add(lblPort);
		
		JTextField txtPort = new JTextField();
			l.putConstraint(SpringLayout.NORTH, txtPort, 5, SpringLayout.SOUTH, lblPort);
			l.putConstraint(SpringLayout.WEST, txtPort, 15, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, txtPort, -15, SpringLayout.EAST, p);
			txtPort.setHorizontalAlignment(SwingConstants.CENTER);
		p.add(txtPort);
		
		JButton btnConnect = new JButton("Connect");
			btnConnect.setBackground(Color.WHITE);
			btnConnect.setFocusPainted(false);
			btnConnect.setContentAreaFilled(false);
			l.putConstraint(SpringLayout.NORTH, btnConnect, 10, SpringLayout.SOUTH, txtPort);
			l.putConstraint(SpringLayout.WEST, btnConnect, 15, SpringLayout.WEST, p);
			l.putConstraint(SpringLayout.EAST, btnConnect, -15, SpringLayout.EAST, p);
			l.putConstraint(SpringLayout.SOUTH, btnConnect, -10, SpringLayout.SOUTH, p);
		p.add(btnConnect);
			
		btnConnect.addActionListener(listener -> {
			
			String username = txtUsername.getText().trim();
			
			if(username.equals("")) {
				JOptionPane.showMessageDialog(this, "Provide an username!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			String portRaw = txtPort.getText().trim();
			
			if(portRaw.equals("")) {
				JOptionPane.showMessageDialog(this, "Provide a port!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		
			int port = 0;
			
			try {
				port = Integer.parseInt(portRaw);			
			}catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Provide a suitable port!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(!(port > 0 && port < 65535)) {
				JOptionPane.showMessageDialog(this, "Provide a suitable port!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			User u = null;
			
			if(txtServer.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "Provide a suitable server directory!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else u = new User(username, port, txtServer.getText());
				
			int status = u.connect();

			if(status == -1) {
				JOptionPane.showMessageDialog(this, "Error during login process!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(status == 1) {
				JOptionPane.showMessageDialog(this, "User already taken!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(status == 2) {
				JOptionPane.showMessageDialog(this, "Server error!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			/*Closing Login window*/
			this.dispose();
			
			/*Opening menu*/
			(new Thread(new GUIMenu(port))).start();
		});
		
		pack();
		setVisible(true);
	}
	
	
	
}
