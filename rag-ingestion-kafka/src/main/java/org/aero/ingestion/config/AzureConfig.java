package org.aero.ingestion.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureConfig {

    @Value("${azure.openai.api-key}")
    private String openaiApiKey;

    @Value("${azure.openai.endpoint}")
    private String openaiEndpoint;

    @Value("${azure.search.api-key}")
    private String searchApiKey;

    @Value("${azure.search.endpoint}")
    private String searchEndpoint;

    @Value("${azure.search.index-name}")
    private String indexName;

    @Bean
    public OpenAIClient openAIClient() {
        return new OpenAIClient(openaiEndpoint, new AzureKeyCredential(openaiApiKey));
    }

    @Bean
    public SearchClient searchClient() {
        return new SearchClientBuilder()
                .credential(new AzureKeyCredential(searchApiKey))
                .endpoint(searchEndpoint)
                .indexName(indexName)
                .buildClient();
    }
}
