package org.aero.ingestion.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Primary
public class SpringAiVectorStoreService implements VectorIndexService {

    private final VectorStore vectorStore;

    public SpringAiVectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void index(List<float[]> vectors, List<String> chunks, String filename) {
        List<Document> docs = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("filename", filename);
            metadata.put("chunk", i);
            docs.add(new Document(chunks.get(i), metadata));
        }
        vectorStore.add(docs);
    }
}
