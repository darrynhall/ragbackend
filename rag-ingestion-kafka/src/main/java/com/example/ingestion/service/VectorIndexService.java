package com.example.ingestion.service;

import java.util.List;

public interface VectorIndexService {
    void index(List<float[]> vectors, List<String> chunks, String filename);
}
