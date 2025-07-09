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
import com.azure.search.documents.SearchDocument;
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
		log.info("Uploaded {} chunks documents. Success count: {}", documents.size(),
				result.getResults().stream().filter(r -> r.isSucceeded()).count());
	}

	private SearchDocument toSearchDocument(Document document) {
		// Generate the embedding for the content
		float[] embedding = this.embeddingModel.embed(document);

		// Create the Azure SearchDocument map
		com.azure.search.documents.SearchDocument searchDocument = new com.azure.search.documents.SearchDocument();
		searchDocument.put("id", document.getId());
		searchDocument.put("content", document.getText());
		searchDocument.put("embedding", embedding);

		try {
			searchDocument.put("processed_datetime", Calendar.getInstance().getTime());
			searchDocument.put("chunk_file", "[should be chunk file path]");
			searchDocument.put("file_class", "[text]");
			searchDocument.put("folder", "[blob store folder containing uploaded file]");
//			searchDocument.put("tags", "[tags]");
//			searchDocument.put("pages", "[pages]");
			searchDocument.put("title", "[title]");
			searchDocument.put("translated_title", "[translated_title]");

			// create and populate entities
			searchDocument.put("entities", extractEntities(document));
			log.info("entities: " + searchDocument.get("entities"));

			// create an populate key phrases
			searchDocument.put("key_phrases", extractKeyPhrases(document));
			log.info("key_phrases: " + searchDocument.get("key_phrases"));

			Map<String, Object> searchDocumentCopy = new HashMap<String, Object>(searchDocument);
			searchDocumentCopy.remove("embedding");
			searchDocument.put("metadata", new ObjectMapper().writeValueAsString(searchDocumentCopy));

		} catch (Exception e) {
			log.error("Error processing document: {}", searchDocument.get("file_name"), e);
		}

		return searchDocument;
	}

	List<String>  extractEntities(Document document){
		List<String> entities = textAnalyticsClient.recognizeEntities(document.getText()).stream()
				.map(entity -> entity.getText()).toList();
		log.info("entities: " + entities);
		return entities;
		
	}

	List<String>  extractKeyPhrases(Document document){

		//create an populated key phrases
		KeyPhrasesCollection key_phrases = textAnalyticsClient.extractKeyPhrases(document.getText()); 
		List<String> allKeyPhrases = new ArrayList<>();
		key_phrases.forEach(documentResult -> {
			allKeyPhrases.add(documentResult);
		});
		
		return allKeyPhrases;
		
	}

}