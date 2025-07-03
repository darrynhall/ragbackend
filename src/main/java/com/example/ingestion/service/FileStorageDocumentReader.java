package com.example.ingestion.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.etl.reader.DocumentReader;

/**
 * DocumentReader implementation that reads the uploaded file from the
 * configured {@link FileStorageService} and uses the {@link TextExtractorService}
 * to extract text.
 */
public class FileStorageDocumentReader implements DocumentReader {

    private final String filename;
    private final FileStorageService storageService;
    private final TextExtractorService extractorService;

    public FileStorageDocumentReader(String filename, FileStorageService storageService,
            TextExtractorService extractorService) {
        this.filename = filename;
        this.storageService = storageService;
        this.extractorService = extractorService;
    }

    @Override
    public List<Document> read() {
        try {
            InputStream is = storageService.getFileInputStream(filename);
            String text = extractorService.extract(is, filename);
            return List.of(new Document(text));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
