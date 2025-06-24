package org.aero.ingestion.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GraphApiConfig {

    @Value("${azure.graph.client-id}")
    private String clientId;

    @Value("${azure.graph.client-secret}")
    private String clientSecret;

    @Value("${azure.graph.tenant-id}")
    private String tenantId;

    @Bean
    public TokenCredentialAuthProvider tokenCredentialAuthProvider() {
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();

        return new TokenCredentialAuthProvider(List.of("https://graph.microsoft.com/.default"), clientSecretCredential);
    }
}
