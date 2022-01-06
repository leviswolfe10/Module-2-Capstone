package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.BalanceDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getBalanceFromUserId(int userId){

        String sql = "SELECT account_id, user_id, balance FROM accounts " +
                "WHERE user_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

        Account returnedBalance = mapRowToAccount(results);


        return returnedBalance;
    }

    public double returnAccountBalance(int userId) {
        String sql = "SELECT balance FROM accounts " +
                "WHERE user_id = ?";
        Double balance = jdbcTemplate.queryForObject(sql, Double.class, userId);
        if (balance != null) {
            return balance;
        } else {
            return -1;
        }
    }



    @Override
    public Account getAccountByUserId(int userId) { //by user id
        Account temp = new Account();
        String sql = "SELECT account_id, user_id, balance FROM accounts " +
                "WHERE user_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if(results.next()) {
            temp = mapRowToAccount(results);
        }
        return temp;
    }


    @Override
    public void updateAccount(Account account, int userId) {

        String sql = "UPDATE accounts SET balance = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, account.getBalance(), userId);

    }

    @Override
    public void deleteAccount(int userId) {
        String sql = "DELETE FROM accounts WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);

    }

    //helper method
    private Account mapRowToAccount(SqlRowSet results){
        Account account = new Account();

        account.setAccountId(results.getInt("account_id"));
        account.setUserId(results.getInt("user_id"));
        account.setBalance(results.getDouble("balance"));

        return account;
    }


}
