package com.techelevator.tenmo.model;

public class Account {

    private int accountId;
    private int userId;
    private double balance;


    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", balance=" + balance +
                '}';
    }

    public double reduceBalance(TransferReturnedDTO transferReturnedDTO){
        setBalance(balance - transferReturnedDTO.getTransferAmount());
        return balance - transferReturnedDTO.getTransferAmount();
    }

    public double increaseBalance(TransferReturnedDTO transferReturnedDTO){
        setBalance(balance + transferReturnedDTO.getTransferAmount());
        return balance + transferReturnedDTO.getTransferAmount();
    }

}
