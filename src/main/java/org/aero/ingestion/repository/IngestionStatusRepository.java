package org.aero.ingestion.repository;

import org.aero.ingestion.entity.IngestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface IngestionStatusRepository extends JpaRepository<IngestionStatus, String> {
    Page<IngestionStatus> findByUserId(String userId, Pageable pageable);
    Page<IngestionStatus> findByTimestampAfter(LocalDateTime since, Pageable pageable);
    Page<IngestionStatus> findByUserIdAndTimestampAfter(String userId, LocalDateTime since, Pageable pageable);
}
