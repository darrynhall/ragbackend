package org.aero.ingestion.entity;

import java.io.Serializable;
import java.util.List;

public record EmbeddingGeneratedEvent(String filename, List<float[]> embeddings, List<String> chunks, String userId) implements Serializable {}
