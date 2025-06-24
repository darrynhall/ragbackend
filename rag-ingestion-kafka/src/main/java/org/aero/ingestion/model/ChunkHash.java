package org.aero.ingestion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChunkHash {
    @Id
    private String hash;
    private String filename;
    private LocalDateTime ingestedAt;
}
