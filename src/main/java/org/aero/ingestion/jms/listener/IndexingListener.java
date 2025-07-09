package org.aero.ingestion.jms.listener;

import org.aero.ingestion.entity.EmbeddingGeneratedEvent;
import org.aero.ingestion.entity.IngestionStatus;
import org.aero.ingestion.repository.IngestionStatusRepository;
import org.aero.ingestion.service.CustomAzureVectorStore;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class IndexingListener {

    private final CustomAzureVectorStore vectorStore;
    private final IngestionStatusRepository statusRepo;

    @JmsListener(destination = "embedding.generated", containerFactory = "jmsListenerContainerFactory")
    public void handle(EmbeddingGeneratedEvent event) {
        log.info("Indexing chunks for {}", event.filename());
        try {
            vectorStore.add(event.chunks(), event.embeddings());
            statusRepo.save(new IngestionStatus(event.filename(), event.userId(), "SUCCESS", null, LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Indexing failed for {}", event.filename(), e);
            statusRepo.save(new IngestionStatus(event.filename(), event.userId(), "FAILED", e.getMessage(), LocalDateTime.now()));
        }
    }
}
