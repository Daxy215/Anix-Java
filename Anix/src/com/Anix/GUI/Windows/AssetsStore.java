package com.Anix.GUI.Windows;

import com.Anix.SQL.SQLManager;

public class AssetsStore {
	//private String currentUser = "";
	
	private SQLManager sql = new SQLManager();
	
	public void init() {
		//sql.connectToSQL("jdbc:mysql://localhost:3306/anix?user=root&password=");
	}
	
	public void update() {
		/*if(currentUser == "") {
			currentUser = "test";
			
			//System.err.println(register(currentUser, "smsmkhaldon@gmail.com", "thisIsAPasswod"));
		} else if(currentUser.equalsIgnoreCase("test")) {
			//System.err.println(login("test", "thisIsAPasswod"));
			
			currentUser = "test2";
		} else if(currentUser.equalsIgnoreCase("test2")) {
			//System.err.println(sql.updateValue("anix", "username", "test2", "email", "smsmkhaldon@gmail.com"));
			
			currentUser = "test3";
		}*/
	}
	
	public String login(String username, String password) {
		if(sql.valueExists("username", "anix", username)) {
			String hash = sql.encryptString(password);
			String query = sql.getValue("password, email", "username", username, "anix");
			
			if(query.split(" ")[0].equalsIgnoreCase(hash)) {
				return "0 " + query;
			} else {
				return "[ERROR] #36 Password is incorrect";
			}
		} else {
			return "[ERROR] #30 Couldn't find a user with the name of " + username;
		}
	}
	
	public String register(String username, String email, String password) {
		return sql.setValues("anix", new String[] {"username", "email", "password"}, new String[] {username, email, sql.encryptString(password)});
	}
}
