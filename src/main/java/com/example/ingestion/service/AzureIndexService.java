package com.example.ingestion.service;

import com.azure.search.documents.SearchClient;
import com.azure.search.documents.IndexDocumentsBatch;
import com.azure.search.documents.models.IndexDocumentsAction;
import com.azure.search.documents.models.IndexActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AzureIndexService implements VectorIndexService {

    private final SearchClient searchClient;

    @Override
    public void index(List<float[]> vectors, List<String> chunks, String filename) {
        IndexDocumentsBatch<Map<String, Object>> batch = new IndexDocumentsBatch<>();

        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", filename + "-" + i);
            doc.put("content", chunks.get(i));
            doc.put("embedding", vectors.get(i));
            batch.addAction(new IndexDocumentsAction<Map<String, Object>>()
                    .setActionType(IndexActionType.UPLOAD)
                    .setDocument(doc));
        }

        searchClient.indexDocuments(batch);
    }
}
