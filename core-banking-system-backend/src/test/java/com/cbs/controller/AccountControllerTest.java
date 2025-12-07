package com.cbs.controller;

import com.cbs.config.SecurityConfig;
import com.cbs.model.dto.request.AccountCreationRequest;
import com.cbs.model.dto.response.AccountResponse;
import com.cbs.model.enums.AccountStatus;
import com.cbs.model.enums.AccountType;
import com.cbs.service.impl.CustomUserDetailsService;
import com.cbs.service.interface_.AccountService;
import com.cbs.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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

@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
class AccountControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AccountService accountService;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private CustomUserDetailsService customUserDetailsService;

        @Autowired
        private ObjectMapper objectMapper;

        private AccountResponse testAccountResponse;
        private AccountCreationRequest testAccountRequest;

        @BeforeEach
        void setUp() {
                testAccountResponse = new AccountResponse();
                testAccountResponse.setAccountId(1L);
                testAccountResponse.setAccountNumber("SB250001001");
                testAccountResponse.setAccountType(AccountType.SAVINGS);
                testAccountResponse.setBalance(new BigDecimal("1000.00"));
                testAccountResponse.setUserId(1L);
                testAccountResponse.setStatus(AccountStatus.ACTIVE);
                testAccountResponse.setCreatedAt(LocalDateTime.now());
                testAccountResponse.setMinimumBalance(new BigDecimal("1000.00"));

                testAccountRequest = new AccountCreationRequest();
                testAccountRequest.setAccountType(AccountType.SAVINGS);
                testAccountRequest.setUserId(1L);
                testAccountRequest.setInitialDeposit(new BigDecimal("1000.00"));
                testAccountRequest.setMinimumBalance(new BigDecimal("1000.00"));
        }

        @Test
        @DisplayName("Should create a new account successfully")
        @WithMockUser
        void createAccount_Success() throws Exception {
                // Arrange
                when(accountService.createAccount(any(AccountCreationRequest.class)))
                                .thenReturn(testAccountResponse);

                // Act & Assert
                mockMvc.perform(post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testAccountRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.accountId").value(1))
                                .andExpect(jsonPath("$.accountNumber").value("SB250001001"))
                                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                                .andExpect(jsonPath("$.balance").value(1000.00));
        }

        @Test
        @DisplayName("Should get account by ID successfully")
        @WithMockUser
        void getAccountById_Success() throws Exception {
                // Arrange
                when(accountService.getAccountById(1L)).thenReturn(Optional.of(testAccountResponse));

                // Act & Assert
                mockMvc.perform(get("/api/accounts/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accountId").value(1))
                                .andExpect(jsonPath("$.accountNumber").value("SB250001001"));
        }

        @Test
        @DisplayName("Should return 404 when account not found by ID")
        @WithMockUser
        void getAccountById_NotFound() throws Exception {
                // Arrange
                when(accountService.getAccountById(999L)).thenReturn(Optional.empty());

                // Act & Assert
                mockMvc.perform(get("/api/accounts/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should get account by number successfully")
        @WithMockUser
        void getAccountByNumber_Success() throws Exception {
                // Arrange
                when(accountService.getAccountByNumber("SB250001001")).thenReturn(Optional.of(testAccountResponse));

                // Act & Assert
                mockMvc.perform(get("/api/accounts/number/SB250001001"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accountNumber").value("SB250001001"));
        }

        @Test
        @DisplayName("Should get accounts by user ID successfully")
        @WithMockUser
        void getAccountsByUserId_Success() throws Exception {
                // Arrange
                List<AccountResponse> accounts = Arrays.asList(testAccountResponse);
                when(accountService.getAccountsByUserId(1L)).thenReturn(accounts);

                // Act & Assert
                mockMvc.perform(get("/api/accounts/user/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].userId").value(1))
                                .andExpect(jsonPath("$[0].accountNumber").value("SB250001001"));
        }

        @Test
        @DisplayName("Should deposit to account successfully")
        @WithMockUser
        void deposit_Success() throws Exception {
                // Arrange
                AccountResponse updatedAccount = new AccountResponse();
                updatedAccount.setAccountId(1L);
                updatedAccount.setAccountNumber("SB250001001");
                updatedAccount.setBalance(new BigDecimal("1500.00"));

                when(accountService.deposit(eq(1L), eq(new BigDecimal("500.00"))))
                                .thenReturn(updatedAccount);

                // Act & Assert
                mockMvc.perform(post("/api/accounts/1/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("amount", new BigDecimal("500.00")))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.balance").value(1500.00));
        }

        @Test
        @DisplayName("Should withdraw from account successfully")
        @WithMockUser
        void withdraw_Success() throws Exception {
                // Arrange
                AccountResponse updatedAccount = new AccountResponse();
                updatedAccount.setAccountId(1L);
                updatedAccount.setAccountNumber("SB250001001");
                updatedAccount.setBalance(new BigDecimal("500.00"));

                when(accountService.withdraw(eq(1L), eq(new BigDecimal("500.00"))))
                                .thenReturn(updatedAccount);

                // Act & Assert
                mockMvc.perform(post("/api/accounts/1/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("amount", new BigDecimal("500.00")))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.balance").value(500.00));
        }

        @Test
        @DisplayName("Should return 400 when withdrawal fails due to insufficient balance")
        @WithMockUser
        void withdraw_InsufficientBalance() throws Exception {
                // Arrange
                when(accountService.withdraw(eq(1L), eq(new BigDecimal("2000.00"))))
                                .thenThrow(new RuntimeException("Insufficient balance"));

                // Act & Assert
                mockMvc.perform(post("/api/accounts/1/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("amount", new BigDecimal("2000.00")))))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Insufficient balance"));
        }

        @Test
        @DisplayName("Should update account status successfully")
        @WithMockUser
        void updateAccountStatus_Success() throws Exception {
                // Arrange
                AccountResponse updatedAccount = new AccountResponse();
                updatedAccount.setAccountId(1L);
                updatedAccount.setStatus(AccountStatus.FROZEN);

                when(accountService.updateAccountStatus(eq(1L), eq("FROZEN")))
                                .thenReturn(updatedAccount);

                // Act & Assert
                mockMvc.perform(put("/api/accounts/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("status", "FROZEN"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("FROZEN"));
        }

        @Test
        @DisplayName("Should check if account exists")
        @WithMockUser
        void checkAccountExists_True() throws Exception {
                // Arrange
                when(accountService.existsByAccountNumber("SB250001001")).thenReturn(true);

                // Act & Assert
                mockMvc.perform(get("/api/accounts/exists/SB250001001"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("true"));
        }
}