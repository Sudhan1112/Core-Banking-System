package com.cbs.controller;

import com.cbs.config.SecurityConfig;
import com.cbs.model.dto.request.LoanApplicationRequest;
import com.cbs.model.dto.response.LoanResponse;
import com.cbs.model.enums.LoanStatus;
import com.cbs.model.enums.LoanType;
import com.cbs.service.impl.CustomUserDetailsService;
import com.cbs.service.interface_.LoanService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
@Import(SecurityConfig.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanResponse testLoanResponse;
    private LoanApplicationRequest testLoanRequest;

    @BeforeEach
    void setUp() {
        testLoanResponse = new LoanResponse();
        testLoanResponse.setLoanId(1L);
        testLoanResponse.setAccountId(1L);
        testLoanResponse.setLoanType(LoanType.PERSONAL);
        testLoanResponse.setPrincipalAmount(new BigDecimal("10000.00"));
        testLoanResponse.setStatus(LoanStatus.PENDING_APPROVAL);
        testLoanResponse.setApplicationDate(LocalDate.now());
        testLoanResponse.setCreatedAt(LocalDateTime.now());
        // set other fields as necessary

        testLoanRequest = new LoanApplicationRequest();
        testLoanRequest.setAccountId(1L);
        testLoanRequest.setLoanType(LoanType.PERSONAL);
        testLoanRequest.setPrincipalAmount(new BigDecimal("10000.00"));
        testLoanRequest.setLoanTermMonths(12);
        testLoanRequest.setInterestRate(new BigDecimal("0.10"));
        testLoanRequest.setMonthlyPayment(new BigDecimal("880.00"));
        testLoanRequest.setTotalInterest(new BigDecimal("560.00")); // approx
        testLoanRequest.setInterestType("FIXED");
        testLoanRequest.setLatePaymentPenalty(new BigDecimal("50.00"));
        testLoanRequest.setCollateralType("NONE");
        testLoanRequest.setCollateralValue(new BigDecimal("0.00"));
        testLoanRequest.setGuarantorId(0L);
    }

    @Test
    @DisplayName("Should apply for loan successfully")
    @WithMockUser
    void applyForLoan_Success() throws Exception {
        when(loanService.applyForLoan(any(LoanApplicationRequest.class))).thenReturn(testLoanResponse);

        mockMvc.perform(post("/api/loans/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLoanRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"));
    }

    @Test
    @DisplayName("Should get loan by ID successfully")
    @WithMockUser
    void getLoanById_Success() throws Exception {
        when(loanService.getLoanById(1L)).thenReturn(Optional.of(testLoanResponse));

        mockMvc.perform(get("/api/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(1));
    }

    @Test
    @DisplayName("Should return 404 when loan not found by ID")
    @WithMockUser
    void getLoanById_NotFound() throws Exception {
        when(loanService.getLoanById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/loans/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get loans by account ID successfully")
    @WithMockUser
    void getLoansByAccountId_Success() throws Exception {
        when(loanService.getLoansByAccountId(1L)).thenReturn(List.of(testLoanResponse));

        mockMvc.perform(get("/api/loans/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1));
    }

    @Test
    @DisplayName("Should get loans by user ID successfully")
    @WithMockUser
    void getLoansByUserId_Success() throws Exception {
        when(loanService.getLoansByUserId(1L)).thenReturn(List.of(testLoanResponse));

        mockMvc.perform(get("/api/loans/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1));
    }

    @Test
    @DisplayName("Should get loans by status successfully")
    @WithMockUser
    void getLoansByStatus_Success() throws Exception {
        when(loanService.getLoansByStatus(LoanStatus.PENDING_APPROVAL)).thenReturn(List.of(testLoanResponse));

        mockMvc.perform(get("/api/loans/status/PENDING_APPROVAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING_APPROVAL"));
    }

    @Test
    @DisplayName("Should get loans by type successfully")
    @WithMockUser
    void getLoansByType_Success() throws Exception {
        when(loanService.getLoansByLoanType(LoanType.PERSONAL)).thenReturn(List.of(testLoanResponse));

        mockMvc.perform(get("/api/loans/type/PERSONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanType").value("PERSONAL"));
    }
}
