package com.example.ingestion.model;

import java.util.List;

public record EmbeddingGeneratedEvent(String filename, List<float[]> embeddings, List<String> chunks, String userId) {}
