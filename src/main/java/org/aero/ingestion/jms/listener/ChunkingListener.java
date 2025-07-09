package org.aero.ingestion.jms.listener;

import org.aero.ingestion.entity.ChunksGeneratedEvent;
import org.aero.ingestion.entity.IngestionStatus;
import org.aero.ingestion.entity.TextExtractedEvent;
import org.aero.ingestion.repository.IngestionStatusRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChunkingListener {

    private final TextSplitter textSplitter;
    private final JmsTemplate jmsTemplate;
    private final IngestionStatusRepository statusRepo;

    @JmsListener(destination = "text.extracted", containerFactory = "jmsListenerContainerFactory")
    public void handle(TextExtractedEvent event) {
        log.info("Chunking text for {}", event.filename());
        try {
            List<Document> docs = textSplitter.apply(List.of(new Document(event.text())));
            List<String> chunks = docs.stream().map(Document::getText).collect(Collectors.toList());
            jmsTemplate.convertAndSend("text.chunked", new ChunksGeneratedEvent(event.filename(), chunks, event.userId()));
            statusRepo.save(new IngestionStatus(event.filename(), event.userId(), "CHUNKED", null, LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Chunking failed for {}", event.filename(), e);
            statusRepo.save(new IngestionStatus(event.filename(), event.userId(), "FAILED", e.getMessage(), LocalDateTime.now()));
        }
    }
}
