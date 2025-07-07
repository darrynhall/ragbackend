package org.aero.ingestion.jms.listener;

 
import org.aero.ingestion.model.FileUploadEvent;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener that triggers the Spring AI ETL pipeline when a file is uploaded.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PipelineListener {

	/*
	 * private final EtlPipeline pipeline; private final
	 * FileStorageService storageService; private final TextExtractorService
	 * extractorService;
	 */
    @JmsListener(destination = "file.uploaded")
    public void handle(FileUploadEvent event) {
        String filename = event.filename();
        log.info("Running ETL pipeline for {}", filename);
    //    pipeline.process(new FileStorageDocumentReader(filename, storageService, extractorService));
    }
}
