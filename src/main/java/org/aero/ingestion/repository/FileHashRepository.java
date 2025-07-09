package org.aero.ingestion.repository;

import org.aero.ingestion.entity.FileHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileHashRepository extends JpaRepository<FileHash, String> {
    // Add custom query methods if needed
}
