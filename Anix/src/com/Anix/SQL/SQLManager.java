package com.Anix.SQL;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLManager implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Connection con = null;
	
	public SQLManager() {
		
	}
	
	public void connectToSQL(String connectionString) {
		if(this.con == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			
			try {
				con = DriverManager.getConnection(connectionString);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(con != null) {
				System.out.println("Successfully connected to my SQL!");
				
				try {
					con.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String encryptString(String password) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
		
		return bytesToHex(encodedhash);
	}
	
	private String bytesToHex(byte[] hash) {
	    StringBuilder hexString = new StringBuilder(2 * hash.length);
	    for (int i = 0; i < hash.length; i++) {
	        String hex = Integer.toHexString(0xff & hash[i]);
	        
	        if(hex.length() == 1) {
	            hexString.append('0');
	        }
	        
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
	
	/**
	 * 
	 * @param from - You're email
	 * @param to
	 * @param subject
	 * @param body
	 * @param emailPassword - This won't be stored anywhere.
	 * @return
	 */
	public String sendEmail(String from, String to, String subject, String body, String emailPassword) {
		/*// Assuming you are sending email from localhost

		// Get a Properties object
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", "localhost");
		
		// Get the default Session object.
		Session session = Session.getDefaultInstance(props, 
				new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, emailPassword);
			}});
		
		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(body);

			// Send message
			Transport.send(message);
			
			return "0";
		} catch (MessagingException mex) {
			return mex.getMessage();
		}*/
		
		return "";
	}
	
	public boolean valueExists(String ID, String table, String value) {
		if(this.con == null) {
			return false;
		}
		
		PreparedStatement ps;

		try {
			ps = con.prepareStatement("SELECT " + ID + " FROM " + table + " WHERE " + ID + " = ?");
			ps.setString(1, value);
			
			ResultSet result = ps.executeQuery();

			while(result.next()) {
				return true;
			}

			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public String getValue(String id, String where, String value, String table) {
		if(this.con == null) {
			return "[ERORR] #203 No connection was made";
		}
		
		boolean whereExists = valueExists(where, table, value);
		
		if(whereExists) {
			PreparedStatement ps = null;
			
			try {
				ps = con.prepareStatement("SELECT " + id + " FROM " + table + " WHERE " + where + " = ?");
				ps.setString(1, value);
				
				ResultSet result = ps.executeQuery();
				ResultSetMetaData resultSetMetaData = result.getMetaData();
				final int columnCount = resultSetMetaData.getColumnCount();
				
				String returnValue = "";
				
				while(result.next()) {
				    for (int i = 1; i <= columnCount; i++) {
				        returnValue += result.getString(i) + " ";
				    }
				}
				
				if(returnValue.length() > 0) {
					return returnValue;
				}
				
				return "[Error] #115: Couldn't find any results!";
			} catch (SQLException e) {
				e.printStackTrace();
				return e.getMessage();
			}
		}

		return "[Erorr] #104 Couldn't get value because 'WHERE' value doesn't exists!";
	}
	
	public String setValue(String ID, String value, String table) {
		if(this.con == null) {
			return "[ERORR] #203 No connection was made";
		}
		
		boolean valueExists = valueExists(ID, table, value);

		if(!valueExists) {
			try {
				PreparedStatement ps = con.prepareStatement("INSERT INTO " +  table + "(" + ID + ") VALUES(?)");
				ps.setString(1, value);
				ps.execute();

				return "0";
			} catch (SQLException e) {
				return e.getMessage();
			}
		}

		return "[Error] #201 Value already exists!";
	}
	
	public String setValues(String table, String[] IDs, String[] values) {
		if(this.con == null) {
			return "[ERORR] #203 No connection was made";
		}
		
		if(IDs.length != values.length)
			return "[ERROR] #160 Cannot set values because IDs array size isn't equal to the values size";
		
		String returnString = "";
		
		try (Statement query = con.createStatement()) {
			boolean exists = false;
			
			for(int i = 0; i < IDs.length; i++) {
				if(valueExists(IDs[i], table, values[i])) {
					exists = true;
					
					returnString += "[ERROR] An id of " + IDs[i] + " inside " + table + " table already exist\n";
				}
			}
			
			if(exists)
				return returnString;
			
			try {
				String[] values2 = new String[IDs.length];
				String ids = "", values3 = "";
				
				for(int i = 0; i < IDs.length; i++) {
					ids += IDs[i];
					values3 += "?";
					String value = values[i];
					
					if(i != IDs.length - 1) {
						ids += ",";
						values3 += ",";
					}
					
					values2[i] = value;
				}
				
				PreparedStatement ps = con.prepareStatement("INSERT INTO " +  table + "(" + ids + ") VALUES(" + values3 + ")");
				
				for(int i = 1; i <= IDs.length; i++) {
					ps.setString(i, values2[i-1]);
				}
				
				ps.execute();
				
				return "0";
			} catch (SQLException e) {
				return e.getMessage();
			}			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return e1.getMessage();
		}
	}
	
	public String updateValue(String table, String ID, String setValue, String where, String whereValue) {
		if(this.con == null) {
			return "[ERORR] #203 No connection was made";
		}
		
		boolean valueExists = valueExists(ID, table, setValue);
		
		if(!valueExists) {
			boolean whereExists = valueExists(where, table, whereValue);
			
			//TODO: Check if the "WHERE" column exists.
			
			if(whereExists) {
				try {
					PreparedStatement ps = con.prepareStatement("UPDATE " + table + " SET " + ID + " = ? WHERE " + where + " = ?");
					
					ps.setString(1, setValue);
					ps.setString(2, whereValue);
					
					return "0 " + ps.executeUpdate();
				} catch (SQLException e) {
					return e.getMessage();
				}
			}
			
			return "[ERROR] #267 The 'WHERE' value doesn't exists!";
		}

		return "[Error] #201 ID value already exists!";
	}
	
	public Connection getConnection() {
		return con;
	}
}
