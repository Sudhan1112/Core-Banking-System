package com.cbs.model.dto.response;

import com.cbs.model.enums.TransactionStatus;
import com.cbs.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private Long transactionId;
    private String transactionReference;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private Long userId;
    private TransactionStatus status;
    private String description;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal balanceAfter;
    private BigDecimal feeAmount;
    private Long relatedTransactionId;
    
    // Getters and setters
    public Long getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getTransactionReference() {
        return transactionReference;
    }
    
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Long getSourceAccountId() {
        return sourceAccountId;
    }
    
    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }
    
    public Long getDestinationAccountId() {
        return destinationAccountId;
    }
    
    public void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
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
    
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
    
    public BigDecimal getFeeAmount() {
        return feeAmount;
    }
    
    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }
    
    public Long getRelatedTransactionId() {
        return relatedTransactionId;
    }
    
    public void setRelatedTransactionId(Long relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
    }
}