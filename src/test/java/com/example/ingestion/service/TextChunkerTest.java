package com.example.ingestion.service;

import org.aero.ingestion.service.TextChunker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TextChunkerTest {

    private final TextChunker chunker = new TextChunker();

    @Test
    public void testChunk() {
        String text = "Paragraph one.\n\nParagraph two.";
        List<String> chunks = chunker.transform(text);
        assertEquals(1, chunks.size());
        assertTrue(chunks.get(0).contains("Paragraph one"));
    }
}
