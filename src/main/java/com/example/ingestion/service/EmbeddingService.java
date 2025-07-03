package com.example.ingestion.service;

import java.util.List;


/**
 * Abstraction over an embedding model.
 */
public interface EmbeddingService {

    List<float[]> embed(List<String> chunks);

}
