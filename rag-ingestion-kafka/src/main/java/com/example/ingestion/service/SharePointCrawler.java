package com.example.ingestion.service;

import com.example.ingestion.model.FileUploadEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SharePointCrawler {

    private final KafkaTemplate<String, FileUploadEvent> kafkaTemplate;

    public SharePointCrawler(KafkaTemplate<String, FileUploadEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 3600000)
    public void crawl() {
        // TODO: Connect to SharePoint via Microsoft Graph
        // Simulate file discovery
        String filename = "example.pdf";
        byte[] fileBytes = new byte[0]; // replace with actual file content
        kafkaTemplate.send("file.upload", new FileUploadEvent(filename, fileBytes));
    }
}
