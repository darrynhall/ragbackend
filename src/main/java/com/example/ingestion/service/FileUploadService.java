package com.example.ingestion.service;

import java.io.InputStream;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.ingestion.model.FileUploadEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for initiating the ingestion pipeline for uploaded files.
 * In a real implementation this would also persist the file to storage.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final KafkaTemplate<String, FileUploadEvent> kafkaTemplate;

    /**
     * Upload the file content and send an event to start processing.
     *
     * @param filename name of the file
     * @param content the file content
     * @param userId id of the user uploading the file
     */
    public void upload(String filename, InputStream content, String userId) {
        // In this sample we just log and emit an event. The content would normally be stored.
        log.info("Received file '{}' from user '{}'; emitting upload event", filename, userId);
        kafkaTemplate.send("file.uploaded", new FileUploadEvent(filename, userId, System.currentTimeMillis()));
    }

    /**
     * Delete the stored file and emit a deletion event.
     *
     * @param filename name of the file to delete
     */
    public void deleteByFilename(String filename) {
        log.info("Deleting stored file '{}'; emitting delete event", filename);
        // A real implementation would remove the file from storage
        kafkaTemplate.send("file.deleted", new FileUploadEvent(filename, "system", System.currentTimeMillis()));
    }
}
