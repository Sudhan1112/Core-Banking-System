package com.cbs.model.dto.request;

import com.cbs.model.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class KYCUploadRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Document type is required")
    private DocumentType documentType;
    
    @NotBlank(message = "Document number is required")
    @Size(max = 50, message = "Document number must not exceed 50 characters")
    private String documentNumber;
    
    @NotBlank(message = "Document URL is required")
    @Size(max = 500, message = "Document URL must not exceed 500 characters")
    private String documentUrl;
    
    private String documentHash;
    
    private LocalDateTime expiryDate;
    
    @Size(max = 1000, message = "Additional info must not exceed 1000 characters")
    private String additionalInfo;
    
    private Boolean isPrimaryDocument;
    
    private MultipartFile documentFile;
    
    // Getters and setters
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
    
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
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
    
    public MultipartFile getDocumentFile() {
        return documentFile;
    }
    
    public void setDocumentFile(MultipartFile documentFile) {
        this.documentFile = documentFile;
    }
}