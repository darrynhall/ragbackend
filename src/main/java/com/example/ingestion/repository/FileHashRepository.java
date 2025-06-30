package com.example.ingestion.repository;

import com.example.ingestion.model.FileHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileHashRepository extends JpaRepository<FileHash, String> {
    // Add custom query methods if needed
}
