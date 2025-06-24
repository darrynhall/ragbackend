package com.example.ingestion.service;

import com.example.ingestion.model.FileUploadEvent;
import com.example.ingestion.model.IngestionStatus;
import com.example.ingestion.repository.IngestionStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final KafkaTemplate<String, FileUploadEvent> kafkaTemplate;
    private final DeconflictionService deconflictionService;
    private final IngestionStatusRepository statusRepository;

    public void upload(String filename, InputStream inputStream, String userId) {
        try {
            byte[] bytes = inputStream.readAllBytes();
            if (deconflictionService.isDuplicateFile(bytes, filename)) {
                statusRepository.save(IngestionStatus.builder()
                        .filename(filename)
                        .userId(userId)
                        .status("DUPLICATE")
                        .timestamp(LocalDateTime.now())
                        .build());
                return;
            }

            kafkaTemplate.send("file.upload", new FileUploadEvent(filename, bytes));
            statusRepository.save(IngestionStatus.builder()
                    .filename(filename)
                    .userId(userId)
                    .status("STARTED")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            statusRepository.save(IngestionStatus.builder()
                    .filename(filename)
                    .userId(userId)
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());
            throw new RuntimeException("Upload failed", e);
        }
    }
}
