package org.blockjump.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.blockjump.server.log.Log;
import org.blockjump.server.log.MessageState;
import org.blockjump.server.objects.User;
import org.blockjump.server.scripts.HighscoreScript;

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
	}//SET SQL_SAFE_UPDATES = 0;
	//DELETE FROM highscores.users;
//	ALTER TABLE highscores.users AUTO_INCREMENT = 1;
//	SELECT * FROM highscores.users;
	public ArrayList<TreeMap<String, Long>> updateHighscores() {
		ArrayList<TreeMap<String, Long>> list = new ArrayList<TreeMap<String, Long>>();
		
		try {
			if(connection.isClosed()) {
				connection = DriverManager.getConnection(url, user, password);
			}
			
		    statement = connection.prepareStatement("SELECT * FROM highscores.USERS ORDER BY score DESC LIMIT 10");
		    resultSet = statement.executeQuery();
          
            while(resultSet.next()) {
            	TreeMap<String, Long> map = new TreeMap<String, Long>();
            	map.put(resultSet.getString("username"), resultSet.getLong("score"));
            	list.add(map);
            }

            Log.log("Successfully completed highscore update!", MessageState.MESSAGE);
		} catch (SQLException ex) {
			ex.printStackTrace();
			Log.log("User: " + user + " closed on error \"SQLEXCEPTION\"", MessageState.ERROR);
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
		
		return list;
	}
	
	public void addHighscore(String name, String email, long score) {
        Log.log("Adding highscore for user " + name + " with a score of " + score + "....", MessageState.MESSAGE);

		try {
			if(connection.isClosed()) {
				connection = DriverManager.getConnection(url, user, password);
			}
			
		    statement = connection.prepareStatement("INSERT INTO highscores.USERS(username, email, score) VALUES(?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setLong(3, score);
            statement.executeUpdate();
            
            Log.log("Successfully added " + name + " with a score of " + score + "!", MessageState.MESSAGE);

		} catch (SQLException ex) {
			ex.printStackTrace();
			Log.log("User: " + user + " closed on error \"SQLEXCEPTION\"", MessageState.ERROR);
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
		
		new HighscoreScript(this);
	}

	public ArrayList<User> getHighscore(String name) {
        ArrayList<User> userList = new ArrayList<User>();
		
		Log.log("Returning highscore for user " + name + ".", MessageState.MESSAGE);
	        
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
            
            Log.log("Successfully completed query of " + name + "!", MessageState.MESSAGE);

		} catch (SQLException ex) {
			ex.printStackTrace();
			Log.log("User: " + user + " closed on error \"SQLEXCEPTION\"", MessageState.ERROR);
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
