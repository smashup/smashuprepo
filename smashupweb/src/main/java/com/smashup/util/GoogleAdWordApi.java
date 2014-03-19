package com.smashup.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.relique.jdbc.csv.CsvDriver;

public class GoogleAdWordApi {
	
	public static void main(String[] args) {
		GoogleAdWordApi googleAdWordApi = new GoogleAdWordApi();
		// 1. List the file
		 getSubCategoryByCategory("Apparel"); //Apparel //Footwear //Arts & // Entertainment

		// 2. get Category
		//getAllCategory();
		
		// 3. contect search
		//contentSearch("Spa & Medical Spa");
	}

	public static void contentSearch(String searchstring) {
		/*if(searchstring == null){
			System.out.println("Enter the search criteria");
			return;
		}*/
		
		try {

			Connection conn = getConnection();
			// Create a Statement object to execute the query with.
			// A Statement is not thread-safe.
			Statement stmt = conn.createStatement();

			String query = "SELECT ID,Category FROM productsservices";

			System.out.println("Query " + query);

			ResultSet results = stmt.executeQuery(query);

			Set<String> set = new HashSet<String>();

			while (results.next()) {
				String categoryStr = results.getString("Category");
				if(categoryStr.toUpperCase().contains(searchstring.toUpperCase())){
					set.add(categoryStr);
				}
			}

			for (String temp : set) {
				System.out.println(temp);
			}

			// Dump out the results to a CSV file with the same format
			// using CsvJdbc helper function
			// boolean append = true;
			// CsvDriver.writeToCsv(results, System.out, append);

			// Clean up
			results.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getAllCategory() {

		try {

			Connection conn = getConnection();
			// Create a Statement object to execute the query with.
			// A Statement is not thread-safe.
			Statement stmt = conn.createStatement();

			String query = "SELECT ID,Category FROM productsservices";

			System.out.println("Query " + query);

			ResultSet results = stmt.executeQuery(query);

			Set<String> set = new HashSet<String>();

			while (results.next()) {
				String categoryStr = results.getString("Category");
				String tmp[] = categoryStr.split("/");

				if (tmp.length >= 2) {
					set.add(tmp[1]);
				}
			}

			for (String temp : set) {
				System.out.println(temp);
			}

			// Dump out the results to a CSV file with the same format
			// using CsvJdbc helper function
			// boolean append = true;
			// CsvDriver.writeToCsv(results, System.out, append);

			// Clean up
			results.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getSubCategoryByCategory(String category) {

		try {

			Connection conn = getConnection();
			// Create a Statement object to execute the query with.
			// A Statement is not thread-safe.
			Statement stmt = conn.createStatement();

			// Select the ID , Parent ID and Category columns from
			// productsservices.csv
			/*
			 * String clause = "WHERE Category like '/Campers & RVs%'"; //clause
			 * = "WHERE Category like '/"+category+"%'"; clause =
			 * "WHERE Category like '/"+category+"'";
			 * 
			 * System.out.println(clause); String query =
			 * "SELECT ID, Parent_ID, Category FROM productsservices "; // WHERE
			 * Parent_ID In ('10001') //query =
			 * "SELECT ID FROM productsservices "+clause; query +=
			 * "WHERE Parent_ID IN (SELECT ID FROM productsservices "+ clause +
			 * ")";
			 */

			String query = "SELECT ID,Category FROM productsservices WHERE Category like '/"
					+ category + "%'";

			System.out.println("Query " + query);

			ResultSet results = stmt.executeQuery(query);

			Set<String> set = new HashSet<String>();

			while (results.next()) {
				String categoryStr = results.getString("Category");
				String tmp[] = categoryStr.split("/");

				if (tmp.length >= 3) {
					set.add(tmp[2]);
				}

			}

			for (String temp : set) {
				System.out.println(temp);
			}

			// Dump out the results to a CSV file with the same format
			// using CsvJdbc helper function
			// boolean append = true;
			// CsvDriver.writeToCsv(results, System.out, append);

			// Clean up
			results.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void list() {

		try {

			Connection conn = getConnection();
			// Create a Statement object to execute the query with.
			// A Statement is not thread-safe.
			Statement stmt = conn.createStatement();

			// Select the ID , Parent ID and Category columns from
			// productsservices.csv
			ResultSet results = stmt
					.executeQuery("SELECT ID,Parent_ID,Category FROM productsservices");

			// Dump out the results to a CSV file with the same format
			// using CsvJdbc helper function
			boolean append = true;
			CsvDriver.writeToCsv(results, System.out, append);

			// Clean up
			results.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Connection getConnection() {
		try {

			// Load the driver.
			Class.forName("org.relique.jdbc.csv.CsvDriver");
			String folder = "C:/Sony/git/smashuprepo/smashupweb/src/main/resources";			
			
			//String folder = "//smashupweb//resources";
			// Create a connection. The first command line parameter is
			// the directory containing the .csv files.
			// A single connection is thread-safe for use by several threads.			
			Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + folder);
			return conn;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
}