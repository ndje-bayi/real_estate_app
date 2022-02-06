package com.ndjebayi.boris.sqlite;

import java.util.ArrayList;

public class QueryHouseToRentResult {
	public static String city = "";
	public static ArrayList<QueryHouseToRentResult> suggestedHouses = new ArrayList<>();
	public static ArrayList<QueryHouseToRentResult> selectedHouses = new ArrayList<>();
	public String neighbourhood = "";
	public String type = "";
	public String description = "";
	public String landlord = "";
	public String email = "";
	public String ville = "";
	public int bids;
	public int id;
	public int landlordId;
	
	public QueryHouseToRentResult(String neighbourhood, String type, String description,
			String landlord, String email, int bids, int id) {
		this.neighbourhood = neighbourhood;
		this.type = type;
		this.description = description;
		this.email = email;
		this.landlord = landlord;
		this.bids = bids;
		this.id = id;
	}

	public QueryHouseToRentResult(String city, String neighbour, String type, String description,
			int bids, int id, String name, int landlordId) {
		this.ville = city;
		this.neighbourhood = neighbour;
		this.type = type;
		this.description = description;
		this.bids = bids;
		this.id = id;
		this.landlord = name;
		this.landlordId = landlordId;
	}
	
	public String toString() {
		return this.ville + " | " + this.neighbourhood + " | " + this.type + " | " + this.description 
				+ " | " + this.bids + "bid(s)";
	}
}
