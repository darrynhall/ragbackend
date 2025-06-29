package com.example.ingestion.etl;

/**
 * Loads transformed data to a target system.
 * @param <T> input type
 */
public interface Loader<T> {
    void load(T input) throws Exception;
}
