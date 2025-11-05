package com.cbs.model.dto.response;

import com.cbs.model.enums.DocumentType;
import com.cbs.model.enums.KYCStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class KYCResponse {
    private Long kycId;
    private Long userId;
    private DocumentType documentType;
    private String documentNumber;
    private String documentUrl;
    private String documentHash;
    private KYCStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submissionDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime verificationDate;
    
    private Long verifiedBy;
    private String rejectionReason;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiryDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private String additionalInfo;
    private Boolean isPrimaryDocument;
    
    // Getters and setters
    public Long getKycId() {
        return kycId;
    }
    
    public void setKycId(Long kycId) {
        this.kycId = kycId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public DocumentType getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }
    
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    public String getDocumentUrl() {
        return documentUrl;
    }
    
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
    
    public String getDocumentHash() {
        return documentHash;
    }
    
    public void setDocumentHash(String documentHash) {
        this.documentHash = documentHash;
    }
    
    public KYCStatus getStatus() {
        return status;
    }
    
    public void setStatus(KYCStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    public LocalDateTime getVerificationDate() {
        return verificationDate;
    }
    
    public void setVerificationDate(LocalDateTime verificationDate) {
        this.verificationDate = verificationDate;
    }
    
    public Long getVerifiedBy() {
        return verifiedBy;
    }
    
    public void setVerifiedBy(Long verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
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
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    public Boolean getIsPrimaryDocument() {
        return isPrimaryDocument;
    }
    
    public void setIsPrimaryDocument(Boolean isPrimaryDocument) {
        this.isPrimaryDocument = isPrimaryDocument;
    }
}