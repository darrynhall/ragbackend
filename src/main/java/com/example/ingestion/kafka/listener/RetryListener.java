package com.example.ingestion.kafka.listener;

import com.example.ingestion.model.FileUploadEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RetryListener {

    @KafkaListener(topics = "file.upload.retry", groupId = "retry-group")
    public void onRetry(FileUploadEvent event) {
        log.info("Retrying upload for file: {}", event.filename());
        // Re-dispatch to regular topic
        // kafkaTemplate.send("file.upload", event);
    }
}
