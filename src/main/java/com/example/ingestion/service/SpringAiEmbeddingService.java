package com.example.ingestion.service;

import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Embedding service backed by Spring AI {@link EmbeddingModel}.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class SpringAiEmbeddingService implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    @Override
    public List<float[]> embed(List<String> chunks) {
    	
        return embeddingModel.embed(chunks);
    }
}
