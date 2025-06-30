package com.example.ingestion.config;

import jakarta.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfig {
    @Bean
    public Queue fileUploadedQueue() {
        return new ActiveMQQueue("file.uploaded");
    }

    @Bean
    public Queue fileDeletedQueue() {
        return new ActiveMQQueue("file.deleted");
    }

    @Bean
    public Queue textExtractedQueue() {
        return new ActiveMQQueue("text.extracted");
    }

    @Bean
    public Queue textChunkedQueue() {
        return new ActiveMQQueue("text.chunked");
    }

    @Bean
    public Queue embeddingGeneratedQueue() {
        return new ActiveMQQueue("embedding.generated");
    }
}
