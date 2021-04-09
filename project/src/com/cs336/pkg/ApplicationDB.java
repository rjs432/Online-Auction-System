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
		String connectionUrl = "jdbc:mysql://localhost:3306/BuyMe";	// project is the database created in mysql
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
			connection = DriverManager.getConnection(connectionUrl,"root", "687OAI62");
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
			
			if (givenAccountID == null || givenPassword == null) {
				return false;
			}
			
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
			
			if (givenAccountID == null || givenPassword == null) {
				return false;
			}
			
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
	
	//mine
	//checks if the we have reached the end of the auction time that the user set
	public boolean endOfautction (String today, String time, String givenAccountID, int CID ){
		try {
			
		
			//Get the database connection
			Connection con = this.getConnection();

			//Create a SQL statement
			Statement stmt = con.createStatement();

			// Forms sql select query with given time
			String sql = String.format("select account_id, CID, End_date, End_time from Sells where account_id = '%s' and CID = '%d' and End_date = '%s'and End_time = '%s' ", givenAccountID, CID, today, time);
			
			//Run the query against the DB and retrieves results
			ResultSet rs = stmt.executeQuery(sql);
			
			// Iterates through the returned rows (should only be 1 row) to see if if the account with the correct password exists
			while (rs.next()) {
				if (rs.getString("account_id").equals(givenAccountID) && rs.getString("CID").equals(String.valueOf(CID)) && rs.getString("End_date").equals(today) && rs.getString("End_time").equals(time)) {
					//System.out.println("Time is up");
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
	
	//checks if there is a min price set by the seller and if so it returns the price else it returns 0
	public float minPrice (int CID){
		try {
			
		
			//Get the database connection
			Connection con = this.getConnection();

			//Create a SQL statement
			Statement stmt = con.createStatement();

			// Forms sql select query with given time
			String sql = String.format("select Minimum from Sells where CID = '%d'", CID); //gets the lowest price set by seller
			
			//Run the query against the DB and retrieves results
			ResultSet rs = stmt.executeQuery(sql);
			
			// Iterates through the returned rows (should only be 1 row) to see if if the account with the correct password exists
			while (rs.next()) {
				
				if (rs.getString("Minimum") != null) {
					float minprice = Float.parseFloat(rs.getString("Minimum")); //this is the min price in our table as float
					con.close();
					rs.close();
					return minprice;
				} else {
					break;
				}
			}

			//Close the connection with no account match
			rs.close();
			con.close();
			return 0;
								
		} catch (Exception ex) {
			System.out.println(ex);
			//System.out.println("Account Does Not Exist");
			return 0;
		}
	}
	
	
	public float highestBid(int CID){ //finds the highest bider account id on a cid
		try {
			
		
			//Get the database connection
			Connection con = this.getConnection();

			//Create a SQL statement
			Statement stmt = con.createStatement();

			// Forms sql select query with given time
			String sql = String.format("select  max(price) as p from Bids where CID = '%d'", CID);//get the max price bid on that cid
			
			//Run the query against the DB and retrieves results
			ResultSet rs = stmt.executeQuery(sql);
			float highestBid = 0;
			// Iterates through the returned rows (should only be 1 row) to see if if the account with the correct password exists
			while (rs.next()) {
				
				if (rs.getString("p") != null) {
					 highestBid = Float.parseFloat(rs.getString("p")); //this is the max bid price in our table
					con.close();
					rs.close();
					return highestBid;
				} else {
					break;
				}
			}

			//Close the connection with no account match
			rs.close();
			con.close();
			return highestBid;
								
		} catch (Exception ex) {
			System.out.println(ex);
			//System.out.println("Account Does Not Exist");
			return 0;
		}
	}
	
	
	public int highestBidAid (int CID){ //finds the highest bider account id on a cid
		try {
			
		
			//Get the database connection
			Connection con = this.getConnection();

			//Create a SQL statement
			Statement stmt = con.createStatement();

			// Forms sql select query with given time
			String sql = String.format("select Bid_id from Bids where price =(select  max(price) as p from Bids where CID = '%d)", CID);//get the max price bid on that cid
			
			//Run the query against the DB and retrieves results
			ResultSet rs = stmt.executeQuery(sql);
			int accountW_highestBid = -1;
			// Iterates through the returned rows (should only be 1 row) to see if if the account with the correct password exists
			while (rs.next()) {
				
					 accountW_highestBid = Integer.parseInt(rs.getString("Bid_id")); //this is the min price in our table
					con.close();
					rs.close();
					return accountW_highestBid;
			
				
			}

			//Close the connection with no account match
			rs.close();
			con.close();
			return accountW_highestBid;
								
		} catch (Exception ex) {
			System.out.println(ex);
			//System.out.println("Account Does Not Exist");
			return -1;
		}
	}
	
	public void setWinner (int Bid_id) {
		Connection con = this.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	String setsWinner = String.format("update Bids set winner = '1' where Bid_id = '%d'", Bid_id);
	
	
	try { //update 
		stmt.executeUpdate(setsWinner);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	//Close the connection with no account match
	try {
		con.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}
}
