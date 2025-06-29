package com.example.ingestion.etl;

/**
 * Transforms input data into another representation.
 * @param <I> input type
 * @param <O> output type
 */
public interface Transformer<I, O> {
    O transform(I input) throws Exception;
}
