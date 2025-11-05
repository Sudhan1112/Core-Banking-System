package com.cbs.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class TransferRequest {
    
    @NotNull(message = "Source account ID is required")
    @Positive(message = "Source account ID must be positive")
    private Long sourceAccountId;
    
    @NotNull(message = "Destination account ID is required")
    @Positive(message = "Destination account ID must be positive")
    private Long destinationAccountId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    // Getters and setters
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
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}