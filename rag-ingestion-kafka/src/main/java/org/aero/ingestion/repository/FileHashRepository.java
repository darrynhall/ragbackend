package org.aero.ingestion.repository;

import org.aero.ingestion.model.FileHash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileHashRepository extends JpaRepository<FileHash, String> {
}
