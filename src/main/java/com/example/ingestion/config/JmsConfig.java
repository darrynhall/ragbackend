package com.example.ingestion.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfig {
    @Bean
    ActiveMQQueue fileUploadedQueue() {
        return new ActiveMQQueue("file.uploaded");
    }

    @Bean
    ActiveMQQueue fileDeletedQueue() {
        return new ActiveMQQueue("file.deleted");
    }

    @Bean
    ActiveMQQueue textExtractedQueue() {
        return new ActiveMQQueue("text.extracted");
    }

    @Bean
    ActiveMQQueue textChunkedQueue() {
        return new ActiveMQQueue("text.chunked");
    }

    @Bean
    ActiveMQQueue embeddingGeneratedQueue() {
        return new ActiveMQQueue("embedding.generated");
    }
}
