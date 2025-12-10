package com.cbs.repository;

import com.cbs.model.entity.Loan;
import com.cbs.model.enums.LoanStatus;
import com.cbs.model.enums.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    private Loan testLoan;

    @BeforeEach
    void setUp() {
        testLoan = new Loan();
        testLoan.setAccountId(1L);
        testLoan.setLoanType(LoanType.PERSONAL);
        testLoan.setPrincipalAmount(new BigDecimal("10000.00"));
        testLoan.setInterestRate(new BigDecimal("0.10"));
        testLoan.setLoanTermMonths(12);
        testLoan.setMonthlyPayment(new BigDecimal("880.00"));
        testLoan.setTotalInterest(new BigDecimal("560.00"));
        testLoan.setStatus(LoanStatus.PENDING_APPROVAL);
        testLoan.setApplicationDate(LocalDate.now());
        testLoan.setCreatedAt(LocalDateTime.now());
        testLoan.setUpdatedAt(LocalDateTime.now());
        // Set other mandatory fields to avoid DataIntegrityViolationException
        testLoan.setCollateralType("NONE");
        testLoan.setCollateralValue(new BigDecimal("0.00"));
        testLoan.setGuarantorId(0L);
        testLoan.setInterestType("FIXED");
        testLoan.setProcessingFees(new BigDecimal("100.00"));
        testLoan.setLatePaymentPenalty(new BigDecimal("50.00"));

        entityManager.persistAndFlush(testLoan);
    }

    @Test
    @DisplayName("Should find loans by account ID")
    void findByAccountId_Success() {
        List<Loan> loans = loanRepository.findByAccountId(1L);
        assertFalse(loans.isEmpty());
        assertEquals(1L, loans.get(0).getAccountId());
    }

    @Test
    @DisplayName("Should find loans by status")
    void findByStatus_Success() {
        List<Loan> loans = loanRepository.findByStatus(LoanStatus.PENDING_APPROVAL);
        assertFalse(loans.isEmpty());
        assertEquals(LoanStatus.PENDING_APPROVAL, loans.get(0).getStatus());
    }

    @Test
    @DisplayName("Should find loans by loan type")
    void findByLoanType_Success() {
        List<Loan> loans = loanRepository.findByLoanType(LoanType.PERSONAL);
        assertFalse(loans.isEmpty());
        assertEquals(LoanType.PERSONAL, loans.get(0).getLoanType());
    }

    @Test
    @DisplayName("Should find loans by account ID and status")
    void findByAccountIdAndStatus_Success() {
        List<Loan> loans = loanRepository.findByAccountIdAndStatus(1L, LoanStatus.PENDING_APPROVAL);
        assertFalse(loans.isEmpty());
        assertEquals(1L, loans.get(0).getAccountId());
        assertEquals(LoanStatus.PENDING_APPROVAL, loans.get(0).getStatus());
    }
}
