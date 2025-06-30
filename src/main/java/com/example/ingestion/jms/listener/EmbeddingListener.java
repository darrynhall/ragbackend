package com.example.ingestion.jms.listener;

import com.example.ingestion.model.ChunksGeneratedEvent;
import com.example.ingestion.model.EmbeddingGeneratedEvent;
import com.example.ingestion.service.EmbeddingService;
import com.example.ingestion.service.DeconflictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmbeddingListener {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddingListener.class);

    private final EmbeddingService embeddingService;
    private final JmsTemplate jmsTemplate;
    private final DeconflictionService deconflictionService;

    private final ConcurrentHashMap<String, Integer> retryCounts = new ConcurrentHashMap<>();
    private static final int MAX_RETRIES = 3;

    public EmbeddingListener(EmbeddingService embeddingService,
                             JmsTemplate jmsTemplate,
                             DeconflictionService deconflictionService) {
        this.embeddingService = embeddingService;
        this.jmsTemplate = jmsTemplate;
        this.deconflictionService = deconflictionService;
    }

    @JmsListener(destination = "text.chunked")
    public void handle(ChunksGeneratedEvent event) {
        String filename = event.filename();
        String userId = event.userId();
        logger.info("Starting embedding for file: {}", filename);

        try {
            List<String> chunks = event.chunks();
            List<String> newChunks = deconflictionService.filterDuplicateChunks(chunks, filename);
            if (newChunks.isEmpty()) {
                logger.info("No new content to embed for file: {}", filename);
                return;
            }

            List<float[]> vectors = embeddingService.embed(newChunks);
            jmsTemplate.convertAndSend("embedding.generated", new EmbeddingGeneratedEvent(filename, vectors, newChunks, userId));
            logger.info("Embedding successful for file: {}", filename);
            retryCounts.remove(filename);
        } catch (Exception e) {
            int retries = retryCounts.getOrDefault(filename, 0);
            logger.error("Embedding failed for file: {} [attempt {}/{}]", filename, retries + 1, MAX_RETRIES, e);
            if (retries < MAX_RETRIES - 1) {
                retryCounts.put(filename, retries + 1);
                handle(event);
            } else {
                logger.error("Embedding permanently failed for file: {}", filename);
                retryCounts.remove(filename);
            }
        }
    }
}
