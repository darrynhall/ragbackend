package com.example.ingestion.jms.listener;

import com.example.ingestion.model.TextExtractedEvent;
import com.example.ingestion.model.ChunksGeneratedEvent;
import com.example.ingestion.service.TextChunker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChunkingListener {
    private static final Logger logger = LoggerFactory.getLogger(ChunkingListener.class);

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
        logger.info("Starting chunking for file: {}", filename);
        try {
            List<String> chunks = chunker.transform(event.text());
            jmsTemplate.convertAndSend("text.chunked", new ChunksGeneratedEvent(filename, chunks, userId));
            logger.info("Chunking successful for file: {}", filename);
            retryCounts.remove(filename);
        } catch (Exception e) {
            int retries = retryCounts.getOrDefault(filename, 0);
            logger.error("Chunking failed for file: {} [attempt {}/{}]", filename, retries + 1, MAX_RETRIES, e);
            if (retries < MAX_RETRIES - 1) {
                retryCounts.put(filename, retries + 1);
                handle(event);
            } else {
                logger.error("Chunking permanently failed for file: {}", filename);
                retryCounts.remove(filename);
            }
        }
    }
}
