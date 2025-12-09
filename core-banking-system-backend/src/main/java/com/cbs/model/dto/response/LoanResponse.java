package com.cbs.model.dto.response;

import com.cbs.model.enums.LoanStatus;
import com.cbs.model.enums.LoanType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanResponse {
    private Long loanId;
    private Long accountId;
    private LoanType loanType;
    private BigDecimal principalAmount;
    private Integer loanTermMonths;
    private BigDecimal interestRate;
    private BigDecimal monthlyPayment;
    private BigDecimal totalInterest;
    private LoanStatus status;
    private LocalDate applicationDate;
    private LocalDate approvalDate;
    private LocalDate disbursementDate;
    private LocalDate maturityDate;
    private String collateralType;
    private String collateralDescription;
    private String collateralDocuments;
    private String collateralValue;
    private String guarantorInfo;
    private Long guarantorId;
    private String interestType;
    private BigDecimal processingFees;
    private BigDecimal latePaymentPenalty;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
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

    public LocalDate getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(LocalDate disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getCollateralType() {
        return collateralType;
    }

    public void setCollateralDescription(String collateralDescription) {
        this.collateralDescription = collateralDescription;
    }

    public void setCollateralValue(String collateralValue) {
        this.collateralValue = collateralValue;
    }

    public void setGuarantorId(Long guarantorId) {
        this.guarantorId = guarantorId;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public void setProcessingFees(BigDecimal processingFees) {
        this.processingFees = processingFees;
    }

    public void setLatePaymentPenalty(BigDecimal latePaymentPenalty) {
        this.latePaymentPenalty = latePaymentPenalty;
    }

    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType;
    }

    public String getCollateralDescription() {
        return collateralDescription;
    }

    public String getCollateralValue() {
        return collateralValue;
    }

    public Long getGuarantorId() {
        return guarantorId;
    }

    public String getInterestType() {
        return interestType;
    }

    public BigDecimal getProcessingFees() {
        return processingFees;
    }

    public BigDecimal getLatePaymentPenalty() {
        return latePaymentPenalty;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getCollateralDocuments() {
        return collateralDocuments;
    }

    public void setCollateralDocuments(String collateralDocuments) {
        this.collateralDocuments = collateralDocuments;
    }

    public String getGuarantorInfo() {
        return guarantorInfo;
    }

    public void setGuarantorInfo(String guarantorInfo) {
        this.guarantorInfo = guarantorInfo;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}