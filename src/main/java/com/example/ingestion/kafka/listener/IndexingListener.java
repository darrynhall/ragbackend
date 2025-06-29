package com.example.ingestion.kafka.listener;

import com.example.ingestion.model.EmbeddingGeneratedEvent;
import com.example.ingestion.service.VectorIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class IndexingListener {
    private static final Logger logger = LoggerFactory.getLogger(IndexingListener.class);

    private final VectorIndexService indexService;
    private final ConcurrentHashMap<String, Integer> retryCounts = new ConcurrentHashMap<>();
    private static final int MAX_RETRIES = 3;

    public IndexingListener(VectorIndexService indexService) {
        this.indexService = indexService;
    }

    @KafkaListener(topics = "embedding.generated", groupId = "ingestion")
    public void handle(EmbeddingGeneratedEvent event) {
        String filename = event.filename();
        logger.info("Starting indexing for file: {}", filename);
        try {
            indexService.index(event.embeddings(), event.chunks(), filename);
            logger.info("Indexing successful for file: {}", filename);
            retryCounts.remove(filename);
        } catch (Exception e) {
            int retries = retryCounts.getOrDefault(filename, 0);
            logger.error("Indexing failed for file: {} [attempt {}/{}]", filename, retries + 1, MAX_RETRIES, e);
            if (retries < MAX_RETRIES - 1) {
                retryCounts.put(filename, retries + 1);
                handle(event);
            } else {
                logger.error("Indexing permanently failed for file: {}", filename);
                retryCounts.remove(filename);
            }
        }
    }
}
