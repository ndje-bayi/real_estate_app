package com.ndjebayi.boris.gui;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.ndjebayi.boris.sqlite.DatabaseEngin;

/**
 * launches the app 
 * @author Ndje Bayi venant Boris
 */
public class lunch {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		DatabaseEngin data = new DatabaseEngin();
		try {
			ResultSet stat = data.getSample();
			List<String[]> workingData = new ArrayList<>();
			while(stat.next()) {
				String[] row = {stat.getString(1), stat.getString(2)};
				workingData.add(row);
			}
			int modeInt =  0;
			String mode = "";
			double total = 0;
			double mean = 0;
			double median = 0;
			System.out.println("The data set...");
			for (String[] row : workingData) {
				System.out.println(Arrays.toString(row));
//	computing the mode		
				int number = Integer.parseInt(row[1]);
				if (number > modeInt) {
					modeInt =  number;
					mode = row[0];
				}
				total += Integer.parseInt(row[1]);
				
			}
//	computing the median
			median = (total % 2 == 1)? (total / 2) - 0.5 : (total / 2) + 0.5;
			
//	computing the mean
			mean = total / workingData.size();
			
//  computing the standard deviation
			int numerator = 0;
			for (String[] row : workingData) 
				numerator += Math.pow((Integer.parseInt(row[1]) - mean), 2);
			double standardDeviation = Math.pow((double) numerator / (workingData.size() - 1), 0.5);
			
			System.out.println("\nThe Mode...");
			System.out.println(mode + " with the total frequency of " + modeInt);
			System.out.println("\nThe Mean...");
			System.out.println(mean);
			System.out.println("\nThe Median...");
			System.out.println(median);
			System.out.println("\nThe Standard deviation...");
			System.out.println(standardDeviation);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		input.next();
		input.close();
		new LanternaIngin();
	}
}
