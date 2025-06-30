package com.example.ingestion.jms.listener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.example.ingestion.model.ChunksGeneratedEvent;
import com.example.ingestion.model.TextExtractedEvent;
import com.example.ingestion.service.TextChunker;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class ChunkingListener {


    private final TextChunker chunker;
    private final JmsTemplate jmsTemplate;

    private final ConcurrentHashMap<String, Integer> retryCounts = new ConcurrentHashMap<>();
    private static final int MAX_RETRIES = 3;

    public ChunkingListener(TextChunker chunker, JmsTemplate jmsTemplate) {
        this.chunker = chunker;
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = "text.extracted")
    public void handle(TextExtractedEvent event) {
        String filename = event.filename();
        String userId = event.userId();
        log.info("Starting chunking for file: {}", filename);
        try {
            List<String> chunks = chunker.transform(event.text());
            jmsTemplate.convertAndSend("text.chunked", new ChunksGeneratedEvent(filename, chunks, userId));
            log.info("Chunking successful for file: {}", filename);
            retryCounts.remove(filename);
        } catch (Exception e) {
            int retries = retryCounts.getOrDefault(filename, 0);
            log.error("Chunking failed for file: {} [attempt {}/{}]", filename, retries + 1, MAX_RETRIES, e);
            if (retries < MAX_RETRIES - 1) {
                retryCounts.put(filename, retries + 1);
                handle(event);
            } else {
            	log.error("Chunking permanently failed for file: {}", filename);
                retryCounts.remove(filename);
            }
        }
    }
}
