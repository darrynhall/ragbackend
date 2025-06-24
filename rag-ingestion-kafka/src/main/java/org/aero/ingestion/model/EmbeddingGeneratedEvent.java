package org.aero.ingestion.model;

import java.util.List;

public record EmbeddingGeneratedEvent(String filename, List<String> chunks, List<float[]> vectors) {}
