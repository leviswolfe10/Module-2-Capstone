package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.BalanceDTO;

public interface AccountDao {

    public Account getAccountByUserId(int id);

    public void updateAccount(Account account, int id);

    public void deleteAccount(int accountId);

    public Account getBalanceFromUserId(int userId);
    public double returnAccountBalance(int userId);



}
