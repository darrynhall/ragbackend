package com.example.ingestion.jms.listener;

import com.example.ingestion.model.FileUploadEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RetryListener {

    @JmsListener(destination = "file.upload.retry")
    public void onRetry(FileUploadEvent event) {
        log.info("Retrying upload for file: {}", event.filename());
        // Re-dispatch to regular queue
        // jmsTemplate.convertAndSend("file.upload", event);
    }
}
