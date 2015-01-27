package org.blockjump.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.blockjump.server.log.Log;
import org.blockjump.server.log.MessageState;
import org.blockjump.server.objects.User;

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
        Log.log("Adding highscore for user " + name + " with a score of " + score + "....", MessageState.ENGINE);
        
		try {
			if(connection.isClosed()) {
				connection = DriverManager.getConnection(url, user, password);
			}
			
		    statement = connection.prepareStatement("INSERT INTO highscores.USERS(username, email, score) VALUES(?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setLong(3, score);
            statement.executeUpdate();
            
            Log.log("Successfully added " + name + " with a score of " + score + "!", MessageState.ENGINE);

		} catch (SQLException ex) {
			Log.log("User: " + user + " closed on error \"SQLEXCEPTION\" for the following reasons: " + ex.getErrorCode(), MessageState.ERROR);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (SQLException ex) {
				Log.log("I don't even know what happened sql error: " + ex.getErrorCode(), MessageState.ERROR);
			}
		}
	}

	public ArrayList<User> getHighscore(String name) {
        ArrayList<User> userList = new ArrayList<User>();
		
		Log.log("Returning highscore for user " + name + ".", MessageState.ENGINE);
	        
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
            
            Log.log("Successfully completed query of " + name + "!", MessageState.ENGINE);

		} catch (SQLException ex) {
			Log.log("User: " + user + " closed on error \"SQLEXCEPTION\" for the following reasons: " + ex.getErrorCode(), MessageState.ERROR);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (SQLException ex) {
				Log.log("I don't even know what happened sql error: " + ex.getErrorCode(), MessageState.ERROR);
			}
		}
		
		return userList;
	}
	
}
