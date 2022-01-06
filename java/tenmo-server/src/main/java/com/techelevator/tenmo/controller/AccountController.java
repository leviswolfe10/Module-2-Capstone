package com.techelevator.tenmo.controller;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.BalanceDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    @Autowired
    private AccountDao dao;
    @Autowired
    private UserDao user;


    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public double getBalance(Principal principal){
        int userId = user.findIdByUsername(principal.getName());
       return dao.returnAccountBalance(userId);
    }

    //@ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(path="/accounts/{id}", method = RequestMethod.PUT)
    public void updateAccount(@RequestBody Account account, @PathVariable int id){
        dao.updateAccount(account, id);
    }

    //@ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path="/accounts/{id}", method = RequestMethod.DELETE)
    public void deleteAccount(@PathVariable int id){
        dao.deleteAccount(id);
    }
}

