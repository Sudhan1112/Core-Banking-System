package com.cbs.repository;

import com.cbs.model.entity.ErrorLog;
import com.cbs.model.enums.ErrorLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {

    Page<ErrorLog> findByErrorLevel(ErrorLevel errorLevel, Pageable pageable);

    Page<ErrorLog> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT e FROM ErrorLog e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    Page<ErrorLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT COUNT(e) FROM ErrorLog e WHERE e.errorLevel = :errorLevel AND e.createdAt BETWEEN :startDate AND :endDate")
    long countByErrorLevelAndDateRange(@Param("errorLevel") ErrorLevel errorLevel, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}