package com.cbs.model.entity;

import com.cbs.model.enums.DocumentType;
import com.cbs.model.enums.KYCStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc")
public class KYC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kyc_id")
    private Long kycId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    
    @Column(name = "document_number", nullable = false)
    private String documentNumber;
    
    @Column(name = "document_url")
    private String documentUrl;
    
    @Column(name = "document_hash")
    private String documentHash;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private KYCStatus status;
    
    @Column(name = "submission_date")
    private LocalDateTime submissionDate;
    
    @Column(name = "verification_date")
    private LocalDateTime verificationDate;
    
    @Column(name = "verified_by")
    private Long verifiedBy;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;
    
    @Column(name = "is_primary_document")
    private Boolean isPrimaryDocument;
    
    // Default constructor
    public KYC() {
        this.createdAt = LocalDateTime.now();
        this.status = KYCStatus.PENDING;
        this.isPrimaryDocument = false;
    }
    
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
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}