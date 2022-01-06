package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.TransferRequestDTO;
import com.techelevator.tenmo.model.TransferReturnedDTO;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransferService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public void makeTransfer(TransferRequestDTO transferRequest) {

        restTemplate.postForObject(API_BASE_URL + "make_transfer",
                makeTransferRequestEntity(transferRequest), TransferRequestDTO.class);

    }

    public void approveTransfer(TransferRequestDTO transferRequest) {

        restTemplate.postForObject(API_BASE_URL + "approve_transfer",
                makeTransferRequestEntity(transferRequest), TransferRequestDTO.class);

    }

    public void rejectTransfer(TransferReturnedDTO transferRequest) {

        restTemplate.postForObject(API_BASE_URL + "reject_transfer",
                makeTransferRequestEntity(transferRequest), TransferReturnedDTO.class);

    }

    private HttpEntity<TransferRequestDTO> makeTransferRequestEntity(TransferRequestDTO transferRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transferRequest, headers);
    }

    private HttpEntity<TransferReturnedDTO> makeTransferRequestEntity(TransferReturnedDTO transferRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transferRequest, headers);
    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    public List<TransferReturnedDTO> getTransfers(){
        List<TransferReturnedDTO> transferList = new ArrayList<>();
        try {
            ResponseEntity<TransferReturnedDTO[]> response =
                    restTemplate.exchange(API_BASE_URL + "transfers",
                            HttpMethod.GET, makeAuthEntity(), TransferReturnedDTO[].class);
            transferList = Arrays.asList(response.getBody());
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
            System.out.println("There was a get transfer list issue");
            //BasicLogger.log(e.getMessage());
        }
        return transferList;
    }

    public List<TransferReturnedDTO> getPendingTransfers(){
        List<TransferReturnedDTO> transferList = new ArrayList<>();
        try {
            ResponseEntity<TransferReturnedDTO[]> response =
                    restTemplate.exchange(API_BASE_URL + "transfers_pending",
                            HttpMethod.GET, makeAuthEntity(), TransferReturnedDTO[].class);
            transferList = Arrays.asList(response.getBody());
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
            System.out.println("There was a get transfer list issue");
            //BasicLogger.log(e.getMessage());
        }
        return transferList;
    }

    public void initiateTransferRequest(TransferRequestDTO transferRequest) {
        restTemplate.postForObject(API_BASE_URL + "initiate_transfer_request",
                makeTransferRequestEntity(transferRequest), TransferRequestDTO.class);
    }
}
