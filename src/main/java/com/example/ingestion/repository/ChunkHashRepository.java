package com.example.ingestion.repository;

import com.example.ingestion.model.ChunkHash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChunkHashRepository extends JpaRepository<ChunkHash, String> {
    boolean existsByHash(String hash);
}
