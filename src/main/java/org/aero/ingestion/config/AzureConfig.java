
package org.aero.ingestion.config;

import java.net.InetSocketAddress;

import org.aero.ingestion.service.CustomAzureVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.azure.AzureVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
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

	@Value("${azure.document.endpoint}")
	private String documentEndpoint;

	@Value("${azure.document.api-key}")
	private String documentApiKey;

	@Value("${spring.ai.azure.language.api-key}")
	private String languageApiKey;

	@Value("${spring.ai.azure.language.endpoint}")
	private String languageEndpoint;

	ProxyOptions proxyOptions = new ProxyOptions(ProxyOptions.Type.HTTP,
			new InetSocketAddress("proxy-west.aero.org", 8080)).setCredentials("30097", "thu");
	HttpClient httpClient2 = new NettyAsyncHttpClientBuilder().proxy(proxyOptions).build();

	
//
//	@Bean
//	OpenAIClient openAIClient() {
//
//		return new OpenAIClientBuilder().endpoint(openaiEndpoint).httpClient(httpClient)
//				.credential(new AzureKeyCredential(openaiApiKey)).buildClient();
//	}

	@Bean
	SearchClient searchClient() {
	
		return new SearchClientBuilder().credential(new AzureKeyCredential(searchApiKey)).endpoint(searchEndpoint)
				.httpClient(httpClient2).indexName(indexName).buildClient();
	}

	@Bean
	SearchIndexClient searchIndexClient() {
		 
		return new SearchIndexClientBuilder().credential(new AzureKeyCredential(searchApiKey)).endpoint(searchEndpoint)
				.httpClient(httpClient2).buildClient();
	}

	@Bean
	VectorStore vectorStore(SearchIndexClient searchIndexClient, EmbeddingModel embeddingModel) {

		return AzureVectorStore.builder(searchIndexClient, embeddingModel).initializeSchema(false)
//				// Define the metadata fields to be used
//				// in the similarity search filters.
//				.filterMetadataFields(List.of(MetadataField.text("country"), MetadataField.int64("year"),
//						MetadataField.date("activationDate")))
//				.defaultTopK(5).defaultSimilarityThreshold(0.7).
				.indexName(indexName)

				.build();
	}

	@Bean
	DocumentAnalysisClient documentAnalysisClient() {
		return new DocumentAnalysisClientBuilder().endpoint(documentEndpoint)
				.httpClient(httpClient2)
				.credential(new AzureKeyCredential(documentApiKey)).buildClient();
	}

	@Value("${azure.graph.client-id}")
	private String graphClientId;

	@Value("${azure.graph.client-secret}")
	private String graphClientSecret;

	@Value("${azure.graph.tenant-id}")
	private String graphTenantId;

	@Bean
	TokenCredentialAuthProvider tokenCredentialAuthProvider() {
		ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder().clientId(graphClientId)
				.clientSecret(graphClientSecret).tenantId(graphTenantId).build();
		return new TokenCredentialAuthProvider(java.util.Arrays.asList("https://graph.microsoft.com/.default"),
				clientSecretCredential);
	}

	@Bean
	GraphServiceClient<?> graphServiceClient(TokenCredentialAuthProvider tokenCredentialAuthProvider) {
		return GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider).buildClient();
	}

	@Bean
	TextAnalyticsClient getTextAnalyticsClient() {
 
		return new TextAnalyticsClientBuilder().credential(new AzureKeyCredential(languageApiKey)).httpClient(httpClient2)
				.endpoint(languageEndpoint).buildClient();
	}

	@Primary
	@Bean
	 AzureVectorStore getCustomAzureVectorStore(SearchIndexClient searchIndexClient, SearchClient searchClient,
            EmbeddingModel embeddingModel, TextAnalyticsClient textAnalyticsClient) {
		return new CustomAzureVectorStore(searchIndexClient, searchClient, embeddingModel, textAnalyticsClient);
	}
}
