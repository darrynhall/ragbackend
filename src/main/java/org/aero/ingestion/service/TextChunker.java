package org.aero.ingestion.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

@Service
public class TextChunker {

    private final TokenTextSplitter splitter = new TokenTextSplitter();

    public List<String> transform(String text) {
        List<Document> documents = splitter.split(new Document(text));
        return documents.stream()
                .map(Document::getText)
                .toList();
    }
}
