package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.TransferRequestDTO;
import com.techelevator.tenmo.model.TransferReturnedDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private TransferDao transferDao;
    @Autowired
    private AccountDao accountDao;

    @RequestMapping(path="/initiate_transfer_request", method = RequestMethod.POST)
    public void addTransferRequest(@RequestBody TransferRequestDTO transferRequest){
        int fromUserID = transferRequest.getUserFromID();
        int toUserID = transferRequest.getUserToID();
        double transferAmount = transferRequest.getAmount();

        TransferReturnedDTO transferToInsert = new TransferReturnedDTO();

        transferToInsert.setTransferTypeId(1);//type: REQUEST
        transferToInsert.setTransferStatusId(1);//status: PENDING
        transferToInsert.setAccountFrom(accountDao.getAccountByUserId(fromUserID).getAccountId());
        transferToInsert.setAccountTo(accountDao.getAccountByUserId(toUserID).getAccountId());
        transferToInsert.setTransferAmount(transferAmount);
        // transferToInsert.setAccountTo(accountDao.getAccountNumberByUserID();) COULD WRITE METHOD
        transferDao.addTransfer(transferToInsert);
    }

    @RequestMapping(path="/make_transfer", method = RequestMethod.POST)
    public void addNewTransfer(@RequestBody TransferRequestDTO transfer){
        int fromUserID = transfer.getUserFromID();
        int toUserID = transfer.getUserToID();
        double transferAmount = transfer.getAmount();

        TransferReturnedDTO transferToInsert = new TransferReturnedDTO();

        transferToInsert.setTransferTypeId(2);//default: SEND
        transferToInsert.setTransferStatusId(2);//default: APPROVED
        transferToInsert.setAccountFrom(accountDao.getAccountByUserId(fromUserID).getAccountId());
        transferToInsert.setAccountTo(accountDao.getAccountByUserId(toUserID).getAccountId());
        transferToInsert.setTransferAmount(transferAmount);
        // transferToInsert.setAccountTo(accountDao.getAccountNumberByUserID();) COULD WRITE METHOD
        transferDao.addTransfer(transferToInsert);

        //check from account balance
        if(transferAmount <= accountDao.getAccountByUserId(fromUserID).getBalance()){
            setBalance(fromUserID, transferAmount, true);
            setBalance(toUserID, transferAmount, false);
        } else {
            System.out.println("Insufficient Balance for Transfer");
        }
    }

    @RequestMapping(path="/approve_transfer")
    public void updateTransfer(@RequestBody TransferReturnedDTO transferApproved){
        /*int fromUserID =transferApproved.getAccountFrom()
        int toUserID = transferApproved.getUserToID();
        double transferAmount = transferApproved.getAmount();

        TransferReturnedDTO transferToUpdate = transferDao.getTransferById(transferApproved.getTransferId());
        transferToUpdate.setTransferStatusId(2);//status: APPROVED

        transferDao.updateTransfer(transferToUpdate);

        //check balance and adjust accounts
        transferToUpdate.setTransferTypeId(2);//default: SEND
        transferToUpdate.setTransferStatusId(2);//default: APPROVED
        transferToUpdate.setAccountFrom(accountDao.getAccountByUserId(fromUserID).getAccountId());
        transferToUpdate.setAccountTo(accountDao.getAccountByUserId(toUserID).getAccountId());
        transferToUpdate.setTransferAmount(transferAmount);
        // transferToInsert.setAccountTo(accountDao.getAccountNumberByUserID();) COULD WRITE METHOD
        transferDao.addTransfer(transferToUpdate);

        //check from account balance
        if(transferAmount <= accountDao.getAccountByUserId(fromUserID).getBalance()){
            setBalance(fromUserID, transferAmount, true);
            setBalance(toUserID, transferAmount, false);
        } else {
            System.out.println("Insufficient Balance for Transfer");
        }*/
    }

    @RequestMapping(path="/reject_transfer")
    public void rejectTransfer(@RequestBody TransferReturnedDTO transferApproved){

        TransferReturnedDTO transferToUpdate = transferDao.getTransferById(transferApproved.getTransferId());
        transferToUpdate.setTransferStatusId(3);//status: rejected

        transferDao.updateTransfer(transferToUpdate);
    }


    public void setBalance(int userId, double transferAmount, boolean isIncreasing){
        if(isIncreasing){
            Account fromAccount = new Account();
            fromAccount.setBalance(accountDao.getAccountByUserId(userId).getBalance() - transferAmount);//this is substracting
            fromAccount.setUserId(userId);
            accountDao.updateAccount(fromAccount, userId);
        } else {
            Account toAccount = new Account();
            toAccount.setBalance(accountDao.getAccountByUserId(userId).getBalance() + transferAmount); //this is adding
            toAccount.setUserId(userId);
            accountDao.updateAccount(toAccount, userId);
        }
    }

    @RequestMapping(path="/transfers", method = RequestMethod.GET)
    public TransferReturnedDTO[] getAllTransfers(Principal principal){

        TransferReturnedDTO[] transferReturnedDTOList = transferDao.getAllTransfers(principal);
        return transferReturnedDTOList;
    }

    @RequestMapping(path="/transfers_pending", method = RequestMethod.GET)
    public TransferReturnedDTO[] getAllPendingTransfers(Principal principal){

        TransferReturnedDTO[] transferReturnedDTOList = transferDao.getAllPendingTransfers(principal);
        return transferReturnedDTOList;
    }

}
