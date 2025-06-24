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
public class IngestionStatus {
    @Id
    private String filename;
    private String userId;
    private String status; // STARTED, SUCCESS, FAILED
    private String errorMessage;
    private LocalDateTime timestamp;
}
