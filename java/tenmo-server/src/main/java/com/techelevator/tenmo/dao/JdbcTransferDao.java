package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.TransferReturnedDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {
    private JdbcTemplate jdbcTemplate;
    private JdbcUserDao userDao;
    private JdbcAccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, JdbcUserDao userDao, JdbcAccountDao accountDao){
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @Override
    public TransferReturnedDTO[] getAllTransfers(Principal principal) {  //user ID from Principal
        List<TransferReturnedDTO> transferReturnedDTOList = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers WHERE (transfer_status_id = ?) AND (account_from = ? OR account_to = ?)";

        int principalUserID = userDao.findIdByUsername(principal.getName());
        int principalAccountID = accountDao.getAccountByUserId(principalUserID).getAccountId();

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,2, principalAccountID, principalAccountID);

        while(results.next()){
            TransferReturnedDTO temp = mapRowToTransfer(results);
            temp.setTransferStatusDesc(getTransferStatusDescByTransferId(temp.getTransferId()));
            temp.setTransferTypeDesc(getTransferTypeDescByTransferId(temp.getTransferId()));
            transferReturnedDTOList.add(temp);
        }

        TransferReturnedDTO[] returnTransferArray = new TransferReturnedDTO[transferReturnedDTOList.size()];
        returnTransferArray = transferReturnedDTOList.toArray(returnTransferArray);
        return returnTransferArray;
    }

    @Override
    public TransferReturnedDTO[] getAllPendingTransfers(Principal principal) {
        List<TransferReturnedDTO> transferReturnedDTOList = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers WHERE (transfer_status_id = 1) AND (account_from = ? OR account_to = ?)";

        int principalUserID = userDao.findIdByUsername(principal.getName());
        int principalAccountID = accountDao.getAccountByUserId(principalUserID).getAccountId();

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, principalAccountID, principalAccountID);

        while(results.next()){
            TransferReturnedDTO temp = mapRowToTransfer(results);
            temp.setTransferStatusDesc(getTransferStatusDescByTransferId(temp.getTransferId()));
            temp.setTransferTypeDesc(getTransferTypeDescByTransferId(temp.getTransferId()));
            transferReturnedDTOList.add(temp);
        }

        TransferReturnedDTO[] returnTransferArray = new TransferReturnedDTO[transferReturnedDTOList.size()];
        returnTransferArray = transferReturnedDTOList.toArray(returnTransferArray);
        return returnTransferArray;
    }

    @Override
    public TransferReturnedDTO getTransferById(int transferId) {
        TransferReturnedDTO temp = new TransferReturnedDTO();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);

        temp = mapRowToTransfer(results);
        return temp;
    }

    @Override
    public String getTransferTypeDescByTransferId(int transferID){
        String transferDesc;

        String sql = "SELECT transfer_type_desc FROM transfer_types " +
                "WHERE transfer_type_id = (SELECT transfer_type_id FROM transfers WHERE transfer_id = ?)";
       transferDesc = jdbcTemplate.queryForObject(sql, String.class, transferID);


        return transferDesc;
    }

    @Override
    public String getTransferStatusDescByTransferId(int transferID){
        String transferDesc;

        String sql = "SELECT transfer_status_desc FROM transfer_statuses " +
                "WHERE transfer_status_id = (SELECT transfer_status_id FROM transfers WHERE transfer_id = ?)";
        transferDesc = jdbcTemplate.queryForObject(sql, String.class, transferID);

        return transferDesc;
    }

    @Override
    public void addTransfer(TransferReturnedDTO transferReturnedDTO) {
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
        int id = jdbcTemplate.queryForObject(sql, Integer.class, transferReturnedDTO.getTransferTypeId(), transferReturnedDTO.getTransferStatusId(),
                transferReturnedDTO.getAccountFrom(), transferReturnedDTO.getAccountTo(), transferReturnedDTO.getTransferAmount());

    }

    //requested, pending
    @Override
    public void addTransferRequest(TransferReturnedDTO transferReturnedDTO) {
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
        int id = jdbcTemplate.queryForObject(sql, Integer.class, transferReturnedDTO.getTransferTypeId(), transferReturnedDTO.getTransferStatusId(),
                transferReturnedDTO.getAccountFrom(), transferReturnedDTO.getAccountTo(), transferReturnedDTO.getTransferAmount());
    }

    @Override
    public void updateTransfer(TransferReturnedDTO transferReturnedDTO) {
        String sql = "UPDATE transfers SET transfer_status = ? WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transferReturnedDTO.getTransferStatusId(), transferReturnedDTO.getTransferId());

    }

    //never used in app. Status updated to rejected, not displayed in
    // completed transfer history list client-side, but remains a record.
    //could be re-enabled for admin use
    /*@Override
    public void deleteTransfer(int transferId) {
        String sql = "DELETE transfers WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transferId);
    }*/

    //helper method
    private TransferReturnedDTO mapRowToTransfer(SqlRowSet results){
        TransferReturnedDTO transferReturnedDTO = new TransferReturnedDTO();

        transferReturnedDTO.setTransferId(results.getInt("transfer_id"));
        transferReturnedDTO.setTransferTypeId(results.getInt("transfer_type_id"));
        transferReturnedDTO.setTransferStatusId(results.getInt("transfer_status_id"));
        transferReturnedDTO.setAccountFrom(results.getInt("account_from"));
        transferReturnedDTO.setAccountTo(results.getInt("account_to"));
        transferReturnedDTO.setTransferAmount(results.getDouble("amount"));

        return transferReturnedDTO;
    }
}
