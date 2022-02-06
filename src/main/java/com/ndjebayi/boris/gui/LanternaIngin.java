package com.ndjebayi.boris.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.ndjebayi.boris.sqlite.DatabaseEngin;
import com.ndjebayi.boris.sqlite.QueryHouseToRentResult;
import com.ndjebayi.boris.mail.MailingSystem;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

public class LanternaIngin {
	private ArrayList<Panel> panels = new ArrayList<>();
	private Screen screen = null;
	private WindowBasedTextGUI textGUI;
	private Window window = new BasicWindow("The Real Estate Agent");
	private DatabaseEngin database = new DatabaseEngin();
	private ArrayList<QueryHouseToRentResult> data = new ArrayList<>();
	private boolean post;
	private boolean get;
	private ArrayList<String> postData = new ArrayList<>();
	public static ArrayList<String> subscriber = new ArrayList<>();
	private String neighbourhood = "";
	private int code = 0;
	private int attempts = 0;
	private String typeOfHouse = "";
	
	public LanternaIngin() {
	     try {
	    	 screen = new DefaultTerminalFactory().createScreen();
	    	 screen.startScreen();
	    	 textGUI = new MultiWindowTextGUI(screen);
	    	 LinearLayout indexLayout = new LinearLayout();
	    	 indexLayout.setSpacing(1);
	    	 Panel contentPanel = new Panel(indexLayout);
	    	 contentPanel.addComponent(new EmptySpace().setLayoutData(
	    			 LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning)));
	    	 Label title = new Label("Welcome to the office of the virtual real estate agent");
	    	 title.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));   
	    	 contentPanel.addComponent(title);
	    	 contentPanel.addComponent(new Button(" View Houses to rent", 
	    			 () -> city()).setLayoutData(LinearLayout.createLayoutData(
	    					 LinearLayout.Alignment.Center)));
	    	 
	    	 contentPanel.addComponent(new Button("Post a house for renting",
	    			 ()-> addHouse()).setLayoutData(
	    			 LinearLayout.createLayoutData(LinearLayout.Alignment.Center)));
	    	 contentPanel.addComponent(new Button("Close a deal", ()-> closeHouse()).setLayoutData(
	    			 LinearLayout.createLayoutData(LinearLayout.Alignment.Center)));
	    	 panels.add(contentPanel);
	    	 window.setComponent(contentPanel);
	    	 textGUI.addWindowAndWait(window);
	     }
	     catch(IOException e) {
	    	 e.printStackTrace();
	     }
	     finally {
	    	 if (screen != null) {
	    		 try {
	    			 screen.stopScreen();
		    		 }catch(IOException e) {
		    			 e.printStackTrace();
		    	}
	    	 }
	     }
	}

	private void closeHouse() {
		get = false;
		post = false;
		attempts = 0;
		Panel housePanel = initPanel("Declare a taken real estate");
		housePanel.addComponent(new Label("Enter Your Email").setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
		housePanel.addComponent(new TextBox());
		closePanel(1, housePanel);
//		cleaner(1);
		panels.add(housePanel);
		window.setComponent(housePanel);
	}

	private void addHouse() {
		get = false;
		post = true;
		attempts = 0;
		Panel additionPanel = initPanel("Add your house to rent");
		additionPanel.addComponent(new Label("Enter the city").setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
		additionPanel.addComponent(new TextBox());
		additionPanel.addComponent(new Label("Enter neighbourhood").setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
		additionPanel.addComponent(new TextBox());
		additionPanel.addComponent(new Label("Indicate Type of house").setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
		additionPanel.addComponent(new TextBox());
		additionPanel.addComponent(new Label("Add ample description").setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
		additionPanel.addComponent(new TextBox());
		closePanel(1, additionPanel);
//		cleaner(1);
		panels.add(additionPanel);
		window.setComponent(additionPanel);	
	}
	
	private void cleaner(int i) {
		for(; i < panels.size();)
			panels.remove(i);
	}
	
	private ArrayList<String> collectPostData(Panel panel) {
		ArrayList<String> array = new ArrayList<String>();
		for(Component element : panel.getChildrenList()) {
			if (element instanceof TextBox) {
				String text = ((TextBox)element).getText();
				if (text.isBlank()) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
			    			 "Sorry! all fields are mandatory but you did not fill all of them", 
			    			 MessageDialogButton.OK);
					break;
				}
				else {
					text = text.strip();
					text = text.toLowerCase();
					array.add(text);
				}
			}
		}
		return array;
	}

	private void goPublic(ArrayList<String> array) {
		try {
			if (database.addHouse(array)) {
				MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent",
						"Congratulations! Your house is now visible on the web",
		    			 MessageDialogButton.OK);
				window.setComponent(panels.get(0));
				cleaner(1);
			}
			else
				MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
		    			 "Sorry! Something went wrong while publishing your house to rent", 
		    			 MessageDialogButton.OK);
		} catch (ClassNotFoundException e) {
			MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
	    			 "Sorry! The following error occured \n" + e.getMessage(), 
	    			 MessageDialogButton.OK);
		} catch (SQLException e) {
			MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
					"Sorry! The following error occured \n" + e.getMessage(),
	    			 MessageDialogButton.OK);
		}
	}

	private Label title(String s) {
		Label labelTitle = new Label(s);
		labelTitle.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER, GridLayout.Alignment.BEGINNING, true, false, 2, 1));
		return labelTitle;
	}
	
	private Panel initPanel(String s) {
		GridLayout mainLayout = new GridLayout(2);
		mainLayout.setVerticalSpacing(1);
		Panel somePanel = new Panel(mainLayout);
		somePanel.addComponent(new EmptySpace().setLayoutData(
				GridLayout.createHorizontallyFilledLayoutData(2)));
		Label panelTitle = title(s);
		somePanel.addComponent(panelTitle);
		return somePanel;
	}
	
	private void closePanel(int position, Panel panel) {
		Button cancel = new Button("Cancel", ()-> cancelButtonEvent(position));
		String text = (position != 4)? "Next" : "Make a bid";
		Button next = new Button(text, ()-> nextButtonEvent(position, panel));
		next.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END,
				GridLayout.Alignment.BEGINNING));
		panel.addComponent(cancel);
		panel.addComponent(next);
	}
	
	private void nextButtonEvent(int position, Panel panel) {
		String text = "";
		switch(position) {
		case 1: {
			if(post) {
				postData = collectPostData(panels.get(position));
				if(postData.size() < 4)
					return;
				subscription(position + 1);
			}
			else if(get) {
				int size = panel.getChildCount();
				for (int i = 0; i < size; i++) {
					if (panel.getChildrenList().get(i) instanceof TextBox) {
						text = ((TextBox) panel.getChildrenList().get(i)).getText();
						text = text.strip().toLowerCase();
						break;
					}
				}
				
				if (text.isBlank()) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! You did not enter any meaningfull city name",
							MessageDialogButton.OK);
					window.setComponent(panels.get(1));
					break;
				}
				try {
					data = database.getCityData(text);
					if (data == null) {
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Sorry! The real estate agent has no available house to rent in " + 
										text.toUpperCase(),
										MessageDialogButton.OK);
						window.setComponent(panels.get(1));
						break;
					}
				} catch (ClassNotFoundException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! The SQLite drivers are not properly set",
							MessageDialogButton.OK);
					window.setComponent(panels.get(1));
					break;
				} catch (SQLException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! The following error occured while searching the database \n"
									+ e.getMessage(),
									MessageDialogButton.OK);
					window.setComponent(panels.get(1));
					break;
				}
				neighbourhood(text);
				break;
			}
			else {
				subscriber.clear();
				subscriber = collectPostData(panel);
				if(subscriber.size() < 1)
					return;
				try {
					data = database.getLandlordHouses(subscriber.get(0));
					if (data.size() > 0)
						code = confirmCode(subscriber.get(0), 2);
					else
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Sorry! You first have to post a real estate before you can "
								+ "close a deal", MessageDialogButton.OK);
				} catch (ClassNotFoundException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! Database drives missing",
									MessageDialogButton.OK);
				} catch (SQLException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! The following error occuered in the database \n"
							+ e.getMessage(),
									MessageDialogButton.OK);
				}
			}
			break;
		}
		case 2: {
			if (post) {
				subscriber = collectPostData(panel);
				if (subscriber.size() < 2)
					return;
				subscriber.add("true");
				subscriber.add("false");
				code = confirmCode(subscriber.get(0), 3);
			}
			else if(get) {
				String choice = getChoice(2, "neighbourhood");
				if (choice == null)
					break;
				typeOfHouses(choice);
			}
			else {
				if(verifyCode(code)) {
					showHousesOwned();
				}
				else {
					if (attempts < 2) {
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Sorry! The codes mismatched. You have one last attempt.", 
								MessageDialogButton.OK);
					}
					else {
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Sorry! The codes mismatched again.\nTaking you back to the "
								+ "home page", 
								MessageDialogButton.OK);
						window.setComponent(panels.get(0));
						cleaner(1);
					}
			}
			}
			break;
		}
		case 3: {
			if(get) {
				String choice = getChoice(3, "type of house");
				if (choice == null)
					return;
				viewDashboard(choice); 
				break;
			}
			else if(post) {
				if(verifyCode(code)) {
					try {
						database.addUser();
						goPublic(postData);
					} catch (SQLException e) {
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Sorry! The following error occured in the database\n"
										+ e.getMessage(),
										MessageDialogButton.OK);
					} catch (ClassNotFoundException e) {
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Sorry! Some divers are no where to be found",
								MessageDialogButton.OK);
					}
				}
				else {
					if (attempts < 2) {
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Sorry! The codes mismatched. You have one last attempt.", 
								MessageDialogButton.OK);
					}
					else {
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Sorry! The codes mismatched again.\nTaking you back to the "
								+ "home page", 
								MessageDialogButton.OK);
						window.setComponent(panels.get(0));
						cleaner(1);
					}
				}
			}
			else {
				QueryHouseToRentResult.selectedHouses.clear();
				for (Component element : panel.getChildrenList()) {
					if ( element instanceof CheckBoxList) {
						CheckBoxList<String> box = ((CheckBoxList<String>) element);
						int nber = box.getItemCount();
						for(int i = 0; i < nber; i++) {
							if (box.isChecked(i)) {
								QueryHouseToRentResult.selectedHouses.add(data.get(i));
							}
						}
						break;
					}
				}
				try {
					if(database.addRentedHouse()) {
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Congratulations on your closed deal.\nWe wish you all the "
								+ "best with your tenant.\nTaking you to the home page.", 
								MessageDialogButton.OK);
						window.setComponent(panels.get(0));
						cleaner(1);
					}
				} catch (ClassNotFoundException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! Some database drivers are missing", 
							MessageDialogButton.OK);
				} catch (SQLException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry!The following error occurred in the database\n"
							+ e.getMessage(), 
							MessageDialogButton.OK);
				}
			}
			break;
		}
		case 4:{
			QueryHouseToRentResult.selectedHouses.clear();
			for(Component widget : panel.getChildrenList()) {
				if (widget instanceof CheckBoxList) {
					CheckBoxList<String> box = ((CheckBoxList<String>) widget);
					int nber = box.getItemCount();
					for(int i = 0; i <= nber; i++) {
						if ( box.isChecked(i) != null && box.isChecked(i)) {
							QueryHouseToRentResult.selectedHouses.add(
									QueryHouseToRentResult.suggestedHouses.get(i));
						}
					}
					if(QueryHouseToRentResult.selectedHouses.size() > 0)
						subscription(5);
					else
						MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
								"Sorry! You have to choose at least a " + typeOfHouse 
								+ " before you can proceed", 
								MessageDialogButton.OK);
					break;
				}
			}
			break;
		}
		case 5: {
			subscriber = collectPostData(panel);
			if (subscriber.size() < 2)
				return;
			subscriber.add("false");
			subscriber.add("true");
			code = confirmCode(subscriber.get(0), 6); 
			break;
		}
		default: {
			if (verifyCode(code)) {
				try {
					database.addUser();
					ArrayList<String> emailToLandlord = MailingSystem.bidNotification(true);
					int counter = 0;
					for (QueryHouseToRentResult house : QueryHouseToRentResult.selectedHouses) {
						MailingSystem.sendEmail(house.email, emailToLandlord.get(counter), true);
						database.updateHouseBid(house.id);
					}
					ArrayList<String> emailToTenant = MailingSystem.bidNotification(false);
					MailingSystem.sendEmail(subscriber.get(0), emailToTenant.get(0), true);
				} catch (ClassNotFoundException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! The database drives failled.", 
							MessageDialogButton.OK);
				} catch (SQLException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! The following error occurred in the database\n"
							+ e.getMessage(), 
							MessageDialogButton.OK);
					return;
				} catch (AddressException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! The following error occurred\n"
							+ e.getMessage(), 
							MessageDialogButton.OK);
					return;
				} catch (MessagingException e) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! The following error occured\n"
							+ e.getMessage(), 
							MessageDialogButton.OK);
					return;
				}
				MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
						"Kindly check you email for more info", 
						MessageDialogButton.OK);
				window.setComponent(panels.get(0));
				cleaner(1);
			}
			else {
				if (attempts < 2) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! The codes mismatched. You have one last attempt.", 
							MessageDialogButton.OK);
				}
				else {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! The codes mismatched again.\nTaking you back to the home page", 
							MessageDialogButton.OK);
					window.setComponent(panels.get(0));
					cleaner(1);
				}
			}
		}
		}
	}
	
	private void showHousesOwned() {
		CheckBoxList<String> checkboxes = new CheckBoxList<>();
		for (QueryHouseToRentResult house : data) {
			checkboxes.addItem(house.toString());
		}
		Panel showHouses = initPanel("Showing your house(s)");
		showHouses.addComponent(checkboxes.setLayoutData(GridLayout.createLayoutData(
				GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, 
				true, true, 2, 1)));
		closePanel(3, showHouses);
		panels.add(showHouses);
		window.setComponent(showHouses);
	}

	private String getChoice(int pos, String message) {
		String choice = "";
		Panel previousPanel = panels.get(pos);
		for (Component element : previousPanel.getChildrenList()){
			if (element instanceof RadioBoxList) {
				choice = ((RadioBoxList<String>) element).getCheckedItem();
				if (choice == null) {
					MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
							"Sorry! To go any further, you have to select a " + message,
							MessageDialogButton.OK);
					return null;
				}
				break;
			}
		}
		return choice;
	}

	private void cancelButtonEvent(int position) {
		window.setComponent(panels.get(position - 1));
		cleaner(position);
	}

	private void city() {
		attempts = 0;
		post = false;
		get = true;
		Panel citySearch = initPanel("Enter the name of city");
		TextBox searchBar = new TextBox();
		searchBar.setLayoutData(GridLayout.createLayoutData(
				GridLayout.Alignment.CENTER, GridLayout.Alignment.BEGINNING, true, false, 2, 1));
		citySearch.addComponent(searchBar);
		closePanel(1, citySearch);
		cleaner(1);
		panels.add(citySearch);
		window.setComponent(citySearch);
	}
	
	private void neighbourhood(String text){
		Panel neighbourPanel = initPanel("Showing neighbourhoods in " + text.toUpperCase());
		neighbourPanel.addComponent(new Label("Select a neighbourhood of your interest").setLayoutData(
				GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.BEGINNING, 
						true, false, 2, 1)));
		neighbourPanel.addComponent(addRadioButtons(1, text));
		neighbourPanel.addComponent(new Label("If the neighbourhood of your interest is not on the above"
				+ " list, it means that \nthe Real Estate Agent in " + text.toUpperCase() 
				+ " has no house available in that neighbourhood").setLayoutData(
						GridLayout.createLayoutData(
								GridLayout.Alignment.BEGINNING, GridLayout.Alignment.BEGINNING, 
								true, false, 2, 1)));
		closePanel(2, neighbourPanel);
		panels.add(neighbourPanel);
		window.setComponent(neighbourPanel);
		
	}
	
	private RadioBoxList<String> addRadioButtons(int index, String select) {
		RadioBoxList<String> radios = new RadioBoxList<String>();
		String element = "";
		List<String> items;
		for (QueryHouseToRentResult actualData : data) {
			items = radios.getItems();
			if (index == 2)
				element = actualData.type.toUpperCase();
			if (index == 1)
				element = actualData.neighbourhood.toUpperCase();
			if (!items.contains(element))
				radios.addItem(element);			
		}
		radios.setLayoutData(GridLayout.createLayoutData(
				GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false, 2, 1));
		return radios;
	}

	private void typeOfHouses(String choice) {
		Panel housPanel = initPanel("Just a Step away...");
		housPanel.addComponent(new Label("Select the type of house you are looking for")
				.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, 
						GridLayout.Alignment.CENTER, true, false, 2, 1)));
		housPanel.addComponent(addRadioButtons(2, choice.toLowerCase()));
		String town = QueryHouseToRentResult.city;
		housPanel.addComponent(new Label("If the type of house you are looking for is not on the above"
				+ " list, it means that \nthe Real Estate Agent in " + choice + " ("
				+ town.toUpperCase() + ") has no house of that type available").setLayoutData(
						GridLayout.createLayoutData(
								GridLayout.Alignment.BEGINNING, GridLayout.Alignment.BEGINNING, 
								true, false, 2, 1)));
		closePanel(3, housPanel);
		panels.add(housPanel);
		window.setComponent(housPanel);	
		neighbourhood = choice;
	}
	
	private void viewDashboard(String type) {
		String town = QueryHouseToRentResult.city.toUpperCase();
		int houses = 0;
		CheckBoxList<String> checkboxes = new CheckBoxList<>();
		QueryHouseToRentResult.suggestedHouses.clear();
		for(QueryHouseToRentResult element : data) {
			if (element.neighbourhood.equalsIgnoreCase(neighbourhood)
				&& element.type.equalsIgnoreCase(type)) {
				houses++;
				checkboxes.addItem(element.description + " | " + element.bids + " Bid(s)");
				QueryHouseToRentResult.suggestedHouses.add(element);
			}
		}
		checkboxes.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, 
				GridLayout.Alignment.CENTER, true, true, 2, 1));
		
		String number = (houses > 1)? "are the " + type + "s" : "is the " + type;
		Panel viewPanel = initPanel("Here " + number + " available in " + neighbourhood + ", "
				+ town);
		
		viewPanel.addComponent(new Label( type + "'s" + " description | number of bids made for the " 
				+ type).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, 
						GridLayout.Alignment.CENTER, true, true, 2, 1)));
		viewPanel.addComponent(checkboxes);
		closePanel(4, viewPanel);
		panels.add(viewPanel);
		window.setComponent(viewPanel);
		typeOfHouse = type;
	}
	
	private void subscription(int pos) {
		Panel accountPanel = initPanel("Account");
		Label emailLabel = new Label("Enter your Email");
		TextBox emailInput = new TextBox();
		Label nameLabel = new Label("Enter your name");
		TextBox nameInput = new TextBox();
		accountPanel.addComponent(emailLabel);
		accountPanel.addComponent(emailInput);
		accountPanel.addComponent(nameLabel);
		accountPanel.addComponent(nameInput);
		closePanel(pos, accountPanel);
		panels.add(accountPanel);
		window.setComponent(accountPanel);
	}
	
	private int confirmCode(String email, int pos) {
		int digit = (int)(Math.random() * 90_000_000) + 10_000_000;
		
		String name = "";
		if (subscriber.size() == 1)
			name = data.get(0).landlord;
		else
			name = subscriber.get(1);
		String	html = MailingSystem.verifiactionMessage(name, digit);
		try {
			MailingSystem.sendEmail(email, html, false);
			
			Panel confirmPanel = initPanel("Email verification...");
			Label description = new Label("Enter the 8 digits code we just sent to " + email);
			description.setLayoutData(GridLayout.createLayoutData(
					GridLayout.Alignment.BEGINNING, GridLayout.Alignment.BEGINNING, true, false, 2, 1));
			TextBox codeInput = new TextBox();
			codeInput.setLayoutData(GridLayout.createLayoutData(
					GridLayout.Alignment.BEGINNING, GridLayout.Alignment.BEGINNING, true, false, 2, 1));
			confirmPanel.addComponent(description);
			confirmPanel.addComponent(codeInput);
			closePanel(pos, confirmPanel);
			window.setComponent(confirmPanel);
			panels.add(confirmPanel);
			System.out.println(digit);
		} catch (MessagingException e) {
			MessageDialog.showMessageDialog(textGUI, "The Real Estate Agent", 
					"Sorry! The following error occurred\n"
					+ e.getMessage(), 
					MessageDialogButton.OK);
		}
		return digit;
	}
	
	private boolean verifyCode(int sent) {
		boolean valid = false;
		Panel panel = panels.get(panels.size() - 1);
		for(Component element : panel.getChildrenList()) {
			if (element instanceof TextBox) {
				String user = ((TextBox) element).getText();
				if (!user.isBlank()) {
					try {
						int input = Integer.valueOf(user);
						if (input == sent) {
							valid = true;
						}
						break;
					}catch (Exception e) {
						break;
					}
				}
			}
		}
		if (!valid)
			attempts++;
		return valid;
	}

}
