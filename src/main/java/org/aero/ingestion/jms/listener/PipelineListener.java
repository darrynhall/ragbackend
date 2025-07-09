package org.aero.ingestion.jms.listener;

import static org.aero.ingestion.constants.AppConstants.FILE_UPLOADED_QUEUE;

import org.aero.ingestion.entity.FileUploadEvent;
import org.aero.ingestion.service.CustomAzureDocumentReader;
import org.aero.ingestion.service.EtlPipeline;
import org.aero.ingestion.service.FileStorageService;
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

	private final EtlPipeline etlPipeline;
	private final FileStorageService storageService;
/*
 * Listens to the "file.uploaded" JMS topic and triggers the ETL pipeline.
 */
    @JmsListener(destination = FILE_UPLOADED_QUEUE)
    public void handle(FileUploadEvent event) {
        log.info("Running ETL pipeline for {}",  event.fileName());
        etlPipeline.runIngestion(new CustomAzureDocumentReader( event.fileName(), storageService).get());
    }
}
