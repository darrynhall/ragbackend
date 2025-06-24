package com.example.ingestion.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TextChunker {

    private final TokenTextSplitter splitter = new TokenTextSplitter();

    public List<String> chunk(String text) {
        Document doc = new Document(text, Map.of());
        return splitter.split(doc).stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }
}
