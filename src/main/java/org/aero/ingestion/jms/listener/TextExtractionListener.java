package org.aero.ingestion.jms.listener;

import static org.aero.ingestion.constants.AppConstants.FILE_UPLOADED_QUEUE;

import org.aero.ingestion.entity.FileUploadEvent;
import org.aero.ingestion.entity.TextExtractedEvent;
import org.aero.ingestion.entity.IngestionStatus;
import org.aero.ingestion.repository.IngestionStatusRepository;
import org.aero.ingestion.service.CustomAzureDocumentReader;
import org.aero.ingestion.service.FileStorageService;
import org.springframework.ai.document.Document;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener that triggers the Spring AI ETL pipeline when a file is uploaded.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TextExtractionListener {

        private final FileStorageService storageService;
        private final JmsTemplate jmsTemplate;
        private final IngestionStatusRepository statusRepo;

    @JmsListener(destination = FILE_UPLOADED_QUEUE, containerFactory = "jmsListenerContainerFactory")
    public void handle(FileUploadEvent event) {
        log.info("Extracting text from {}",  event.fileName());
        try {
            var docs = new CustomAzureDocumentReader(event.fileName(), storageService).get();
            String text = docs.stream().map(Document::getText).collect(Collectors.joining("\n"));
            jmsTemplate.convertAndSend("text.extracted",
                    new TextExtractedEvent(event.fileName(), text, event.badgeNumber()));
            statusRepo.save(new IngestionStatus(event.fileName(), event.badgeNumber(), "TEXT_EXTRACTED", null, LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Text extraction failed for {}", event.fileName(), e);
            statusRepo.save(new IngestionStatus(event.fileName(), event.badgeNumber(), "FAILED", e.getMessage(), LocalDateTime.now()));
        }
    }
}
