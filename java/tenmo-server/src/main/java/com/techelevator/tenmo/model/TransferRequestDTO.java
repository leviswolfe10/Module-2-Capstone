package com.techelevator.tenmo.model;

public class TransferRequestDTO {
    private int transferID;
    private int userFromID;
    private int userToID;
    private double amount;

    public int getUserFromID() {
        return userFromID;
    }

    public void setUserFromID(int userFromID) {
        this.userFromID = userFromID;
    }

    public int getUserToID() {
        return userToID;
    }

    public void setUserToID(int userToID) {
        this.userToID = userToID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getTransferID() {
        return transferID;
    }

    public void setTransferID(int transferID) {
        this.transferID = transferID;
    }

    @Override
    public String toString() {
        return "TransferRequestDTO{" +
                "userFromID=" + userFromID +
                ", userToID=" + userToID +
                ", amount=" + amount +
                '}';
    }
}
