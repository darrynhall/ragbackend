package com.example.ingestion.service;

 

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AzureIndexService implements VectorIndexService {

    private final org.springframework.ai.vectorstore.VectorStore vectorStore;

    @Override
    public void index(List<float[]> vectors, List<String> chunks, String filename) {
        for (int i = 0; i < chunks.size(); i++) {
            org.springframework.ai.document.Document doc = new org.springframework.ai.document.Document(
                filename + "-" + i,
                chunks.get(i),
                java.util.Map.of("embedding", vectors.get(i))
            );
            vectorStore.add(java.util.List.of(doc));
        }
    }
}
