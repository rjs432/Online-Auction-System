package com.cs336.pkg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.io.*;

public class ApplicationDB {
	
	public ApplicationDB(){
		
	}

	public Connection getConnection(){
		
		//Create a connection string
		String connectionUrl = "jdbc:mysql://localhost:3306/project";	// project is the databse created in mysql
		Connection connection = null;
		
		try {
			//Load JDBC driver - the interface standardizing the connection procedure. Look at WEB-INF\lib for a mysql connector jar file, otherwise it fails.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//Create a connection to your DB
			connection = DriverManager.getConnection(connectionUrl,"root", "password");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connection;
		
	}
	
	public void closeConnection(Connection connection){
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public static void main(String[] args) {
		ApplicationDB dao = new ApplicationDB();
		Connection connection = dao.getConnection();
		
		System.out.println(connection);		
		dao.closeConnection(connection);
	}
	
	// givenAccountID / givenPass refers to the credentials that the user submitted on log in form
	public boolean accountExists(String givenAccountID, String givenPassword){
		try {
			// Log In

			//Get the database connection
			Connection con = this.getConnection();

			//Create a SQL statement
			Statement stmt = con.createStatement();

			// Forms sql select query with given account id and password
			String sql = String.format("select account_id, password from account where account_id = '%s' and password = '%s'", givenAccountID, givenPassword);
			
			//Run the query against the DB and retrieves results
			ResultSet rs = stmt.executeQuery(sql);
			
			// Iterates through the returned rows (should only be 1 row) to see if if the account with the correct password exists
			while (rs.next()) {
				if (rs.getString("account_id").equals(givenAccountID) && rs.getString("password").equals(givenPassword)) {
					System.out.println("ACCOUNT EXISTS - Logged In");
					con.close();
					rs.close();
					return true;
				} else {
					break;
				}
			}

			//Close the connection with no account match
			rs.close();
			con.close();
			return false;
								
		} catch (Exception ex) {
			System.out.println(ex);
			//System.out.println("Account Does Not Exist");
			return false;
		}
	}
	
	// givenAccountID / givenPass refers to the credentials that the user submitted on sign up form
	// DOES NOT ACCOUNT FOR CUSTOMER REP ACCOUNT CREATION
	public boolean createAccount(String givenAccountID, String givenPassword){
		try {
			// Sign Up

			//Get the database connection
			Connection con = this.getConnection();

			//Create a SQL statement
			Statement stmt = con.createStatement();

			// Forms sql insert query with given account id and password
			String sql = String.format("insert into account values ('%s', 0, '%s')", givenAccountID, givenPassword);
			
			//Run the query against the DB
			stmt.executeUpdate(sql);

			//Close the connection with no account match
			con.close();
			return true;
								
		} catch (Exception ex) {
			System.out.println(ex);
			return false;
		}
	}

}