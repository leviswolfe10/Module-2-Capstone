package com.techelevator.tenmo.model;

public class BalanceDTO {

    private double balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "$" + balance;
    }
}
