package org.aero.ingestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;

@Configuration

public class GraphApiConfig {

	@Value("${azure.graph.client-id}")
	private String clientId;

	@Value("${azure.graph.client-secret}")
	private String clientSecret;

	@Value("${azure.graph.tenant-id}")
	private String tenantId;

	@Bean
	ClientSecretCredential clientSecretCredential() {
		return new ClientSecretCredentialBuilder().clientId(clientId).clientSecret(clientSecret).tenantId(tenantId)
				.build();
	}
 
}
