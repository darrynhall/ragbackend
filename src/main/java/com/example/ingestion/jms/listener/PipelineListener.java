package com.example.ingestion.jms.listener;

import org.springframework.ai.etl.DocumentProcessingPipeline;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.example.ingestion.model.FileUploadEvent;
import com.example.ingestion.service.FileStorageDocumentReader;
import com.example.ingestion.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener that triggers the Spring AI ETL pipeline when a file is uploaded.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PipelineListener {

    private final DocumentProcessingPipeline pipeline;
    private final FileStorageService storageService;

    @JmsListener(destination = "file.uploaded")
    public void handle(FileUploadEvent event) {
        String filename = event.filename();
        log.info("Running ETL pipeline for {}", filename);
        pipeline.process(new FileStorageDocumentReader(filename, storageService));
    }
}
