package com.cbs.service.interface_;

import com.cbs.model.dto.request.KYCUploadRequest;
import com.cbs.model.dto.request.KYCVerificationRequest;
import com.cbs.model.dto.response.KYCResponse;
import com.cbs.model.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface KYCService {
    
    KYCResponse uploadDocument(KYCUploadRequest request);
    
    KYCResponse uploadDocumentWithFile(KYCUploadRequest request, MultipartFile file);
    
    Optional<KYCResponse> getKycById(Long kycId);
    
    List<KYCResponse> getKycByUserId(Long userId);
    
    List<KYCResponse> getKycByUserIdAndStatus(Long userId, String status);
    
    List<KYCResponse> getAllKyc();
    
    List<KYCResponse> getKycByStatus(String status);
    
    KYCResponse verifyKyc(Long kycId, KYCVerificationRequest verificationRequest, Long verifiedBy);
    
    KYCResponse rejectKyc(Long kycId, String rejectionReason, Long verifiedBy);
    
    List<KYCResponse> getPendingKyc();
    
    List<KYCResponse> getExpiredKyc();
    
    boolean hasUserCompletedKYC(Long userId);
    
    Optional<KYCResponse> getPrimaryDocument(Long userId);
    
    List<KYCResponse> getKycByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    void deleteKyc(Long kycId);
    
    String generateDocumentHash(MultipartFile file);
    
    boolean validateDocument(MultipartFile file, DocumentType documentType);
}