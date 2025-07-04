package com.example.ingestion.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.etl.reader.DocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.InputStreamResource;

/**
 * {@link DocumentReader} implementation that loads the uploaded file from the
 * configured {@link FileStorageService} and delegates text extraction to the
 * Spring AI {@link TikaDocumentReader} or {@link ParagraphPdfDocumentReader}.
 */
public class FileStorageDocumentReader implements DocumentReader {

    private final String filename;
    private final FileStorageService storageService;

    public FileStorageDocumentReader(String filename, FileStorageService storageService) {
        this.filename = filename;
        this.storageService = storageService;
    }

    @Override
    public List<Document> read() {
        try (InputStream is = storageService.getFileInputStream(filename)) {
            InputStreamResource resource = new InputStreamResource(is) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            DocumentReader reader;
            if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
                reader = new ParagraphPdfDocumentReader(resource, PdfDocumentReaderConfig.builder().build());
            } else {
                reader = new TikaDocumentReader(resource);
            }
            return reader.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
