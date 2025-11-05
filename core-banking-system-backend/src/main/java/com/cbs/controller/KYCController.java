package com.cbs.controller;

import com.cbs.model.dto.request.KYCUploadRequest;
import com.cbs.model.dto.request.KYCVerificationRequest;
import com.cbs.model.dto.response.KYCResponse;
import com.cbs.service.interface_.KYCService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kyc")
public class KYCController {
    
    private final KYCService kycService;
    
    @Autowired
    public KYCController(KYCService kycService) {
        this.kycService = kycService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<KYCResponse> uploadDocument(@Valid @RequestBody KYCUploadRequest request) {
        KYCResponse kycResponse = kycService.uploadDocument(request);
        return new ResponseEntity<>(kycResponse, HttpStatus.CREATED);
    }
    
    @PostMapping("/upload-with-file")
    public ResponseEntity<KYCResponse> uploadDocumentWithFile(
            @RequestPart("request") @Valid KYCUploadRequest request,
            @RequestPart("file") MultipartFile file) {
        KYCResponse kycResponse = kycService.uploadDocumentWithFile(request, file);
        return new ResponseEntity<>(kycResponse, HttpStatus.CREATED);
    }
    
    @GetMapping("/{kycId}")
    public ResponseEntity<KYCResponse> getKycById(@PathVariable Long kycId) {
        return kycService.getKycById(kycId)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<KYCResponse>> getKycByUserId(@PathVariable Long userId) {
        List<KYCResponse> kycList = kycService.getKycByUserId(userId);
        return new ResponseEntity<>(kycList, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<KYCResponse>> getKycByUserIdAndStatus(
            @PathVariable Long userId, @PathVariable String status) {
        List<KYCResponse> kycList = kycService.getKycByUserIdAndStatus(userId, status);
        return new ResponseEntity<>(kycList, HttpStatus.OK);
    }
    
    @GetMapping
    public ResponseEntity<List<KYCResponse>> getAllKyc() {
        List<KYCResponse> kycList = kycService.getAllKyc();
        return new ResponseEntity<>(kycList, HttpStatus.OK);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<KYCResponse>> getKycByStatus(@PathVariable String status) {
        List<KYCResponse> kycList = kycService.getKycByStatus(status);
        return new ResponseEntity<>(kycList, HttpStatus.OK);
    }
    
    @PutMapping("/{kycId}/verify")
    public ResponseEntity<KYCResponse> verifyKyc(
            @PathVariable Long kycId,
            @RequestBody KYCVerificationRequest verificationRequest,
            @RequestHeader(value = "X-Verified-By", required = false) Long verifiedBy) {
        KYCResponse kycResponse = kycService.verifyKyc(kycId, verificationRequest, verifiedBy);
        return new ResponseEntity<>(kycResponse, HttpStatus.OK);
    }
    
    @PutMapping("/{kycId}/reject")
    public ResponseEntity<KYCResponse> rejectKyc(
            @PathVariable Long kycId,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-Verified-By", required = false) Long verifiedBy) {
        String rejectionReason = request.get("rejectionReason");
        KYCResponse kycResponse = kycService.rejectKyc(kycId, rejectionReason, verifiedBy);
        return new ResponseEntity<>(kycResponse, HttpStatus.OK);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<KYCResponse>> getPendingKyc() {
        List<KYCResponse> kycList = kycService.getPendingKyc();
        return new ResponseEntity<>(kycList, HttpStatus.OK);
    }
    
    @GetMapping("/expired")
    public ResponseEntity<List<KYCResponse>> getExpiredKyc() {
        List<KYCResponse> kycList = kycService.getExpiredKyc();
        return new ResponseEntity<>(kycList, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<Map<String, Object>> checkUserKycStatus(@PathVariable Long userId) {
        boolean isCompleted = kycService.hasUserCompletedKYC(userId);
        return new ResponseEntity<>(Map.of("kycCompleted", isCompleted), HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/primary")
    public ResponseEntity<KYCResponse> getPrimaryDocument(@PathVariable Long userId) {
        return kycService.getPrimaryDocument(userId)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<KYCResponse>> getKycByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<KYCResponse> kycList = kycService.getKycByDateRange(startDate, endDate);
        return new ResponseEntity<>(kycList, HttpStatus.OK);
    }
    
    @DeleteMapping("/{kycId}")
    public ResponseEntity<Void> deleteKyc(@PathVariable Long kycId) {
        kycService.deleteKyc(kycId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}