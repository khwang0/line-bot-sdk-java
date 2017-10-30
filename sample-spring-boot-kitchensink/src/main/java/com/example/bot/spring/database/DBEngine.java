package com.example.bot.spring.database;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.linecorp.bot.model.event.message.TextMessageContent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBEngine {
	public DBEngine() {
		
	}
	
	public void update(String userID,String entryName,String value) throws Exception{
		Connection connection= getConnection();
		PreparedStatement stmt;
		try {
			get()
		}catch(Exception e) {
			if(e.getMessage().equals("No such entry")) {
				stmt=connection.prepareStatement("INSERT INTO line_user_info VALUES ( ? ,'','','','','','')");
				stmt.setString(1,userID);
				stmt.executeUpdate();
				stmt.close();
			}
			else throw Exception("Wrong Command");
		}
		try {
			stmt = connection.prepareStatement(
					"UPDATE line_user_info SET ? = ? WHERE userid = ?");
			stmt.setString(1, entryName);
			stmt.setString(2, value);
			stmt.setString(3, userID);
			stmt.executeUpdate();
		}catch(Exception e) {
			throw Exception("Wrong Command");
		}
		stmt.close();
		connection.close();
	}
	
	public String get(String userID,String entryName) throw Exception{
		Connection connection= getConnection();
		PreparedStatement stmt;
		ResultSet rs;
		try{
			stmt = connection.prepareStatement(
					"SELECT ? FROM line_user_info WHERE userid = ?");
			stmt.setString(1, entryName);
			stmt.setString(2, userID);
			rs=stmt.executeQuery();
		}catch(Exception){
			throw Exception("Wrong Command");
		}
		if(!rs.next()) throw Exception("No such entry");
		String tmp=rs.getString(1);
		rs.close();
		stmt.close();
		connection.close();
		return tmp;
	}
	
	protected Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}
}
