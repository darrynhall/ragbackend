package com.example.ingestion.service;

import java.util.List;

public interface EmbeddingService {
    List<float[]> embed(List<String> chunks);
}
