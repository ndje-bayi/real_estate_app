package com.ndjebayi.boris.sqlite;

import java.sql.Statement;
import java.util.ArrayList;

import com.ndjebayi.boris.gui.LanternaIngin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseEngin {
	
	private Connection connection;
	private boolean hasData = false;
	private static int id = 0;
	
	public ArrayList<QueryHouseToRentResult> getCityData(String s) 
			throws SQLException, ClassNotFoundException {
		
		if (connection == null) {
			getConnection();
		}
		ArrayList<QueryHouseToRentResult> workingData = new ArrayList<QueryHouseToRentResult>();
		if(hasData) {
			PreparedStatement statement = connection.prepareStatement(
					"SELECT city, neighbourhood, house_type, description, number_of_bids, h.id,"
					+ " email, name"
					+ " FROM houses AS h"
					+ " INNER JOIN users"
					+ " ON users.id = h.user_id"
					+ " WHERE city = ? AND rented = 0"
					+ " ORDER BY neighbourhood ASC, house_type ASC, number_of_bids DESC;");
			statement.setString(1, s);
			ResultSet result = statement.executeQuery();
			int counter = 1;
			while (result.next()) {
				if (counter == 1) {
					QueryHouseToRentResult.city = result.getString(1);
					counter++;
				}
				workingData.add(new QueryHouseToRentResult(result.getString(2),
						result.getString(3), result.getString(4), result.getString(8),
						result.getString(7), result.getInt(5), result.getInt(6)));
			}
		}
		return (workingData.size() > 0)? workingData: null;
	}

	private void getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:the_real_estate_agent.db");
		initTable("'users'", 
				"(id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "email TEXT,"
				+ "name VARCHAR(50),"
				+ "landlord BOOLEAN,"
				+ "tenant BOOLEAN);");
		initTable("'houses'", 
				"(id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "user_id INTEGER,"
					+ "city VARCHAR(70),"
					+ "neighbourhood VARCHAR(70),"
					+ "house_type VARCHAR(30),"
					+ "description TEXT,"
					+ "number_of_bids INTEGER,"
					+ "rented BOOLEAN);");
		initTable("'house_rented'",
				"(id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "house_id INTEGER,"
				+ "landlord_id INTEGER,"
				+ "tenant_id INTEGR);");
		hasData = true;
	}
	
	private void initTable(String tableName, String tableColumns) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(
				"SELECT name FROM sqlite_master WHERE type = 'table'"
				+ "AND name = " + tableName + ";");
		if (!result.next()) {
			Statement statement2 = connection.createStatement();	
			statement2.execute("CREATE TABLE " + tableName + " " + tableColumns);
		}
		result.close();
		statement.closeOnCompletion();
	}
	
	public boolean addHouse(ArrayList<String> array) throws ClassNotFoundException, SQLException {
		if (connection == null) {
			getConnection();
		}
		boolean status = false;
		PreparedStatement statement;
		if(hasData) {
			statement = connection.prepareStatement(
					"INSERT INTO houses VALUES (?,"
					+ id + ",?, ?, ?,?,0,0);");
			int stop = array.size();
			for(int i = 2, j = 0; j < stop; i++, j++) 
				statement.setString(i, array.get(j));
			status = !statement.execute();
		}
		return status;
	}

	public boolean addUser() 
			throws SQLException, ClassNotFoundException {
		ArrayList<String> userData = LanternaIngin.subscriber;
		if (connection == null) {
			getConnection();
		}
		boolean user = userAlreadyExists(userData.get(0));
		PreparedStatement statement;
		if(user) {
			String update = (userData.get(2).equalsIgnoreCase("true")) ?
					"UPDATE users SET name = ?, landlord = 1 WHERE email = ?;" :
					"UPDATE users SET name = ?, tenant = 1 WHERE email = ?;";
			statement = connection.prepareStatement(update);
			statement.setString(1, userData.get(1));
			statement.setString(2, userData.get(0));
		}
		else {
			statement = connection.prepareStatement(
					"INSERT INTO users VALUES ("
							+ "?,?,?,?,?);");
			int count = 2;
			for(String info : userData) {
				if (info.equalsIgnoreCase("true"))
					statement.setBoolean(count, true);
				else if(info.equalsIgnoreCase("false"))
					statement.setBoolean(count, false);
				else
					statement.setString(count, info);
				count++;
			}
		}
		boolean returnValue = !statement.execute();
		PreparedStatement stat = connection.prepareStatement("SELECT id FROM users WHERE email = ?;");
		stat.setString(1, userData.get(0));
		ResultSet result = stat.executeQuery();
		result.next();
		id = result.getInt(1);
		return returnValue;
	}
	
	private boolean userAlreadyExists(String email) throws SQLException {
		PreparedStatement statement =  connection.prepareStatement(
				"SELECT * FROM users WHERE email = ?;");
		statement.setString(1, email);
		ResultSet result = statement.executeQuery();
		return result.next();	
	}
	
	public void updateHouseBid(int houseId) 
			throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute("UPDATE houses SET number_of_bids = number_of_bids + 1 "
				+ "WHERE id = " + houseId);
	}

	public ArrayList<QueryHouseToRentResult> getLandlordHouses(String email)
			throws SQLException, ClassNotFoundException {
		if (connection == null) {
			getConnection();
		}
		ArrayList<QueryHouseToRentResult> data = new ArrayList<>();
		PreparedStatement stat = connection.prepareStatement("SELECT city, neighbourhood, house_type,"
				+ " description, number_of_bids, h.id, name, users.id AS landlordId FROM houses AS h"
				+ " INNER JOIN users"
				+ " ON  h.user_id = users.id"
				+ " WHERE email = ? AND landlord = 1"
				+ " ORDER BY city");
		stat.setString(1, email);
		ResultSet result = stat.executeQuery();
		while(result.next()) 
			data.add(new QueryHouseToRentResult(result.getString("city"),
					result.getString("neighbourhood"), result.getString("house_type"), 
					result.getString("description"), result.getInt("number_of_bids"), 
					result.getInt("id"), result.getString("name"), result.getInt("landlordId")));
		return data;
	}

	public boolean addRentedHouse() throws ClassNotFoundException, SQLException {
		if (connection == null) 
			getConnection();
		Statement stat = connection.createStatement();
		for (QueryHouseToRentResult house : QueryHouseToRentResult.selectedHouses) {
			if (stat.execute("UPDATE houses SET rented = " + true + " WHERE id = " + house.id))
					return false;
			if (stat.execute("INSERT INTO house_rented (house_id, landlord_id) VALUES " 
					+ "(" +  house.id + ", " + house.landlordId + ");"))
				return false;
		}
		return true;
	}

	public ResultSet getSample() 
			throws ClassNotFoundException, SQLException {
		if (connection == null) 
			getConnection();
		Statement stat = connection.createStatement();
		return stat.executeQuery(
				"SELECT description, number_of_bids FROM houses WHERE house_type = 'appartment'"
				);
	}
}
