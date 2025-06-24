package com.example.ingestion.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("aws")
@Service
public class AWSIndexService implements VectorIndexService {

    @Override
    public void index(List<float[]> vectors, List<String> chunks, String filename) {
        // Simulated call to OpenSearch/CloudSearch
        System.out.println("Indexing " + vectors.size() + " vectors for file: " + filename);
    }
}
