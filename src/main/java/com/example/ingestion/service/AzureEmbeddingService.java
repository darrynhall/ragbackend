package com.example.ingestion.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.azure.ai.openai.models.Embeddings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.stream.Collectors;

@Profile("azure")
@Service
@RequiredArgsConstructor
public class AzureEmbeddingService implements EmbeddingService {

    private final OpenAIClient openAIClient;

    @Override
    public List<float[]> embed(List<String> chunks) {
        return chunks.stream()
            .map(chunk -> {
                Embeddings embeddings = openAIClient.getEmbeddings("your-embedding-deployment-name", new EmbeddingsOptions(List.of(chunk)));
                List<Float> embeddingNumbers = embeddings.getData().get(0).getEmbedding();
                float[] embeddingArray = new float[embeddingNumbers.size()];
                for (int i = 0; i < embeddingNumbers.size(); i++) {
                    embeddingArray[i] = embeddingNumbers.get(i);
                }
                return embeddingArray;
            })
            .collect(Collectors.toList());
    }
}
