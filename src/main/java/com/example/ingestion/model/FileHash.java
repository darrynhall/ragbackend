package com.example.ingestion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileHash {
    @Id
    private String hash;
    private String filename;
    private long size;
    private LocalDateTime ingestedAt;
}
