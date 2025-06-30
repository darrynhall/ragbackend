package com.example.ingestion.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.example.ingestion.model.ChunksGeneratedEvent;
import com.example.ingestion.model.EmbeddingGeneratedEvent;
import com.example.ingestion.model.FileUploadEvent;
import com.example.ingestion.model.TextExtractedEvent;

@Configuration

public class GraphApiConfig {

    @Value("${azure.graph.client-id}")
    private String clientId;

    @Value("${azure.graph.client-secret}")
    private String clientSecret;

    @Value("${azure.graph.tenant-id}")
    private String tenantId;

    @Bean
    public ClientSecretCredential clientSecretCredential() {
        return new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();
    }
    
    
    @Bean
    public KafkaAdmin.NewTopics topics456() {
        return new NewTopics(
                TopicBuilder.name("embedding.generated")
                    .build(),
                    TopicBuilder.name("file.upload.retry")                   
                    .build(),
                    TopicBuilder.name("file.uploaded")                   
                    .build(),
                    TopicBuilder.name("text.chunked")                   
                    .build(),
                TopicBuilder.name("text.extracted")                   
                    .build());
    }
    
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");  
        return new KafkaAdmin(configs);
    }
    
//    @Bean
//     ConsumerFactory<String, FileUploadEvent> consumerFactory() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.ingestion.model");
//        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.ingestion.model.FileUploadEvent");
//        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
//        config.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class.getName());
//        config.put("bootstrap.servers", "localhost:9092");
//        config.put("group.id", "file-uploaded-group");
//        config.put("key.deserializer", StringDeserializer.class);
//        config.put("value.deserializer", JsonDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(config);
//    }
//    
//    @Bean
//     ConsumerFactory<String, ChunksGeneratedEvent> consumerFactor2y() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.ingestion.model");
//        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.ingestion.model.ChunksGeneratedEvent");
//        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
//        config.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class.getName());
//        config.put("bootstrap.servers", "localhost:9092");
//        config.put("group.id", "chunks-generated-group");
//        config.put("key.deserializer", StringDeserializer.class);
//        config.put("value.deserializer", JsonDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(config);
//    }
//    
//    @Bean
//     ConsumerFactory<String, EmbeddingGeneratedEvent> consumerFactor3y() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.ingestion.model");
//        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.ingestion.model.EmbeddingGeneratedEvent");
//        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
//        config.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class.getName());
//        config.put("bootstrap.servers", "localhost:9092");
//        config.put("group.id", "embedding-generated-group");
//        config.put("key.deserializer", StringDeserializer.class);
//        config.put("value.deserializer", JsonDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(config);
//    }
//    
//    @Bean
//     ConsumerFactory<String, TextExtractedEvent> consumerFactor4y() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.ingestion.model");
//        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.ingestion.model.TextExtractedEvent");
//        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
//        config.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class.getName());
//        config.put("bootstrap.servers", "localhost:9092");
//        config.put("group.id", "text-extracted-group");
//        config.put("key.deserializer", StringDeserializer.class);
//        config.put("value.deserializer", JsonDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(config);
//    }

}
