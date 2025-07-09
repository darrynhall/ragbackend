package org.aero.ingestion.jms.listener;

import org.aero.ingestion.entity.ChunksGeneratedEvent;
import org.aero.ingestion.entity.EmbeddingGeneratedEvent;
import org.aero.ingestion.entity.IngestionStatus;
import org.aero.ingestion.repository.IngestionStatusRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmbeddingGenerationListener {

    private final EmbeddingModel embeddingModel;
    private final JmsTemplate jmsTemplate;
    private final IngestionStatusRepository statusRepo;

    @JmsListener(destination = "text.chunked", containerFactory = "jmsListenerContainerFactory")
    public void handle(ChunksGeneratedEvent event) {
        log.info("Generating embeddings for {}", event.filename());
        try {
            List<float[]> embeddings = new ArrayList<>();
            for (String chunk : event.chunks()) {
                float[] emb = embeddingModel.embed(new Document(chunk));
                embeddings.add(emb);
            }
            jmsTemplate.convertAndSend("embedding.generated",
                    new EmbeddingGeneratedEvent(event.filename(), embeddings, event.chunks(), event.userId()));
            statusRepo.save(new IngestionStatus(event.filename(), event.userId(), "EMBEDDED", null, LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Embedding generation failed for {}", event.filename(), e);
            statusRepo.save(new IngestionStatus(event.filename(), event.userId(), "FAILED", e.getMessage(), LocalDateTime.now()));
        }
    }
}
