package com.example.ingestion.repository;

import com.example.ingestion.model.FileHash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileHashRepository extends JpaRepository<FileHash, String> {
}
