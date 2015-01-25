package org.blockjump.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.blockjump.server.objects.User;
import org.blockjump.server.packets.PacketBuffer;

public class SQLManager {
	
	private Connection connection;
	private PreparedStatement statement;
	private ResultSet resultSet;
	private String url,
				   user,
				   password;
	
	public SQLManager(String user, String password) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		this.url = "jdbc:mysql://127.0.0.1:3306/highscores";
		this.user = user;
		this.password = password;
		connection = DriverManager.getConnection(url, user, password);
	}
	
	public void addHighscore(String name, String email, long score) {
        System.out.println("Adding highscore for user " + name + " with a score of " + score + "....");
        
		try {
			if(connection.isClosed()) {
				connection = DriverManager.getConnection(url, user, password);
			}
			
		    statement = connection.prepareStatement("INSERT INTO highscores.USERS(username, email, score) VALUES(?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setLong(3, score);
            statement.executeUpdate();
            
            System.out.println("Successfully added " + name + " with a score of " + score + "!");

		} catch (SQLException ex) {
			System.out.println("User: " + user + " closed on error \"SQLEXCEPTION\" for the following reasons: " + ex.getErrorCode());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (SQLException ex) {
				System.out.println("Lol this would never happen.");
			}
		}
	}

	public ArrayList<User> getHighscore(String name) {
        ArrayList<User> userList = new ArrayList<User>();
		
		System.out.println("Returning highscore for user " + name + ".");
	        
		try {
			if(connection.isClosed()) {
				connection = DriverManager.getConnection(url, user, password);
			}
			
		    statement = connection.prepareStatement("SELECT * FROM highscores.users WHERE `username` = ?");
            statement.setString(1, name);
            resultSet = statement.executeQuery();            
            
            while(resultSet.next()) {
            	userList.add(new User(resultSet.getString("username"), resultSet.getString("email"), resultSet.getLong("score"), resultSet.getLong("user_id")));
            }
            
            System.out.println("Successfully completed query of " + name + "!");

		} catch (SQLException ex) {
			System.out.println("User: " + user + " closed on error \"SQLEXCEPTION\" for the following reasons: " + ex.getErrorCode());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (SQLException ex) {
				System.out.println("Lol this would never happen.");
			}
		}
		
		return userList;
	}
	
}
