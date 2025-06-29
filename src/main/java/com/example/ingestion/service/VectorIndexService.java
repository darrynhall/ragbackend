package com.example.ingestion.service;

import java.util.List;

import com.example.ingestion.etl.Loader;
import com.example.ingestion.model.EmbeddingGeneratedEvent;

/**
 * Simple abstraction for indexing embedding vectors.
 */
public interface VectorIndexService extends Loader<EmbeddingGeneratedEvent> {
    /**
     * Index the given vectors and their corresponding text chunks for the specified filename.
     *
     * @param vectors the list of embedding vectors
     * @param chunks the original text chunks
     * @param filename the name of the source file
     */
    void index(List<float[]> vectors, List<String> chunks, String filename);

    @Override
    default void load(EmbeddingGeneratedEvent event) {
        index(event.embeddings(), event.chunks(), event.filename());
    }
}
