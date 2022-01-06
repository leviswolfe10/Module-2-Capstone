package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferRequestDTO;
import com.techelevator.tenmo.model.TransferReturnedDTO;

import java.security.Principal;
import java.util.List;

public interface TransferDao {

    public TransferReturnedDTO[] getAllTransfers(Principal principal);

    public TransferReturnedDTO[] getAllPendingTransfers(Principal principal);

    public TransferReturnedDTO getTransferById(int transferId);

    public void addTransfer(TransferReturnedDTO transferReturnedDTO);

    public void addTransferRequest(TransferReturnedDTO transferReturnedDTO);

    public void updateTransfer(TransferReturnedDTO transferReturnedDTO);

    //public void deleteTransfer(int transferId);

    public String getTransferTypeDescByTransferId(int transferID);

    public String getTransferStatusDescByTransferId(int transferID);

}
