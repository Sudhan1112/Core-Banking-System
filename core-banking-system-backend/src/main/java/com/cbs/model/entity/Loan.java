package com.cbs.model.entity;

import com.cbs.model.enums.LoanStatus;
import com.cbs.model.enums.LoanType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "loan_term_months", nullable = false)
    private Integer loanTermMonths;

    @Column(name = "monthly_payment", nullable = false, precision = 19, scale = 4)
    private BigDecimal monthlyPayment;

    @Column(name = "loan_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @Column(name = "total_interest", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalInterest;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "approval_date", nullable = true)
    private LocalDate approvalDate;

    @Column(name = "disbursement_date", nullable = true)
    private LocalDate disbursementDate;

    @Column(name = "maturity_date", nullable = true)
    private LocalDate maturityDate;

    @Column(name = "collateral_type", nullable = false)
    private String collateralType;

    @Column(name = "collateral_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal collateralValue;

    @Column(name = "guarantor_id", nullable = false)
    private Long guarantorId;

    @Column(name = "interest_type", nullable = false)
    private String interestType;

    @Column(name = "processing_fees", nullable = false, precision = 19, scale = 4)
    private BigDecimal processingFees;

    @Column(name = "late_payment_penalty", nullable = false, precision = 19, scale = 4)
    private BigDecimal latePaymentPenalty;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor
    public Loan() {
        this.createdAt = LocalDateTime.now();
        this.status = LoanStatus.PENDING_APPROVAL;
        this.loanTermMonths = 12; // Default to 1 year
    }

    // Getters and setters
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Integer getLoanTermMonths() {
        return loanTermMonths;
    }

    public void setLoanTermMonths(Integer loanTermMonths) {
        this.loanTermMonths = loanTermMonths;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public LocalDate getDisbursementDate() {
        return disbursementDate;
    }

    public String getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType;
    }

    public BigDecimal getCollateralValue() {
        return collateralValue;
    }

    public Long getGuarantorId() {
        return guarantorId;
    }

    public String getInterestType() {
        return interestType;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public BigDecimal getProcessingFees() {
        return processingFees;
    }

    public void setProcessingFees(BigDecimal processingFees) {
        this.processingFees = processingFees;
    }

    public BigDecimal getLatePaymentPenalty() {
        return latePaymentPenalty;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}