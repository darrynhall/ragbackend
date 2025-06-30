

package com.example.ingestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;

@Configuration
public class AzureConfig {

    @Value("${spring.ai.azure.openai.api-key}")
    private String openaiApiKey;

    @Value("${spring.ai.azure.openai.endpoint}") 
    private String openaiEndpoint;

    @Value("${spring.ai.azure.search.api-key}")
    private String searchApiKey;

    @Value("${spring.ai.azure.search.endpoint}")
    private String searchEndpoint;

    @Value("${spring.ai.azure.search.index-name}")
    private String indexName;

    @Bean
	 OpenAIClient openAIClient() {
        return new OpenAIClientBuilder()
            .endpoint(openaiEndpoint)
            .credential(new AzureKeyCredential(openaiApiKey))
            .buildClient();
    }

    @Bean
     SearchClient searchClient() {
        return new SearchClientBuilder()
                .credential(new AzureKeyCredential(searchApiKey))
                .endpoint(searchEndpoint)
                .indexName(indexName)
                .buildClient();
    }

    @Bean
     SearchIndexClient searchIndexClient() {
        return new SearchIndexClientBuilder()
                .credential(new AzureKeyCredential(searchApiKey))
                .endpoint(searchEndpoint)
                .buildClient();
    }

    @Value("${azure.graph.client-id}")
    private String graphClientId;

    @Value("${azure.graph.client-secret}")
    private String graphClientSecret;

    @Value("${azure.graph.tenant-id}")
    private String graphTenantId;

    @Bean
     TokenCredentialAuthProvider tokenCredentialAuthProvider() {
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(graphClientId)
                .clientSecret(graphClientSecret)
                .tenantId(graphTenantId)
                .build();
        return new TokenCredentialAuthProvider(
                java.util.Arrays.asList("https://graph.microsoft.com/.default"),
                clientSecretCredential);
    }

    @Bean
     GraphServiceClient<?> graphServiceClient(TokenCredentialAuthProvider tokenCredentialAuthProvider) {
        return GraphServiceClient
                .builder()
                .authenticationProvider(tokenCredentialAuthProvider)
                .buildClient();
    }

}
