package com.example.ingestion.service;

import java.util.List;

import com.example.ingestion.etl.Transformer;

/**
 * Abstraction over an embedding model.
 */
public interface EmbeddingService extends Transformer<List<String>, List<float[]>> {

    List<float[]> embed(List<String> chunks);

    @Override
    default List<float[]> transform(List<String> input) {
        return embed(input);
    }
}
