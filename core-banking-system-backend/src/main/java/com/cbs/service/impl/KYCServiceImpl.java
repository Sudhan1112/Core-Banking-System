package com.cbs.service.impl;

import com.cbs.model.dto.request.KYCUploadRequest;
import com.cbs.model.dto.request.KYCVerificationRequest;
import com.cbs.model.dto.response.KYCResponse;
import com.cbs.model.entity.KYC;
import com.cbs.model.enums.DocumentType;
import com.cbs.model.enums.KYCStatus;
import com.cbs.repository.KYCRepository;
import com.cbs.service.interface_.KYCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class KYCServiceImpl implements KYCService {

    private static final Logger logger = LoggerFactory.getLogger(KYCServiceImpl.class);

    private final KYCRepository kycRepository;
    private final Path uploadPath;
    private final long maxFileSize;

    public KYCServiceImpl(
            KYCRepository kycRepository,
            @Value("${kyc.upload.directory:uploads/kyc}") String uploadDirectory,
            @Value("${kyc.max.file.size:10485760}") long maxFileSize
    ) {
        this.kycRepository = kycRepository;
        this.uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        this.maxFileSize = maxFileSize;

        try {
            Files.createDirectories(this.uploadPath);
            logger.info("KYC upload directory initialized at: {}", this.uploadPath);
        } catch (IOException e) {
            logger.error("Failed to create upload directory {}", this.uploadPath, e);
            throw new RuntimeException("Could not initialize KYC upload directory", e);
        }
    }

    @Override
    public KYCResponse uploadDocument(KYCUploadRequest request) {
        logger.info("Uploading KYC document for user ID: {}", request.getUserId());

        // Validate duplicates
        if (kycRepository.existsByUserIdAndDocumentType(request.getUserId(), request.getDocumentType())) {
            throw new RuntimeException("Document of this type already exists for this user");
        }
        if (kycRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new RuntimeException("Document number already exists");
        }

        KYC kyc = new KYC();
        kyc.setUserId(request.getUserId());
        kyc.setDocumentType(request.getDocumentType());
        kyc.setDocumentNumber(request.getDocumentNumber());
        kyc.setDocumentUrl(request.getDocumentUrl());
        kyc.setDocumentHash(request.getDocumentHash());
        kyc.setExpiryDate(request.getExpiryDate());
        kyc.setAdditionalInfo(request.getAdditionalInfo());
        kyc.setIsPrimaryDocument(request.getIsPrimaryDocument() != null && request.getIsPrimaryDocument());
        kyc.setStatus(KYCStatus.SUBMITTED);
        kyc.setSubmissionDate(LocalDateTime.now());

        KYC savedKyc = kycRepository.save(kyc);
        logger.info("KYC document uploaded successfully with ID: {}", savedKyc.getKycId());
        return convertToResponse(savedKyc);
    }

    @Override
    public KYCResponse uploadDocumentWithFile(KYCUploadRequest request, MultipartFile file) {
        logger.info("Uploading KYC document with file for user ID: {}", request.getUserId());

        if (!validateDocument(file, request.getDocumentType())) {
            throw new RuntimeException("Invalid document file");
        }

        String documentHash = generateDocumentHash(file);
        String fileName = saveFile(file);

        request.setDocumentUrl(fileName);
        request.setDocumentHash(documentHash);

        return uploadDocument(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KYCResponse> getKycById(Long kycId) {
        return kycRepository.findById(kycId).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KYCResponse> getKycByUserId(Long userId) {
        return kycRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KYCResponse> getKycByUserIdAndStatus(Long userId, String status) {
        KYCStatus kycStatus = KYCStatus.valueOf(status.toUpperCase());
        return kycRepository.findByUserIdAndStatus(userId, kycStatus)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KYCResponse> getAllKyc() {
        return kycRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KYCResponse> getKycByStatus(String status) {
        KYCStatus kycStatus = KYCStatus.valueOf(status.toUpperCase());
        return kycRepository.findByStatus(kycStatus)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public KYCResponse verifyKyc(Long kycId, KYCVerificationRequest verificationRequest, Long verifiedBy) {
        logger.info("Verifying KYC document with ID: {}", kycId);

        KYC kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new RuntimeException("KYC document not found"));

        if (kyc.getStatus() != KYCStatus.SUBMITTED && kyc.getStatus() != KYCStatus.UNDER_REVIEW) {
            throw new RuntimeException("KYC document is not in a verifiable state");
        }

        kyc.setStatus(verificationRequest.getStatus());
        kyc.setVerificationDate(LocalDateTime.now());
        kyc.setVerifiedBy(verifiedBy);

        if (verificationRequest.getStatus() == KYCStatus.REJECTED) {
            kyc.setRejectionReason(verificationRequest.getRejectionReason());
        }

        KYC updatedKyc = kycRepository.save(kyc);
        logger.info("KYC document verified with status: {}", verificationRequest.getStatus());
        return convertToResponse(updatedKyc);
    }

    @Override
    public KYCResponse rejectKyc(Long kycId, String rejectionReason, Long verifiedBy) {
        KYCVerificationRequest req = new KYCVerificationRequest();
        req.setStatus(KYCStatus.REJECTED);
        req.setRejectionReason(rejectionReason);
        return verifyKyc(kycId, req, verifiedBy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KYCResponse> getPendingKyc() {
        return kycRepository.findByStatus(KYCStatus.PENDING)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KYCResponse> getExpiredKyc() {
        return kycRepository.findExpiredKYCDocuments(LocalDateTime.now())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserCompletedKYC(Long userId) {
        return kycRepository.hasUserCompletedKYC(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KYCResponse> getPrimaryDocument(Long userId) {
        return kycRepository.findPrimaryDocumentByUserId(userId)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KYCResponse> getKycByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return kycRepository.findByStatusAndSubmissionDateBetween(KYCStatus.SUBMITTED, startDate, endDate)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteKyc(Long kycId) {
        KYC kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new RuntimeException("KYC document not found"));

        if (kyc.getDocumentUrl() != null) {
            try {
                Path filePath = uploadPath.resolve(kyc.getDocumentUrl());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                logger.warn("Failed to delete file: {}", kyc.getDocumentUrl(), e);
            }
        }

        kycRepository.deleteById(kycId);
        logger.info("KYC document deleted with ID: {}", kycId);
    }

    @Override
    public String generateDocumentHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("Failed to generate document hash", e);
            return null;
        }
    }

    @Override
    public boolean validateDocument(MultipartFile file, DocumentType documentType) {
        if (file.getSize() > maxFileSize) {
            logger.error("File size exceeds maximum limit: {}", file.getSize());
            return false;
        }

        String contentType = file.getContentType();
        return switch (documentType) {
            case AADHAAR_CARD, PAN_CARD, PASSPORT, VOTER_ID, DRIVING_LICENSE ->
                    contentType.equals("application/pdf") || contentType.equals("image/jpeg") || contentType.equals("image/png");
            case PASSPORT_PHOTO, SIGNATURE ->
                    contentType.equals("image/jpeg") || contentType.equals("image/png");
            case BANK_STATEMENT, UTILITY_BILL, INCOME_PROOF ->
                    contentType.equals("application/pdf") || contentType.equals("image/jpeg") || contentType.equals("image/png");
            default -> true;
        };
    }

    private String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File saved at: {}", filePath);
            return fileName;
        } catch (IOException e) {
            logger.error("Failed to save file", e);
            throw new RuntimeException("Failed to save document file", e);
        }
    }

    private KYCResponse convertToResponse(KYC kyc) {
        KYCResponse response = new KYCResponse();
        response.setKycId(kyc.getKycId());
        response.setUserId(kyc.getUserId());
        response.setDocumentType(kyc.getDocumentType());
        response.setDocumentNumber(kyc.getDocumentNumber());
        response.setDocumentUrl(kyc.getDocumentUrl());
        response.setDocumentHash(kyc.getDocumentHash());
        response.setStatus(kyc.getStatus());
        response.setSubmissionDate(kyc.getSubmissionDate());
        response.setVerificationDate(kyc.getVerificationDate());
        response.setVerifiedBy(kyc.getVerifiedBy());
        response.setRejectionReason(kyc.getRejectionReason());
        response.setExpiryDate(kyc.getExpiryDate());
        response.setCreatedAt(kyc.getCreatedAt());
        response.setUpdatedAt(kyc.getUpdatedAt());
        response.setAdditionalInfo(kyc.getAdditionalInfo());
        response.setIsPrimaryDocument(kyc.getIsPrimaryDocument());
        return response;
    }
}
