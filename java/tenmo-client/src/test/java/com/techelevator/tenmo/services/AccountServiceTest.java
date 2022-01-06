package com.techelevator.tenmo.services;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccountServiceTest {

    @Test
    public void getAccountBalance() {
        AccountService accountService = new AccountService();
        double expect = 199.00;

    }

    //how do we test something with a changing database platform
    //this probably needs to be an integration test
}