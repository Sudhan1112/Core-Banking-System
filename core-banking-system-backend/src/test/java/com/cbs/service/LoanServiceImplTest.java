package com.cbs.service;

import com.cbs.model.dto.request.LoanApplicationRequest;
import com.cbs.model.dto.response.LoanResponse;
import com.cbs.model.entity.Loan;
import com.cbs.model.enums.LoanStatus;
import com.cbs.model.enums.LoanType;
import com.cbs.repository.LoanRepository;
import com.cbs.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Loan testLoan;
    private LoanApplicationRequest testLoanRequest;

    @BeforeEach
    void setUp() {
        testLoan = new Loan();
        testLoan.setLoanId(1L);
        testLoan.setAccountId(1L);
        testLoan.setLoanType(LoanType.PERSONAL);
        testLoan.setPrincipalAmount(new BigDecimal("10000.00"));
        testLoan.setLoanTermMonths(12);
        testLoan.setInterestRate(new BigDecimal("0.10"));
        testLoan.setMonthlyPayment(new BigDecimal("880.00"));
        testLoan.setStatus(LoanStatus.PENDING_APPROVAL);
        testLoan.setApplicationDate(LocalDate.now());
        testLoan.setCreatedAt(LocalDateTime.now());
        testLoan.setUpdatedAt(LocalDateTime.now());
        testLoan.setTotalInterest(new BigDecimal("500.00"));
        testLoan.setInterestType("FIXED");
        testLoan.setProcessingFees(new BigDecimal("100.00"));
        testLoan.setLatePaymentPenalty(new BigDecimal("50.00"));
        testLoan.setCollateralType("NONE");
        testLoan.setCollateralValue(new BigDecimal("0.00"));
        testLoan.setGuarantorId(0L);

        testLoanRequest = new LoanApplicationRequest();
        testLoanRequest.setAccountId(1L);
        testLoanRequest.setLoanType(LoanType.PERSONAL);
        testLoanRequest.setPrincipalAmount(new BigDecimal("10000.00"));
        testLoanRequest.setLoanTermMonths(12);
        testLoanRequest.setInterestRate(new BigDecimal("0.10"));
        testLoanRequest.setMonthlyPayment(new BigDecimal("880.00"));
        testLoanRequest.setTotalInterest(new BigDecimal("500.00"));
        testLoanRequest.setInterestType("FIXED");
        testLoanRequest.setProcessingFees("100.00"); // Request uses String for fees usually based on DTO check,
                                                     // cleaning up type later if needed
        testLoanRequest.setLatePaymentPenalty(new BigDecimal("50.00"));
        testLoanRequest.setCollateralType("NONE");
        testLoanRequest.setCollateralValue(new BigDecimal("0.00"));
        testLoanRequest.setGuarantorId(0L);
    }

    @Test
    @DisplayName("Should apply for a loan successfully")
    void applyForLoan_Success() {
        // Arrange
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);

        // Act
        LoanResponse result = loanService.applyForLoan(testLoanRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getLoanId());
        assertEquals(LoanStatus.PENDING_APPROVAL, result.getStatus());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should get loan by ID successfully")
    void getLoanById_Success() {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        // Act
        Optional<LoanResponse> result = loanService.getLoanById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getLoanId());
        verify(loanRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when get loan by ID fails")
    void getLoanById_NotFound() {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<LoanResponse> result = loanService.getLoanById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(loanRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get loans by account ID successfully")
    void getLoansByAccountId_Success() {
        // Arrange
        when(loanRepository.findByAccountId(1L)).thenReturn(List.of(testLoan));

        // Act
        List<LoanResponse> result = loanService.getLoansByAccountId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getLoanId());
        verify(loanRepository).findByAccountId(1L);
    }

    @Test
    @DisplayName("Should get loans by user ID - currently returns empty")
    void getLoansByUserId_Success() {
        // Act
        List<LoanResponse> result = loanService.getLoansByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        // No repo call expected yet as per impl
    }

    @Test
    @DisplayName("Should get loans by status successfully")
    void getLoansByStatus_Success() {
        // Arrange
        when(loanRepository.findByStatus(LoanStatus.PENDING_APPROVAL)).thenReturn(List.of(testLoan));

        // Act
        List<LoanResponse> result = loanService.getLoansByStatus(LoanStatus.PENDING_APPROVAL);

        // Assert
        assertEquals(1, result.size());
        assertEquals(LoanStatus.PENDING_APPROVAL, result.get(0).getStatus());
        verify(loanRepository).findByStatus(LoanStatus.PENDING_APPROVAL);
    }

    @Test
    @DisplayName("Should get loans by loan type successfully")
    void getLoansByLoanType_Success() {
        // Arrange
        when(loanRepository.findByLoanType(LoanType.PERSONAL)).thenReturn(List.of(testLoan));

        // Act
        List<LoanResponse> result = loanService.getLoansByLoanType(LoanType.PERSONAL);

        // Assert
        assertEquals(1, result.size());
        assertEquals(LoanType.PERSONAL, result.get(0).getLoanType());
        verify(loanRepository).findByLoanType(LoanType.PERSONAL);
    }
}
