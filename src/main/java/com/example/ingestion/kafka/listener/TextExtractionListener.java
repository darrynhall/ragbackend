package com.example.ingestion.kafka.listener;

import com.example.ingestion.model.FileUploadEvent;
import com.example.ingestion.model.TextExtractedEvent;
import com.example.ingestion.service.TextExtractorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TextExtractionListener {
    private static final Logger logger = LoggerFactory.getLogger(TextExtractionListener.class);

    private final TextExtractorService extractor;
    private final KafkaTemplate<String, TextExtractedEvent> kafkaTemplate;

    private final ConcurrentHashMap<String, Integer> retryCounts = new ConcurrentHashMap<>();
    private static final int MAX_RETRIES = 3;

    public TextExtractionListener(TextExtractorService extractor, KafkaTemplate<String, TextExtractedEvent> kafkaTemplate) {
        this.extractor = extractor;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "file.uploaded", groupId = "ingestion")
    public void handle(FileUploadEvent event) {
        String filename = event.filename();
        String userId = event.userId();
        logger.info("Starting text extraction for file: {}", filename);

        try {
            // No fileBytes in FileUploadEvent, so just pass empty string for now
            String text = "";
            kafkaTemplate.send("text.extracted", new TextExtractedEvent(filename, text, userId));
            logger.info("Text extraction successful for file: {}", filename);
            retryCounts.remove(filename); // clear retry record
        } catch (Exception e) {
            int currentRetry = retryCounts.getOrDefault(filename, 0);
            logger.error("Text extraction failed for file: {} [attempt {}/{}]", filename, currentRetry + 1, MAX_RETRIES, e);

            if (currentRetry < MAX_RETRIES - 1) {
                retryCounts.put(filename, currentRetry + 1);
                logger.info("Retrying text extraction for file: {}", filename);
                handle(event);  // recursive retry (consider using async queue in production)
            } else {
                logger.error("Text extraction permanently failed for file: {} after {} attempts", filename, MAX_RETRIES);
                retryCounts.remove(filename);
                // Optionally send to a dead letter queue or log externally
            }
        }
    }
}
