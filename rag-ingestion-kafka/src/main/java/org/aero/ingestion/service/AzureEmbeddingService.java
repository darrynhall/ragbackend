package org.aero.ingestion.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embedding;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AzureEmbeddingService implements EmbeddingService {

    private final OpenAIClient openAIClient;

    public AzureEmbeddingService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    @Override
    public List<float[]> embed(List<String> chunks) {
        return chunks.stream()
                .map(chunk -> openAIClient.getEmbeddings(chunk)
                        .getData()
                        .stream()
                        .map(Embedding::getEmbedding)
                        .flatMap(List::stream)
                        .mapToFloat(Float::floatValue)
                        .toArray())
                .collect(Collectors.toList());
    }
}
