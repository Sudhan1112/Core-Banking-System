package com.cbs.integration;

import com.cbs.model.dto.request.AccountCreationRequest;
import com.cbs.model.dto.response.AccountResponse;
import com.cbs.model.enums.AccountStatus;
import com.cbs.model.enums.AccountType;
import com.cbs.repository.AccountRepository;
import com.cbs.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import com.cbs.config.TestSecurityConfig;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class AccountIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private AccountCreationRequest testAccountRequest;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/accounts";
        accountRepository.deleteAll();
        
        testAccountRequest = new AccountCreationRequest();
        testAccountRequest.setAccountType(AccountType.SAVINGS);
        testAccountRequest.setUserId(1L);
        testAccountRequest.setInitialDeposit(new BigDecimal("1000.00"));
        testAccountRequest.setMinimumBalance(new BigDecimal("0.00"));
    }

    @Test
    @DisplayName("Should create and retrieve an account")
    void createAndRetrieveAccount_Success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccountCreationRequest> request = new HttpEntity<>(testAccountRequest, headers);

        // Act - Create account
        ResponseEntity<AccountResponse> createResponse = restTemplate.exchange(
                baseUrl, HttpMethod.POST, request, AccountResponse.class);

        // Assert - Creation
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertEquals(AccountType.SAVINGS, createResponse.getBody().getAccountType());
        assertTrue(createResponse.getBody().getBalance().compareTo(new BigDecimal("1000.00")) == 0);

        // Act - Get account by ID
        ResponseEntity<AccountResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + createResponse.getBody().getAccountId(), AccountResponse.class);

        // Assert - Get account
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(createResponse.getBody().getAccountNumber(), getResponse.getBody().getAccountNumber());
    }

    @Test
    @DisplayName("Should get accounts by user ID")
    void getAccountsByUserId_Success() {
        // Arrange - Create two accounts for the same user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccountCreationRequest> request1 = new HttpEntity<>(testAccountRequest, headers);
        restTemplate.exchange(baseUrl, HttpMethod.POST, request1, AccountResponse.class);

        AccountCreationRequest secondAccount = new AccountCreationRequest();
        secondAccount.setAccountType(AccountType.CURRENT);
        secondAccount.setUserId(1L);
        secondAccount.setInitialDeposit(new BigDecimal("500.00"));
        HttpEntity<AccountCreationRequest> request2 = new HttpEntity<>(secondAccount, headers);
        restTemplate.exchange(baseUrl, HttpMethod.POST, request2, AccountResponse.class);

        // Act - Get accounts by user ID
        ResponseEntity<AccountResponse[]> response = restTemplate.getForEntity(
                baseUrl + "/user/1", AccountResponse[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        List<AccountType> accountTypes = Arrays.stream(response.getBody())
                .map(AccountResponse::getAccountType)
                .toList();
        assertTrue(accountTypes.contains(AccountType.SAVINGS));
        assertTrue(accountTypes.contains(AccountType.CURRENT));
    }

    @Test
    @DisplayName("Should deposit to account successfully")
    void deposit_Success() {
        // Arrange - Create an account
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccountCreationRequest> createRequest = new HttpEntity<>(testAccountRequest, headers);
        ResponseEntity<AccountResponse> createResponse = restTemplate.exchange(
                baseUrl, HttpMethod.POST, createRequest, AccountResponse.class);

        // Create deposit request
        HttpEntity<Map<String, BigDecimal>> depositRequest = new HttpEntity<>(
                Map.of("amount", new BigDecimal("500.00")), headers);

        // Act - Deposit
        ResponseEntity<AccountResponse> depositResponse = restTemplate.exchange(
                baseUrl + "/" + createResponse.getBody().getAccountId() + "/deposit",
                HttpMethod.POST, depositRequest, AccountResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, depositResponse.getStatusCode());
        assertNotNull(depositResponse.getBody());
        assertTrue(depositResponse.getBody().getBalance().compareTo(new BigDecimal("1500.00")) == 0);
    }

    @Test
    @DisplayName("Should withdraw from account successfully")
    void withdraw_Success() {
        // Arrange - Create an account
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccountCreationRequest> createRequest = new HttpEntity<>(testAccountRequest, headers);
        ResponseEntity<AccountResponse> createResponse = restTemplate.exchange(
                baseUrl, HttpMethod.POST, createRequest, AccountResponse.class);

        // Create withdraw request
        HttpEntity<Map<String, BigDecimal>> withdrawRequest = new HttpEntity<>(
                Map.of("amount", new BigDecimal("300.00")), headers);

        // Act - Withdraw
        ResponseEntity<AccountResponse> withdrawResponse = restTemplate.exchange(
                baseUrl + "/" + createResponse.getBody().getAccountId() + "/withdraw",
                HttpMethod.POST, withdrawRequest, AccountResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, withdrawResponse.getStatusCode());
        assertNotNull(withdrawResponse.getBody());
        assertTrue(withdrawResponse.getBody().getBalance().compareTo(new BigDecimal("700.00")) == 0);
    }

    @Test
    @DisplayName("Should update account status successfully")
    void updateAccountStatus_Success() {
        // Arrange - Create an account
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccountCreationRequest> createRequest = new HttpEntity<>(testAccountRequest, headers);
        ResponseEntity<AccountResponse> createResponse = restTemplate.exchange(
                baseUrl, HttpMethod.POST, createRequest, AccountResponse.class);

        // Create status update request
        HttpEntity<Map<String, String>> statusRequest = new HttpEntity<>(
                Map.of("status", "FROZEN"), headers);

        // Act - Update status
        ResponseEntity<AccountResponse> statusResponse = restTemplate.exchange(
                baseUrl + "/" + createResponse.getBody().getAccountId() + "/status",
                HttpMethod.PUT, statusRequest, AccountResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, statusResponse.getStatusCode());
        assertNotNull(statusResponse.getBody());
        assertEquals(AccountStatus.FROZEN, statusResponse.getBody().getStatus());
    }

    @Test
    @DisplayName("Should check if account exists")
    void checkAccountExists_Success() {
        // Arrange - Create an account
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccountCreationRequest> createRequest = new HttpEntity<>(testAccountRequest, headers);
        ResponseEntity<AccountResponse> createResponse = restTemplate.exchange(
                baseUrl, HttpMethod.POST, createRequest, AccountResponse.class);

        // Act - Check if account exists
        ResponseEntity<Boolean> response = restTemplate.getForEntity(
                baseUrl + "/exists/" + createResponse.getBody().getAccountNumber(), Boolean.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        // Check non-existent account
        ResponseEntity<Boolean> nonExistentResponse = restTemplate.getForEntity(
                baseUrl + "/exists/NONEXISTENT", Boolean.class);
        assertEquals(HttpStatus.OK, nonExistentResponse.getStatusCode());
        assertFalse(nonExistentResponse.getBody());
    }
}