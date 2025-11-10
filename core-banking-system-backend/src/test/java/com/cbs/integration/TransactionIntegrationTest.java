package com.cbs.integration;

import com.cbs.config.TestSecurityConfig;
import com.cbs.model.dto.request.DepositRequest;
import org.springframework.context.annotation.Import;
import com.cbs.model.dto.request.TransferRequest;
import com.cbs.model.dto.request.WithdrawalRequest;
import com.cbs.model.dto.response.TransactionResponse;
import com.cbs.model.entity.Account;
import com.cbs.model.entity.User;
import com.cbs.model.enums.*;
import com.cbs.repository.AccountRepository;
import com.cbs.repository.TransactionRepository;
import com.cbs.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class TransactionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private DepositRequest testDepositRequest;
    private WithdrawalRequest testWithdrawalRequest;
    private TransferRequest testTransferRequest;

    private Long sourceAccountId;
    private Long destinationAccountId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/transactions";

        // Clean slate
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(testUser);

        // Create source account
        Account sourceAccount = new Account();
        sourceAccount.setAccountNumber("ACC001");
        sourceAccount.setAccountType(AccountType.SAVINGS);
        sourceAccount.setBalance(new BigDecimal("5000.00"));
        sourceAccount.setUserId(testUser.getUserId());
        sourceAccount.setStatus(AccountStatus.ACTIVE);
        sourceAccount.setMinimumBalance(new BigDecimal("1000.00"));
        sourceAccount = accountRepository.save(sourceAccount);
        sourceAccountId = sourceAccount.getAccountId();

        // Create destination account
        Account destinationAccount = new Account();
        destinationAccount.setAccountNumber("ACC002");
        destinationAccount.setAccountType(AccountType.SAVINGS);
        destinationAccount.setBalance(new BigDecimal("2000.00"));
        destinationAccount.setUserId(testUser.getUserId());
        destinationAccount.setStatus(AccountStatus.ACTIVE);
        destinationAccount.setMinimumBalance(new BigDecimal("500.00"));
        destinationAccount = accountRepository.save(destinationAccount);
        destinationAccountId = destinationAccount.getAccountId();

        // Setup DTOs
        testDepositRequest = new DepositRequest();
        testDepositRequest.setAccountId(sourceAccountId);
        testDepositRequest.setAmount(new BigDecimal("1000.00"));
        testDepositRequest.setDescription("Test deposit");

        testWithdrawalRequest = new WithdrawalRequest();
        testWithdrawalRequest.setAccountId(sourceAccountId);
        testWithdrawalRequest.setAmount(new BigDecimal("500.00"));
        testWithdrawalRequest.setDescription("Test withdrawal");

        testTransferRequest = new TransferRequest();
        testTransferRequest.setSourceAccountId(sourceAccountId);
        testTransferRequest.setDestinationAccountId(destinationAccountId);
        testTransferRequest.setAmount(new BigDecimal("300.00"));
        testTransferRequest.setDescription("Test transfer");
    }

    @Test
    @DisplayName("Should process deposit and retrieve transaction successfully")
    void depositAndRetrieveTransaction_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DepositRequest> request = new HttpEntity<>(testDepositRequest, headers);
        ResponseEntity<TransactionResponse> depositResponse = restTemplate.exchange(
                baseUrl + "/deposit", HttpMethod.POST, request, TransactionResponse.class);

        assertEquals(HttpStatus.CREATED, depositResponse.getStatusCode());
        assertNotNull(depositResponse.getBody());
        assertEquals(TransactionType.DEPOSIT, depositResponse.getBody().getTransactionType());

        // Fetch transaction by ID
        Long transactionId = depositResponse.getBody().getTransactionId();
        ResponseEntity<TransactionResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + transactionId, TransactionResponse.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(depositResponse.getBody().getTransactionReference(), getResponse.getBody().getTransactionReference());
    }

    @Test
    @DisplayName("Should process withdrawal successfully")
    void withdraw_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<WithdrawalRequest> request = new HttpEntity<>(testWithdrawalRequest, headers);

        ResponseEntity<TransactionResponse> response = restTemplate.exchange(
                baseUrl + "/withdraw", HttpMethod.POST, request, TransactionResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TransactionType.WITHDRAWAL, response.getBody().getTransactionType());
    }

    @Test
    @DisplayName("Should process transfer successfully")
    void transfer_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransferRequest> request = new HttpEntity<>(testTransferRequest, headers);

        ResponseEntity<TransactionResponse> response = restTemplate.exchange(
                baseUrl + "/transfer", HttpMethod.POST, request, TransactionResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TransactionType.TRANSFER, response.getBody().getTransactionType());
        assertEquals(sourceAccountId, response.getBody().getSourceAccountId());
        assertEquals(destinationAccountId, response.getBody().getDestinationAccountId());
    }

    @Test
    @DisplayName("Should get transactions by account ID")
    void getTransactionsByAccountId_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create deposit + withdraw
        restTemplate.postForEntity(baseUrl + "/deposit", new HttpEntity<>(testDepositRequest, headers), TransactionResponse.class);
        restTemplate.postForEntity(baseUrl + "/withdraw", new HttpEntity<>(testWithdrawalRequest, headers), TransactionResponse.class);

        ResponseEntity<TransactionResponse[]> response = restTemplate.getForEntity(
                baseUrl + "/account/" + sourceAccountId, TransactionResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<TransactionType> types = Arrays.stream(response.getBody())
                .map(TransactionResponse::getTransactionType)
                .toList();

        assertTrue(types.contains(TransactionType.DEPOSIT));
        assertTrue(types.contains(TransactionType.WITHDRAWAL));
    }

    @Test
    @DisplayName("Should reverse transaction successfully")
    void reverseTransaction_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create deposit
        ResponseEntity<TransactionResponse> depositResponse = restTemplate.postForEntity(
                baseUrl + "/deposit", new HttpEntity<>(testDepositRequest, headers), TransactionResponse.class);

        Long transactionId = depositResponse.getBody().getTransactionId();

        ResponseEntity<TransactionResponse> reverseResponse = restTemplate.exchange(
                baseUrl + "/" + transactionId + "/reverse", HttpMethod.POST, null, TransactionResponse.class);

        assertEquals(HttpStatus.OK, reverseResponse.getStatusCode());
        assertNotNull(reverseResponse.getBody());
        assertEquals(TransactionType.REFUND, reverseResponse.getBody().getTransactionType());
        assertEquals(TransactionStatus.COMPLETED, reverseResponse.getBody().getStatus());
    }

    @Test
    @DisplayName("Should get total debits and credits for account")
    void getTotalDebitsAndCredits_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(baseUrl + "/deposit", new HttpEntity<>(testDepositRequest, headers), TransactionResponse.class);
        restTemplate.postForEntity(baseUrl + "/withdraw", new HttpEntity<>(testWithdrawalRequest, headers), TransactionResponse.class);

        Class<Map<String, Object>> responseType = (Class<Map<String, Object>>) (Class<?>) Map.class;
        ResponseEntity<Map<String, Object>> debitsResponse = restTemplate.getForEntity(
                baseUrl + "/account/" + sourceAccountId + "/debits", responseType);
        ResponseEntity<Map<String, Object>> creditsResponse = restTemplate.getForEntity(
                baseUrl + "/account/" + sourceAccountId + "/credits", responseType);

        assertEquals(HttpStatus.OK, debitsResponse.getStatusCode());
        assertEquals(HttpStatus.OK, creditsResponse.getStatusCode());
        assertNotNull(debitsResponse.getBody().get("totalDebits"));
        assertNotNull(creditsResponse.getBody().get("totalCredits"));
    }
}
