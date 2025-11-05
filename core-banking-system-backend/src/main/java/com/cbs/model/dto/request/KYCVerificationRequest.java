package com.cbs.model.dto.request;

import com.cbs.model.enums.KYCStatus;
import jakarta.validation.constraints.NotNull;

public class KYCVerificationRequest {
    
    @NotNull(message = "Status is required")
    private KYCStatus status;
    
    private String rejectionReason;
    
    private String additionalNotes;
    
    // Getters and setters
    public KYCStatus getStatus() {
        return status;
    }
    
    public void setStatus(KYCStatus status) {
        this.status = status;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
}