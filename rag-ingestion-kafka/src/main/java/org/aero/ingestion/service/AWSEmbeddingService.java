package org.aero.ingestion.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("aws")
@Service
public class AWSEmbeddingService implements EmbeddingService {

    @Override
    public List<float[]> embed(List<String> chunks) {
        // TODO: Implement using AWS Bedrock or SageMaker
        throw new UnsupportedOperationException("AWS Embedding not implemented yet.");
    }
}
