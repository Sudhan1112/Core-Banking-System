package com.cbs.controller;

import com.cbs.model.dto.request.DepositRequest;
import com.cbs.model.dto.request.TransferRequest;
import com.cbs.model.dto.request.WithdrawalRequest;
import com.cbs.model.dto.response.TransactionResponse;
import com.cbs.model.enums.TransactionStatus;
import com.cbs.model.enums.TransactionType;
import com.cbs.service.interface_.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionResponse testTransactionResponse;
    private DepositRequest testDepositRequest;
    private WithdrawalRequest testWithdrawalRequest;
    private TransferRequest testTransferRequest;

    @BeforeEach
    void setUp() {
        testTransactionResponse = new TransactionResponse();
        testTransactionResponse.setTransactionId(1L);
        testTransactionResponse.setTransactionReference("TXN2025012612345678901234");
        testTransactionResponse.setTransactionType(TransactionType.DEPOSIT);
        testTransactionResponse.setAmount(new BigDecimal("1000.00"));
        testTransactionResponse.setDestinationAccountId(1L);
        testTransactionResponse.setUserId(1L);
        testTransactionResponse.setStatus(TransactionStatus.COMPLETED);
        testTransactionResponse.setTransactionDate(LocalDateTime.now());
        testTransactionResponse.setBalanceAfter(new BigDecimal("2000.00"));

        testDepositRequest = new DepositRequest();
        testDepositRequest.setAccountId(1L);
        testDepositRequest.setAmount(new BigDecimal("1000.00"));
        testDepositRequest.setDescription("Test deposit");

        testWithdrawalRequest = new WithdrawalRequest();
        testWithdrawalRequest.setAccountId(1L);
        testWithdrawalRequest.setAmount(new BigDecimal("500.00"));
        testWithdrawalRequest.setDescription("Test withdrawal");

        testTransferRequest = new TransferRequest();
        testTransferRequest.setSourceAccountId(1L);
        testTransferRequest.setDestinationAccountId(2L);
        testTransferRequest.setAmount(new BigDecimal("300.00"));
        testTransferRequest.setDescription("Test transfer");
    }

    @Test
    @DisplayName("Should process deposit successfully")
    void deposit_Success() throws Exception {
        // Arrange
        when(transactionService.deposit(any(DepositRequest.class)))
                .thenReturn(testTransactionResponse);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDepositRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should process withdrawal successfully")
    void withdraw_Success() throws Exception {
        // Arrange
        TransactionResponse withdrawalResponse = new TransactionResponse();
        withdrawalResponse.setTransactionId(2L);
        withdrawalResponse.setTransactionType(TransactionType.WITHDRAWAL);
        withdrawalResponse.setAmount(new BigDecimal("500.00"));
        withdrawalResponse.setSourceAccountId(1L);
        withdrawalResponse.setStatus(TransactionStatus.COMPLETED);
        
        when(transactionService.withdraw(any(WithdrawalRequest.class)))
                .thenReturn(withdrawalResponse);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWithdrawalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionType").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    @DisplayName("Should return 400 when withdrawal fails")
    void withdraw_InsufficientBalance() throws Exception {
        // Arrange
        when(transactionService.withdraw(any(WithdrawalRequest.class)))
                .thenThrow(new RuntimeException("Insufficient balance"));

        // Act & Assert
        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWithdrawalRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient balance"));
    }

    @Test
    @DisplayName("Should process transfer successfully")
    void transfer_Success() throws Exception {
        // Arrange
        TransactionResponse transferResponse = new TransactionResponse();
        transferResponse.setTransactionId(3L);
        transferResponse.setTransactionType(TransactionType.TRANSFER);
        transferResponse.setAmount(new BigDecimal("300.00"));
        transferResponse.setSourceAccountId(1L);
        transferResponse.setDestinationAccountId(2L);
        transferResponse.setStatus(TransactionStatus.COMPLETED);
        
        when(transactionService.transfer(any(TransferRequest.class)))
                .thenReturn(transferResponse);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTransferRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionType").value("TRANSFER"))
                .andExpect(jsonPath("$.sourceAccountId").value(1))
                .andExpect(jsonPath("$.destinationAccountId").value(2));
    }

    @Test
    @DisplayName("Should get transaction by ID successfully")
    void getTransactionById_Success() throws Exception {
        // Arrange
        when(transactionService.getTransactionById(1L))
                .thenReturn(Optional.of(testTransactionResponse));

        // Act & Assert
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.transactionReference").value("TXN2025012612345678901234"));
    }

    @Test
    @DisplayName("Should return 404 when transaction not found")
    void getTransactionById_NotFound() throws Exception {
        // Arrange
        when(transactionService.getTransactionById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get transactions by account ID successfully")
    void getTransactionsByAccountId_Success() throws Exception {
        // Arrange
        List<TransactionResponse> transactions = Arrays.asList(testTransactionResponse);
        when(transactionService.getTransactionsByAccountId(1L))
                .thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].destinationAccountId").value(1))
                .andExpect(jsonPath("$[0].transactionType").value("DEPOSIT"));
    }

    @Test
    @DisplayName("Should get transactions by user ID successfully")
    void getTransactionsByUserId_Success() throws Exception {
        // Arrange
        List<TransactionResponse> transactions = Arrays.asList(testTransactionResponse);
        when(transactionService.getTransactionsByUserId(1L))
                .thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @DisplayName("Should reverse transaction successfully")
    void reverseTransaction_Success() throws Exception {
        // Arrange
        TransactionResponse reversalResponse = new TransactionResponse();
        reversalResponse.setTransactionId(4L);
        reversalResponse.setTransactionType(TransactionType.REFUND);
        reversalResponse.setAmount(new BigDecimal("1000.00"));
        reversalResponse.setStatus(TransactionStatus.COMPLETED);
        
        when(transactionService.reverseTransaction(1L))
                .thenReturn(reversalResponse);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/1/reverse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value("REFUND"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should get total debits for account")
    void getTotalDebits_Success() throws Exception {
        // Arrange
        when(transactionService.getTotalDebits(1L))
                .thenReturn(new BigDecimal("1500.00"));

        // Act & Assert
        mockMvc.perform(get("/api/transactions/account/1/debits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDebits").value(1500.00));
    }

    @Test
    @DisplayName("Should get total credits for account")
    void getTotalCredits_Success() throws Exception {
        // Arrange
        when(transactionService.getTotalCredits(1L))
                .thenReturn(new BigDecimal("2000.00"));

        // Act & Assert
        mockMvc.perform(get("/api/transactions/account/1/credits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCredits").value(2000.00));
    }
}