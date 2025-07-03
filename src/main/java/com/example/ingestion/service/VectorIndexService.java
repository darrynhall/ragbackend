package com.example.ingestion.service;

import java.util.List;

import com.example.ingestion.model.EmbeddingGeneratedEvent;

/**
 * Simple abstraction for indexing embedding vectors.
 */
public interface VectorIndexService {
    /**
     * Index the given vectors and their corresponding text chunks for the specified filename.
     *
     * @param vectors the list of embedding vectors
     * @param chunks the original text chunks
     * @param filename the name of the source file
     */
    void index(List<float[]> vectors, List<String> chunks, String filename);

}
