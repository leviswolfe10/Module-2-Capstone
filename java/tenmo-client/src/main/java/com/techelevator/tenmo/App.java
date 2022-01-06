package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;
import io.cucumber.core.gherkin.ScenarioOutline;
import io.cucumber.java.bs.A;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private UserService userService;
    private TransferService transferService;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL),
                new AccountService(), new UserService(), new TransferService());
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService,
               UserService userService, TransferService transferService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.userService = userService;
        this.transferService = transferService;
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            console.printLine("");
            console.printLine("_____  MAIN MENU  _____");
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory("approved");
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() {
        console.displayBalance(accountService.getAccountBalance());
        console.printLine("");

    }


    private void viewPendingRequests() {
        viewTransferHistory("pending");
    }


    private void viewTransferHistory(String status) {
        boolean isLooping = true;

        List<TransferReturnedDTO> returnedTransferList = new ArrayList<>();

        //List<TransferReturnedDTO> returnedTransferList = transferService.getTransfers();
        if (status.equalsIgnoreCase("pending")) {
            returnedTransferList = transferService.getPendingTransfers();
        } else {
            returnedTransferList = transferService.getTransfers();
        }

        console.displayTransfers(returnedTransferList, currentUser.getUser().getId(), userService.getListOfUsersIncludingUS());

        List<Integer> transferIdList = new ArrayList<>();
        for (TransferReturnedDTO transfer : returnedTransferList) {
            transferIdList.add(transfer.getTransferId());
        }

        if (status.equalsIgnoreCase("pending")){
            //console.printLine("run approve menu");
            runApproveMenu(true, transferIdList, returnedTransferList);
        }else if (!status.equalsIgnoreCase("pending")){
            viewTransferDetail(isLooping, transferIdList, returnedTransferList);
        }
    }


    private void viewTransferDetail(boolean isLooping, List<Integer> transferIdList, List<TransferReturnedDTO> returnedTransferList) {
        TransferReturnedDTO transferForDetail = new TransferReturnedDTO();
        while (isLooping) {
            int transferID = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)");
            //validate choice of transferID
            if (transferID != 0) {
                if (transferIdList.contains(transferID)) {
                    //extract transfer for detailed display
                    for (TransferReturnedDTO transfer : returnedTransferList) {
                        if (transfer.getTransferId() == transferID) {
                            transferForDetail = transfer;
                        }
                    }
                    //display transfer details
                    List<UserDTO> allUsers = userService.getListOfUsersIncludingUS();
                    String currentUserName = currentUser.getUser().getUsername();
                    console.displayTranferDetail(transferForDetail, allUsers, currentUserName);
                    isLooping = false;

                } else {
                    console.printLine("Transfer ID does not exist. Please make another selection");
                }
            } else {
                isLooping = false;
            }
        }
    }

    private void runApproveMenu(boolean isLooping, List<Integer> transferIdList, List<TransferReturnedDTO> returnedTransferList){
        TransferReturnedDTO transferForApproval = new TransferReturnedDTO();
        while (isLooping) {
            int transferID = console.getUserInputInteger("Please enter transfer ID to approve/reject (0 to cancel)");
            if (transferID != 0) {
                if (transferIdList.contains(transferID)) {
                    for (TransferReturnedDTO transfer : returnedTransferList) {
                        if (transfer.getTransferId() == transferID) {
                            transferForApproval = transfer;
                        }
                    }
                    List<UserDTO> allUsers = new ArrayList<>();
                    allUsers = userService.getListOfUsersIncludingUS();
                    String currentUserName = currentUser.getUser().getUsername();
                    getApproveMenuSelection(transferForApproval, allUsers, currentUserName);
                    isLooping = false;

                } else {
                    console.printLine("Transfer ID does not exist. Please make another selection");
                }
            } else {
                isLooping = false;
            }
        }
    }

    private void getApproveMenuSelection(TransferReturnedDTO transferForApproval,  List<UserDTO> allUsers, String currentUserName){
        int approvalMenuChoice = -2;
        while (approvalMenuChoice < 0) {
            approvalMenuChoice = console.displayApprovalMenuAndGetChoice(transferForApproval, allUsers, currentUserName);
            if (approvalMenuChoice == 1){
                console.printLine("transfer will be approved and your account will be deducted. (not yet implemented.)");
            } else if (approvalMenuChoice == 2){
                //transferService.rejectTransfer(transferForApproval);
                console.printLine("transfer will be marked rejected. No money will be transferred. (not yet implemented.)");
            } else {
                console.printLine("You have made no changes. Returning to main menu.");
            }
        }
    }

    private void sendBucks() {
        int userFromID = currentUser.getUser().getId();
        List<UserDTO> displayList = userService.getListOfUsersNotUS();
        console.displayUsers(displayList);

        boolean isLoopingForToId = true;
        List<Integer> userIdList = new ArrayList<>();

        for (UserDTO user : displayList) {
            userIdList.add(user.getId());
        }

        while (isLoopingForToId) {
            int userToId = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
            if (userToId != 0) {
                if (userIdList.contains(userToId)) {
                    createTransferDTO(userFromID, userToId, false);
                    //update to receive success message/variable returned from createTransfer method,
                    // which comes from Server through transferService.makeTransfer(transferRequest);
                    //use success to run console.postTransferSummary(transferRequest, accountService.getAccountBalance());
                    isLoopingForToId = false;
                } else {
                    console.printLine("Please enter a valid user ID!");
                }
            } else {
                isLoopingForToId = false;
            }
        }
    }

    //for approved Sends
    private void createTransferDTO(int userFromID, int userToId, boolean isRequest) {
        boolean isLoopingForAmountEntry = true;
        while (isLoopingForAmountEntry) {
            double amount = console.getUserInputDecimal("Enter amount (greater than $0)");
            if (amount > 0 && amount <= accountService.getAccountBalance()) {
                TransferRequestDTO transferRequest = new TransferRequestDTO();
                transferRequest.setUserFromID(userFromID);
                transferRequest.setUserToID(userToId);
                transferRequest.setAmount(amount);
                transferService.initiateTransferRequest(transferRequest);
                console.postTransferSummary(transferRequest, accountService.getAccountBalance(), isRequest);
                isLoopingForAmountEntry = false;
            } else {
                console.printLine("Please enter a valid amount of money!");
            }
        }
    }

    private void requestBucks() {
        int userToId = currentUser.getUser().getId(); //current user becomes recipient
        List<UserDTO> displayList = userService.getListOfUsersNotUS();
        console.displayUsers(displayList);

        boolean isLoopingForToId = true;
        List<Integer> userIdList = new ArrayList<>();

        for (UserDTO user : displayList) {
            userIdList.add(user.getId());
        }

        while (isLoopingForToId) {
            int userFromId = console.getUserInputInteger("Enter ID of user you are requesting money from (0 to cancel)");
            if (userFromId != 0) {
                if (userIdList.contains(userFromId)) {
                    createTransferDTO(userFromId, userToId, true);
                    //receive success from createTransfer method, which comes from Server through transferService.makeTransfer(transferRequest);
                    //use success to run console.postTransferSummary(transferRequest, accountService.getAccountBalance());
                    isLoopingForToId = false;
                } else {
                    console.printLine("Please enter a valid user ID!");
                }
            } else {
                isLoopingForToId = false;
            }
        }
    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
                accountService.setAuthToken(currentUser.getToken());
                userService.setAuthToken(currentUser.getToken());
                transferService.setAuthToken(currentUser.getToken());
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
