package com.example.ingestion.etl;

/**
 * Simple contract for extracting data from an input source.
 * @param <I> input type
 * @param <O> extracted type
 */
public interface Extractor<I, O> {
    O extract(I input) throws Exception;
}
