package com.example.ingestion.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Profile("aws")
@Service
public class AWSEmbeddingService implements EmbeddingService {

    private static final int EMBED_DIM = 1536;

    @Override
    public List<float[]> embed(List<String> chunks) {
        // Simulated call to Bedrock - random vectors
        return chunks.stream()
                .map(chunk -> new Random().doubles(EMBED_DIM).mapToObj(d -> (float) d).collect(Collectors.toList()))
                .map(list -> list.stream().mapToFloat(f -> f).toArray())
                .collect(Collectors.toList());
    }
}
