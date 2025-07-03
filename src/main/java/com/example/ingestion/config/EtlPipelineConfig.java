package com.example.ingestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.etl.DocumentProcessingPipeline;
import org.springframework.ai.etl.transformer.EmbeddingDocumentTransformer;
import org.springframework.ai.etl.transformer.TextSplitterDocumentTransformer;
import org.springframework.ai.etl.writer.VectorStoreWriter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;

@Configuration
public class EtlPipelineConfig {

    @Bean
    DocumentProcessingPipeline documentProcessingPipeline(EmbeddingModel embeddingModel,
            VectorStore vectorStore) {
        return DocumentProcessingPipeline.builder()
                .addTransformer(new TextSplitterDocumentTransformer(new TokenTextSplitter()))
                .addTransformer(new EmbeddingDocumentTransformer(embeddingModel))
                .writer(new VectorStoreWriter(vectorStore))
                .build();
    }
}
