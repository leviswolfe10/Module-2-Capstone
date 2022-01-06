package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.BalanceDTO;
import io.cucumber.java.en_old.Ac;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken){this.authToken = authToken;}

    /*public Account getAccountByUserID(int userID){

        Account retrievedAccount= restTemplate.getForObject(API_BASE_URL + "accounts/" + userID, Account.class);
        return retrievedAccount;
    }*/


    public double getAccountBalance(){

        ResponseEntity<Double> balance = restTemplate.exchange(API_BASE_URL + "balance" , HttpMethod.GET, makeAuthEntity(), Double.class);

        return balance.getBody();
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
