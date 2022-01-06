package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken){this.authToken = authToken;}

    // accept an Array
    public List<UserDTO> getListOfUsersNotUS(){


        List<UserDTO> userList = new ArrayList<>();
        try {
            ResponseEntity<UserDTO[]> response =
                    restTemplate.exchange(API_BASE_URL + "users",
                            HttpMethod.GET, makeAuthEntity(), UserDTO[].class);
            userList = Arrays.asList(response.getBody());
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
            System.out.println("There was a get list issue");
            //BasicLogger.log(e.getMessage());
        }
        return userList;
    }

    public List<UserDTO> getListOfUsersIncludingUS(){

        List<UserDTO> userList = new ArrayList<>();
        try {
            ResponseEntity<UserDTO[]> response =
                    restTemplate.exchange(API_BASE_URL + "allusers",
                            HttpMethod.GET, makeAuthEntity(), UserDTO[].class);
            userList = Arrays.asList(response.getBody());
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
            System.out.println("There was a get list issue");
            //BasicLogger.log(e.getMessage());
        }
        return userList;
    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
