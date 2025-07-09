package org.aero.ingestion.repository;

import org.aero.ingestion.entity.ChunkHash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChunkHashRepository extends JpaRepository<ChunkHash, String> {
    boolean existsByHash(String hash);
}
