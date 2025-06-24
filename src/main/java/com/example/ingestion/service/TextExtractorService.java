package com.example.ingestion.service;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class TextExtractorService {

    public String extract(InputStream inputStream) {
        try {
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            parser.parse(inputStream, handler, metadata);
            return handler.toString();
        } catch (Exception e) {
            throw new RuntimeException("Text extraction failed", e);
        }
    }
}
