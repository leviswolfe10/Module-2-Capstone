package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

    @Autowired
    private UserDao userDao;


    @RequestMapping (path = "/users", method = RequestMethod.GET)
    public UserDTO[] listUsers(Principal principal){
        UserDTO[] userList = userDao.findAllNotPrincipal(principal);

        return userList;
    }

    @RequestMapping (path = "/allusers", method = RequestMethod.GET)
    public UserDTO[] listEveryUser(){

        UserDTO[] allUsers = userDao.findAllDTOs();;
        return allUsers;

    }


}
