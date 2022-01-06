package com.techelevator.view;


import com.techelevator.tenmo.model.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}

	public Double getUserInputDecimal(String prompt) {
		double result = 0;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Double.parseDouble(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == 0);
		return result;
	}

	public void printLine(String string){
		out.println(string);
		out.flush();
	}

	public void displayBalance(double balance){
        out.printf("You currently have $%.2f\n", balance);
    }

	//Method to display registered user
	public void displayUsers(List<UserDTO> userList){
		out.println("--------------------------");
		out.println("__AVAILABLE USERS_________");
		out.printf("%-8s %-12s\n","ID","Username");
		out.println("--------------------------");
		out.flush();
		for(UserDTO user : userList){
			out.print(user.getId());
			out.print("     ");
			out.println(user.getUsername());
			out.flush();
		}
		out.println("---------");
		out.flush();
	}

	public void displayTransfers(List<TransferReturnedDTO> transferList, int currentUserId, List<UserDTO> userList){
		out.println("----------------------------------------");
		out.printf("%-10s %-12s\n", "", "TRANSFERS");
		out.printf("%-11s %-12s %8s\n","ID","To/From","Amount");
		out.println("----------------------------------------");
		out.flush();
		if (transferList.size() == 0){
			out.println("You have no transfer history! Send some money!");
		}

		for(TransferReturnedDTO transfer : transferList){
			int id = transfer.getTransferId();
			int currentUserAccountId = 0;
			for(UserDTO user : userList){
				if(user.getId() == currentUserId){
					currentUserAccountId = user.getAccountId();
				}
			}
			String toOrFrom = transfer.getAccountTo() == currentUserAccountId ? "From" : "To";
			String username = transfer.getAccountTo() == currentUserAccountId ? getUsernameFromAccountId(userList, transfer.getAccountFrom()) : getUsernameFromAccountId(userList, transfer.getAccountTo());

			double amount = transfer.getTransferAmount();
			out.printf("%-7d %6s: %-8s %-1s%7.2f\n",id,toOrFrom,username,"$",amount);
			out.flush();
		}
		out.println("---------");
		out.flush();
	}

	public void displayTranferDetail(TransferReturnedDTO transferDTO, List<UserDTO> users, String currentUserName){
		String toUserName = (getSenderUsername(transferDTO, users, false));
		String fromUserName = (getSenderUsername(transferDTO, users, true));

		if(toUserName.equals(currentUserName)){
			toUserName = "You";
		}
		if(fromUserName.equals(currentUserName)){
			fromUserName = "You";
		}

		out.println("");
		out.println("----------------------------");
		out.println("_____TRANSFER DETAILS_______");
		out.println("----------------------------");
		out.printf("%13s %-15d\n", "Transfer ID: ", transferDTO.getTransferId());
		out.printf("%13s %-15s\n", "From: ", fromUserName);
		out.printf("%13s %-15s\n","To: ", toUserName);
		out.printf("%13s %-15s\n", "Type: ", transferDTO.getTransferTypeDesc());
		out.printf("%13s %-15s\n","Status: ", transferDTO.getTransferStatusDesc());
		out.printf("%13s %-1s%-14.2f\n","Amount: ","$",transferDTO.getTransferAmount());
		out.println("----------------------------");
		out.println("");
		out.flush();
	}

	public int displayApprovalMenuAndGetChoice(TransferReturnedDTO transferDTO, List<UserDTO> users, String currentUserName){
		int menuSelection = -1;
		out.println("");
		out.println("1: Approve");
		out.println("2: Reject");
		out.println("0: Do nothing and exit");
		out.println("---------");
		menuSelection = getUserInputInteger("Please choose an option");
		out.flush();
		return menuSelection;
	}

    public void postTransferSummary(TransferRequestDTO transferRequest, double balance, boolean isRequest){
        out.println("");
        out.println(">>> Success!"); //get this back from the Server instead
		if(isRequest){
			out.printf(">>> You requested $%s from user #%s. The transfer will be complete if they login and approve your request. Your current balance is $%.2f\n",
					transferRequest.getAmount(), transferRequest.getUserToID(), balance);
		} else {
			out.printf(">>> You sent user#%s $%s and your new account balance is $%.2f\n",
					transferRequest.getUserToID(), transferRequest.getAmount(), balance);
		}
    }

	//helper method for displayTransferDetails
	public String getSenderUsername(TransferReturnedDTO transferDTO, List<UserDTO> users, boolean isSender) {

		UserDTO tempUser = new UserDTO();

			if (isSender) {
				for (UserDTO user : users) {
					if (user.getAccountId() == transferDTO.getAccountFrom()) {
						tempUser = user;
					}
				}
			} else {
				for (UserDTO user : users) {
					if (user.getAccountId() == transferDTO.getAccountTo()) {
						tempUser = user;
					}
				}
			}
			return tempUser.getUsername();
		}

		public String getUsernameFromAccountId(List<UserDTO> userList, int accountId){
		String username = "";
			for (UserDTO user : userList) {
				if (user.getAccountId() == accountId) {
					username = user.getUsername();
				}
			}
			return username;
		}
	}

