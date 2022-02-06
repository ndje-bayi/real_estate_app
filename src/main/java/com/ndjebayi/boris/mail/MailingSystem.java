package com.ndjebayi.boris.mail;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ndjebayi.boris.gui.LanternaIngin;
import com.ndjebayi.boris.sqlite.QueryHouseToRentResult;

public class MailingSystem {
	
	private static String reaEmail = "vtakwessi@gmail.com";
	private static String reaPassword = "hraziiqnsjscxpsp";
	
	public static boolean sendEmail(String to, String html, boolean bid) 
			throws AddressException, MessagingException {
		Properties property = new Properties();
		property.put("mail.smtp.auth", "true");
		property.put("mail.smtp.starttls.enable", "true");
		property.put("mail.smtp.port", "587");
		property.put("mail.smtp.host", "smtp.gmail.com");
		property.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		Session session = Session.getInstance(property, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(reaEmail, reaPassword);
			}
		});
		Message email = new MimeMessage(session);
		email.setFrom(new InternetAddress(reaEmail));
		email.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		String subject = (bid)? "Bid made on your real estate" : "Email Authentication";
		email.setSubject(subject);
		email.setContent(html, "text/html");
		Transport.send(email);
		return true;
	}
	
	public static String verifiactionMessage(String name, int number) {
		return "<h2>Hello " + name + ",</h2"
				+ "<br> <h3>Use the following 8-digits to authenticate your email "
				+ "in The Real Estate Agent</h3>"
				+ "<h1 sytle='text-align:center'>" + number + "</h1>"
				+ "<p>The Real Estate Agent is an innovation that brings to you the "
				+ "good fruits of internet technologies. From now on, real estates"
				+ " are just a click away from you.Thus, the ball is in your court. </p>"
				+ "<p>Yours,</p> <p>The client service team</p>";
	}
	
	public static ArrayList<String> bidNotification(boolean landlord) {
		ArrayList<String> bid = LanternaIngin.subscriber;
		ArrayList<String> htmls = new ArrayList<>();
		if(landlord) {
			ArrayList<QueryHouseToRentResult> houses = QueryHouseToRentResult.selectedHouses;
			for(QueryHouseToRentResult house : houses) {
				String html = "<h2>Hello " + house.landlord + "</h2>";
				html += "<p>We are glad to bring to your attention the bid made on your "
						+ house.type + " located in " + house.neighbourhood + ", "
						+ QueryHouseToRentResult.city + " which you discribed as follows: \"" 
						+ house.description + ".\"" + "\nThe bid was made by " + bid.get(1) 
						+ ". Feel free to get in touch with the most probable future tenant of your " 
						+ house.type + " via the following email: " + bid.get(0) + "</p>"
						+ "<p> Yours Sincerely,\nThe Customer Service Team. </p>";
				htmls.add(html);
			}
		}
		else {
			String html = "<h2>Hello " + bid.get(1)+ "</h2>";
			html += "<p>Your bid";
			String owner = "";
			char plural = ' ';
			if (QueryHouseToRentResult.selectedHouses.size() > 1) {
				html += "s have ";
				owner = " verious owners ";
				plural = 's';
				
			}
			else {
				html += " has ";
				owner = " owner ";				
			}
			 html += "been successfully sent to the" + owner + "of the real estate" + plural  
					 + "you selected. Please wait to hear from the owner for more details.</p>"
					 + "<p> Yours Sincerely,\nThe Customer Service Team. </p>";
			 htmls.add(html);
		}
		return htmls;
	}

}