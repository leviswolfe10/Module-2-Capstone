package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferDetail {

    private int transferId;
    private String fromUser; //that is the string username assoc. with a user id
    private String toUser; //this is the string username assoc with a user id
    private String transferType; //transfer type desc. assoc with a transfer type id
    private String status; //transfer status desc. assoc with a transfer status id
    private BigDecimal transferAmount; //

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    @Override
    public String toString() {
        return "TransferDetail{" +
                "transferId=" + transferId +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", transferType='" + transferType + '\'' +
                ", status='" + status + '\'' +
                ", transferAmount=" + transferAmount +
                '}';
    }
}
