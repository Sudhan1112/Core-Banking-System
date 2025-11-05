package com.cbs.repository;

import com.cbs.model.entity.KYC;
import com.cbs.model.enums.DocumentType;
import com.cbs.model.enums.KYCStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KYCRepository extends JpaRepository<KYC, Long> {
    
    List<KYC> findByUserId(Long userId);
    
    List<KYC> findByUserIdAndStatus(Long userId, KYCStatus status);
    
    Optional<KYC> findByUserIdAndDocumentType(Long userId, DocumentType documentType);
    
    List<KYC> findByStatus(KYCStatus status);
    
    List<KYC> findByDocumentType(DocumentType documentType);
    
    Optional<KYC> findByDocumentNumber(String documentNumber);
    
    boolean existsByUserIdAndDocumentType(Long userId, DocumentType documentType);
    
    boolean existsByDocumentNumber(String documentNumber);
    
    @Query("SELECT k FROM KYC k WHERE k.userId = :userId AND k.status IN :statuses")
    List<KYC> findByUserIdAndStatusIn(@Param("userId") Long userId, @Param("statuses") List<KYCStatus> statuses);
    
    @Query("SELECT k FROM KYC k WHERE k.status = :status AND k.submissionDate BETWEEN :startDate AND :endDate")
    List<KYC> findByStatusAndSubmissionDateBetween(@Param("status") KYCStatus status,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(k) FROM KYC k WHERE k.userId = :userId AND k.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") KYCStatus status);
    
    @Query("SELECT k FROM KYC k WHERE k.expiryDate < :currentDate AND k.status = 'APPROVED'")
    List<KYC> findExpiredKYCDocuments(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT k FROM KYC k WHERE k.isPrimaryDocument = true AND k.userId = :userId")
    Optional<KYC> findPrimaryDocumentByUserId(@Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM KYC k WHERE k.userId = :userId AND k.status = 'APPROVED'")
    boolean hasUserCompletedKYC(@Param("userId") Long userId);
}