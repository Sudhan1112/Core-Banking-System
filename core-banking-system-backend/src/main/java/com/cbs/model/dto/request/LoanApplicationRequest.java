package com.cbs.model.dto.request;

import com.cbs.model.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanApplicationRequest {

    @NotNull(message = "Account ID is required")
    @Positive(message = "Account ID must be positive")
    private Long accountId;

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @DecimalMin(value = "1000.00", message = "Principal amount must be at least 1000.00")
    private BigDecimal principalAmount;

    @DecimalMin(value = "12", message = "Loan term must be at least 12 months")
    private Integer loanTermMonths;

    @DecimalMin(value = "0.01", message = "Interest rate must be at least 0.1%")
    private BigDecimal interestRate;

    @DecimalMin(value = "0.0", message = "Monthly payment must be at least 0.01")
    private BigDecimal monthlyPayment;

    private String collateralDescription;

    private String guarantorInfo;

    private String collateralDocuments;

    private String collateralType;

    private BigDecimal collateralValue;

    private Long guarantorId;

    private String interestType;

    private String processingFees;

    private BigDecimal latePaymentPenalty;

    private BigDecimal totalInterest;

    private LocalDate applicationDate;

    private LocalDate approvalDate;

    private LocalDate disbursementDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Getters and setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
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

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getCollateralDescription() {
        return collateralDescription;
    }

    public void setCollateralDescription(String collateralDescription) {
        this.collateralDescription = collateralDescription;
    }

    public String getCollateralType() {
        return collateralType;
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

    public String getProcessingFees() {
        return processingFees;
    }

    public BigDecimal getLatePaymentPenalty() {
        return latePaymentPenalty;
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

    public void setDisbursementDate(LocalDate disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setGuarantorInfo(String guarantorInfo) {
        this.guarantorInfo = guarantorInfo;
    }

    public String getGuarantorInfo() {
        return guarantorInfo;
    }

    public void setCollateralDocuments(String collateralDocuments) {
        this.collateralDocuments = collateralDocuments;
    }

    public String getCollateralDocuments() {
        return collateralDocuments;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType;
    }

    public void setCollateralValue(BigDecimal collateralValue) {
        this.collateralValue = collateralValue;
    }

    public void setGuarantorId(Long guarantorId) {
        this.guarantorId = guarantorId;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public void setProcessingFees(String processingFees) {
        this.processingFees = processingFees;
    }

    public void setLatePaymentPenalty(BigDecimal latePaymentPenalty) {
        this.latePaymentPenalty = latePaymentPenalty;
    }
}