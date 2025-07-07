package org.aero.ingestion.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.azure.AzureVectorStore;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.models.KeyPhrasesCollection;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.models.IndexDocumentsResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CustomAzureVectorStore extends AzureVectorStore {

	private final SearchClient searchClient;
	private final EmbeddingModel embeddingModel;
	private final TextAnalyticsClient textAnalyticsClient;
 

	public CustomAzureVectorStore(SearchIndexClient searchIndexClient, SearchClient searchClient,
			EmbeddingModel embeddingModel, TextAnalyticsClient textAnalyticsClient) {
		super(AzureVectorStore.builder(searchIndexClient, embeddingModel));
		this.searchClient = searchClient;
		this.embeddingModel = embeddingModel;
		this.textAnalyticsClient = textAnalyticsClient;

	}

	@Override
	public void add(List<Document> documents) {
		Assert.notNull(documents, "Documents must not be null");
		if (documents.isEmpty()) {
			return;
		}

		// Create a list of Azure SearchDocuments from the Spring AI Documents
		List<com.azure.search.documents.SearchDocument> searchDocuments = documents.stream().map(this::toSearchDocument)
				.collect(Collectors.toList());

		// Upload the documents using the underlying client
		IndexDocumentsResult result = this.searchClient.uploadDocuments(searchDocuments);

		// Optional: Add logging to check for success
		log.info("Uploaded {} documents. Success count: {}", documents.size(),
				result.getResults().stream().filter(r -> r.isSucceeded()).count());
	}

	private com.azure.search.documents.SearchDocument toSearchDocument(Document document) {
		// Generate the embedding for the content
		float[] embedding = this.embeddingModel.embed(document);

		// Create the Azure SearchDocument map
		com.azure.search.documents.SearchDocument searchDocument = new com.azure.search.documents.SearchDocument();
		searchDocument.put("id", document.getId());
		searchDocument.put("content", document.getFormattedContent());
		searchDocument.put("embedding", embedding);

		try {
			searchDocument.put("processed_datetime", Calendar.getInstance().getTime());
			searchDocument.put("chunk_file", "[should be chunk file path]");
			searchDocument.put("file_class", "text");
			searchDocument.put("folder", "blob store folder containing uploaded file");
//			searchDocument.put("tags", "[tags]");
//			searchDocument.put("pages", "[pages]");
			searchDocument.put("title", "[title]");
			searchDocument.put("translated_title", "[translated_title]");
			
			//create and populated entities
			List<String> entities = textAnalyticsClient.recognizeEntities(document.getText()).stream()
					.map(entity -> entity.getText()).toList();
			log.info("entities: " + entities);
			searchDocument.put("entities", entities);

			//create an populated key phrases
			KeyPhrasesCollection key_phrases = textAnalyticsClient.extractKeyPhrases(document.getText()); 
			List<String> allKeyPhrases = new ArrayList<>();
			key_phrases.forEach(documentResult -> {
				allKeyPhrases.add(documentResult);
			});
			
			log.info("allKeyPhrases: " + allKeyPhrases);
		 	searchDocument.put("key_phrases", allKeyPhrases);
		 	
		 	Map<String, Object> searchDocumentCopy = new HashMap<String, Object>(searchDocument);
		 	searchDocumentCopy.remove("embedding");
		 	
		 	searchDocument.put("metadata", new ObjectMapper().writeValueAsString(searchDocumentCopy));

		} catch (Exception e) {
			log.error("Error processing document: {}", searchDocument.get("file_name"), e);
		}

		return searchDocument;
	}



//	private void preProcessChunks(List<Document> chunks) {
//		log.info("Preprocessing chunks...");
//
//		chunks.stream().forEach(document -> {
//			try {
//				document.getMetadata().put("processed_datetime", Calendar.getInstance().getTime());
//				document.getMetadata().put("chunk_file", "[chunk_file]");
//				document.getMetadata().put("file_class", "text");
//				document.getMetadata().put("folder", "folder");
//				document.getMetadata().put("tags", "[tags]");
//				document.getMetadata().put("pages", "[pages]");
//				document.getMetadata().put("title", "[title]");
//				document.getMetadata().put("translated_title", "[translated_title]");
//				List<String> entities = textAnalyticsClient.recognizeEntities(document.getText()).stream()
//						.map(entity -> entity.getText()).toList();
//				log.info(entities);
//				document.getMetadata().put("entities", entities);
//
//				KeyPhrasesCollection key_phrases = textAnalyticsClient.extractKeyPhrases(document.getText());
//
//				log.info(key_phrases);
//
//				List<String> allKeyPhrases = new ArrayList<>();
//
//				// 1. Loop through the result for each document
//				key_phrases.forEach(documentResult -> {
//					allKeyPhrases.add(documentResult);
//				});
//				document.getMetadata().put("key_phrases", allKeyPhrases);
//
//			} catch (Exception e) {
//				log.error("Error processing document: {}", document.getMetadata().get("file_name"), e);
//			}
//		});
//
//	}
}