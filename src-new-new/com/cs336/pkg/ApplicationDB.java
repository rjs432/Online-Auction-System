package com.cs336.pkg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.io.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Date;

public class ApplicationDB {

	public ApplicationDB() {

	}

	public Connection getConnection() {

		// Create a connection string
		String connectionUrl = "jdbc:mysql://localhost:3306/BuyMe"; // project is the database created in mysql
		Connection connection = null;

		try {
			// Load JDBC driver - the interface standardizing the connection procedure. Look
			// at WEB-INF\lib for a mysql connector jar file, otherwise it fails.
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
			// Create a connection to your DB
			connection = DriverManager.getConnection(connectionUrl, "root", "687OAI62");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;

	}

	public void closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ApplicationDB dao = new ApplicationDB();
		// Connection connection = dao.getConnection();

		// System.out.println(connection);
		dao.getListings();
		// dao.closeConnection(connection);
	}

	// givenAccountID / givenPass refers to the credentials that the user submitted
	// on log in form
	public boolean accountExists(String givenAccountID, String givenPassword) {
		try {
			// Log In

			if (givenAccountID == null || givenPassword == null) {
				return false;
			}

			// Get the database connection
			Connection con = this.getConnection();

			// Create a SQL statement
			Statement stmt = con.createStatement();

			// Forms sql select query with given account id and password
			String sql = String.format(
					"select account_id, password from account where account_id = '%s' and password = '%s'",
					givenAccountID, givenPassword);

			// Run the query against the DB and retrieves results
			ResultSet rs = stmt.executeQuery(sql);

			// Iterates through the returned rows (should only be 1 row) to see if if the
			// account with the correct password exists
			while (rs.next()) {
				if (rs.getString("account_id").equals(givenAccountID)
						&& rs.getString("password").equals(givenPassword)) {
					System.out.println("ACCOUNT EXISTS - Logged In");
					con.close();
					rs.close();
					return true;
				} else {
					break;
				}
			}

			// Close the connection with no account match
			rs.close();
			con.close();
			return false;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	// givenAccountID / givenPass refers to the credentials that the user submitted
	// on sign up form
	// DOES NOT ACCOUNT FOR CUSTOMER REP ACCOUNT CREATION
	public boolean createAccount(String givenAccountID, String givenPassword) {
		try {
			// Sign Up

			if (givenAccountID.equals("") || givenPassword.equals("")) {
				return false;
			}

			if (accountIsValid(givenAccountID)) {
				return false;
			} else {

				// Get the database connection
				Connection con = this.getConnection();

				// Create a SQL statement
				Statement stmt = con.createStatement();

				// Forms sql insert query with given account id and password
				String sql = String.format("insert into account values ('%s', 0, '%s')", givenAccountID, givenPassword);
				String sellerSQL = String.format("insert into seller values ('%s')", givenAccountID);
				String buyerSQL = String.format("insert into buyer values ('%s')", givenAccountID);

				// Run the query against the DB
				stmt.executeUpdate(sql);
				stmt.executeUpdate(sellerSQL);
				stmt.executeUpdate(buyerSQL);

				// Close the connection with no account match
				con.close();
				return true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			;
			return false;
		}
	}

	// Converts the form submitted by the seller into an item listing - returns true
	// if query went through
	public boolean createListing(String brand, String itemType, String clothingSize, String shoeSize, String accountID,
			String bidIncrement, String startPrice, String startDate, String startTime, String endDate, String endTime,
			String minPrice) {
		try {

			if (startDate.equals("") || endDate.equals("") || startTime.equals("") || endTime.equals("")
					|| bidIncrement.equals("") || startPrice.equals("") || accountID.equals("")) {
				return false;
			}

			// Checks if a valid account id is submitted when creating listing... should be
			// obtained from session object though
			if (!accountIsValid(accountID))
				return false;

			// Parses date and time submitted
			String[] startD = startDate.split("/");
			String[] endD = endDate.split("/");

			String[] startT = startTime.split(":");
			String[] endT = endTime.split(":");

			String startDateTime = startD[2] + "-" + startD[0] + "-" + startD[1] + " " + startT[0] + ":" + startT[1]
					+ ":" + startT[2];
			String endDateTime = endD[2] + "-" + endD[0] + "-" + endD[1] + " " + endT[0] + ":" + endT[1] + ":"
					+ endT[2];

			// Checks that start date/time is before end date/time
			if (!compareDates(startDateTime, endDateTime))
				return false;

			// Notes item type that is being listed
			String category = null;
			String size = null;
			if (itemType == null) {
				System.out.println("hi");
			}
			else if (itemType.equals("Tops")) {
				category = "tops";
				size = clothingSize;
			} else if (itemType.equals("Bottoms")) {
				category = "bottoms";
				size = clothingSize;
			} else {
				category = "shoes";
				size = shoeSize;
			}

			// Get the database connection
			Connection con = this.getConnection();

			// Create a SQL statement
			Statement stmt = con.createStatement();

			// Generate random CID
			Random rand = new Random();
			int upper = 10000;
			int cid = -1;

			// Generates CID and checks that it is not a duplicate
			while (cid == -1) {
				cid = rand.nextInt(upper);

				// Forms sql select query with given account id and password
				String sql = String.format("select cid from clothing");

				// Run the query against the DB and retrieves results
				ResultSet rs = stmt.executeQuery(sql);

				// Iterates through the returned rows (should only be 1 row) to see if if the
				// account with the correct password exists
				while (rs.next()) {
					if (rs.getInt("cid") == cid) {
						System.out.println("CID ALREADY EXISTS");
						cid = -1;
						break;

					} else {
						continue;
					}
				}
				rs.close();
			}

			// Forms sql insert query for Clothing, Sells, and ISA Category tables with data
			
			String clothingSQL = String.format(
					"insert into clothing (cid, brand, bid_increment,"
							+ "cur_price, start_price) values (%d, '%s', %f, %f, %f)",
					cid, brand, Float.parseFloat(bidIncrement), Float.parseFloat(startPrice),
					Float.parseFloat(startPrice));

			String sellsSQL = String.format(
					"insert into sells (account_id, cid, minimum, start_date,"
							+ "end_date) values ('%s', %d, %f, '%s', '%s')",
					accountID, cid, Float.parseFloat(minPrice), startDateTime, endDateTime);

			String categorySQL = String.format("insert into %s (cid, category, size) values (%d, '%s', '%s')", category,
					cid, category, size);

			stmt.executeUpdate(clothingSQL);
			stmt.executeUpdate(categorySQL);
			stmt.executeUpdate(sellsSQL);

			// Close the connection with no account match
			con.close();
			return true;

		} catch (Exception ex) {
			System.out.println(ex);
			return false;
		}
	}

	// Retrieve all item listings
	public ArrayList<String[]> getListings() {
		try {

			// Get the database connection
			Connection con = this.getConnection();

			// Create a SQL statement
			Statement stmt = con.createStatement();

			// Forms sql to get all listing data
			String[] cats = { "shoes", "tops", "bottoms" };
			ArrayList<String[]> itemList = new ArrayList<String[]>();

			for (int i = 0; i < cats.length; i++) {

				String sql = String.format(
						"select c.cid, cat.category, cat.size, c.brand, c.cur_price, s.start_date, s.end_date,"
								+ "a.account_id from account a, sells s, clothing c, %s cat where s.cid = cat.cid and cat.cid = c.cid and a.account_id = s.account_id",
						cats[i]);

				// Run the query against the DB and retrieves results
				ResultSet rs = stmt.executeQuery(sql);

				// Iterates through the returned listings
				while (rs.next()) {
					String[] items = { rs.getString("cid"), rs.getString("category"), rs.getString("size"),
							rs.getString("brand"), rs.getString("cur_price"), rs.getString("start_date"),
							rs.getString("end_date"), rs.getString("account_id") };

					itemList.add(items);

					/*
					 * System.out.println(rs.getInt("cid"));
					 * System.out.println(rs.getString("category"));
					 * System.out.println(rs.getString("size"));
					 * System.out.println(rs.getString("brand"));
					 * System.out.println(rs.getFloat("cur_price"));
					 * System.out.println(rs.getString("start_date"));
					 * System.out.println(rs.getString("end_date"));
					 * System.out.println(rs.getString("account_id"));
					 */
				}
				rs.close();
			}

			// Close the connection
			con.close();
			return itemList;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private boolean accountIsValid(String accountID) {
		try {

			if (accountID.equals("")) {
				return false;
			}

			// Get the database connection
			Connection con = this.getConnection();

			// Create a SQL statement
			Statement stmt = con.createStatement();

			// Forms sql select query with given account id
			String sql = String.format("select account_id from account where account_id = '%s'", accountID);

			// Run the query against the DB and retrieves accounts
			ResultSet rs = stmt.executeQuery(sql);

			// Iterates through the returned rows (should only be 1 row) to see if if the
			// account exists
			while (rs.next()) {
				if (rs.getString("account_id").equals(accountID)) {
					con.close();
					rs.close();
					return true;
				} else {
					continue;
				}
			}

			// Close the connection with no account match
			rs.close();
			con.close();
			return false;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private static boolean compareDates(String d1, String d2) {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = (Date) sdf.parse(d1);
			Date date2 = (Date) sdf.parse(d2);

			// after() will return false if date1 is after date 2
			if (date1.after(date2)) {
				return false;
			}
			// before() will return true if and only if date1 is before date2
			if (date1.before(date2)) {
				return true;
			}

			// equals() returns false if both the dates are equal
			if (date1.equals(date2)) {
				return false;
			}
			return false;
		} catch (ParseException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	//rob
		//checks if the we have reached the end of the auction time that the user set
		public boolean endOfautction (String today, int CID ){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
				//Get the database connection
				Connection con = this.getConnection();

				//Create a SQL statement
				Statement stmt = con.createStatement();

				// Forms sql select query with given time
				String sql = String.format("select CID, End_date from Sells where CID = '%d' ", CID);
				
				//Run the query against the DB and retrieves results
				ResultSet rs = stmt.executeQuery(sql);
			//	System.out.println ("HERE: ");
				
				// Iterates through the returned rows (should only be 1 row) to see if if the account with the correct password exists
				while (rs.next()) {
					//System.out.println ("end date: " + sdf.parse(rs.getString("End_date")));
					if (rs.getString("CID").equals(String.valueOf(CID)) && ( (sdf.parse(today).after(sdf.parse(rs.getString("End_date")))) || (sdf.parse(today).equals(sdf.parse(rs.getString("End_date"))))  )) {
						//System.out.println("Time is up");
						con.close();
						rs.close();
						//System.out.println ("HERE: worked");
						return true;
					} else {
						//System.out.println ("HERE: midb");
						break;
					}
				}
				//System.out.println ("HERE: end ");
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
						//System.out.println ("HERE: worked2");
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
						//System.out.println ("HERE: worked3");
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
				String sql = String.format("select Bid_id from Bids where price =(select  max(price) as p from Bids where CID = '%d')", CID);//get the max price bid on that cid
				
				//Run the query against the DB and retrieves results
				ResultSet rs = stmt.executeQuery(sql);
				int accountW_highestBid = -1;
				// Iterates through the returned rows (should only be 1 row) to see if if the account with the correct password exists
				while (rs.next()) {
					//System.out.println ("HERE: worked5");
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
		
		//System.out.println ("HERE: worked8");
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
		
		

	//Close the connection with no account match
	try {
		con.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}
}