package com.cryptogram.code;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class User {
	private String serverURL = null;
	private String loginURL = null;
	private String searchURL = null;
	private String logoutURL = null;
	
	private String username;
	private int incomingPort;
	private String token = null;
	public static User instance = null;
	
	public User(String username, int incomingPort, String serverURL) {
		this.serverURL = serverURL;
		this.username = username;
		this.incomingPort = incomingPort;
		
		loginURL = "http://" + this.serverURL + "/login.php";
		searchURL = "http://" + this.serverURL + "/search.php";
		logoutURL = "http://" + this.serverURL + "/logout.php";
		
		instance = this;
	}
	
	/*
	 * -1 for a generic IO error
	 * 0 if connection has been enstablished and token has been readed
	 * 1 if the user already exists
	 * 2 if there has been an internal server error
	 * */
	public int connect(){
		
		if(token != null) return 0;

		//preparing post parameters
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("port", ""+incomingPort);
		
		try {
			HttpURLConnection connection = post(loginURL, map);
			
			//checking response code
			int responseCode = connection.getResponseCode();
			
			//if 401 user already exists
			if(responseCode == 401) return 1;
			if(responseCode == 500) return 2;
			if(responseCode != 201) return -1;
			
			String response = readResponse(connection);
			
			//Parsing JSON response
			JSONObject jo = (JSONObject) (new JSONParser().parse(response));
			if(jo.get("token") == null) return -1;
			this.token = (String) jo.get("token");
			
			return 0;
		}
		catch(IOException | ParseException e) {
			return -1;
		}
		
	}
	
	public InetSocketAddress search(String username) {
		
		if(this.token == null) return null;
		
		//preparing post parameters
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("token", this.token);
		
		try {
			HttpURLConnection connection = post(searchURL, map);
			
			//checking response code
			int responseCode = connection.getResponseCode();	
			
			//checking response code
			if(responseCode != 200) return null;
			
			String response = readResponse(connection);
			
			//Parsing JSON response
			JSONObject jo = (JSONObject) (new JSONParser().parse(response));
			if(jo.get(username) == null || jo.get("port") == null) return null;
			
			return (new InetSocketAddress(
					(String) jo.get(username),
					((Long) jo.get("port")).intValue()
					));
		}
		catch(IOException | ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * -1 if not connected
	 * 0 logout succeeded
	 * 1 generic error
	 * */
	public int disconnect() {
		
		if(this.token == null) return -1;
		
		//preparing post parameters
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", this.username);
		map.put("token", this.token);
		
		try {
			HttpURLConnection connection = post(logoutURL, map);
			
			//checking response code
			int responseCode = connection.getResponseCode();	
			
			//checking response code
			if(responseCode != 204) return 1;
			
			return 0;
		}
		catch(IOException e) {
			e.printStackTrace();
			return 1;
		}

	}
	
	private HttpURLConnection post(String queryUrl, HashMap<String, String> map)
								throws IOException {
		
		/*Open connection and set request as POST*/
		URL url = new URL(queryUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		
		/*Build parameters string*/
		StringBuffer parameters = new StringBuffer();
		boolean first = true;
		
		for(String key : map.keySet()) {
			if(!first) 
				parameters.append("&");
			
			parameters.append(key + "=" + map.get(key));
			
			first = false;
		}
		
		/*Sending parameters*/
		connection.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.writeBytes(parameters.toString());
		out.flush();
		out.close();
		
		return connection;
	}
	
	private String readResponse(HttpURLConnection connection) throws IOException {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer response = new StringBuffer();
		
		String inputLine;
		while ((inputLine = in.readLine()) != null) response.append(inputLine);

		in.close();
		
		return response.toString();
	}
	
	/*GETTER*/
	String getServerURL() {return this.serverURL; }
	String getUsername() { return this.username; }
	int getPort() { return this.incomingPort; }
	String getToken() { return this.token; }
	
}
