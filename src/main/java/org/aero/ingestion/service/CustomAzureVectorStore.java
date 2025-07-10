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

		int batchSize = 100;
		
		for (int i = 0; i < documents.size(); i += batchSize) {
			
			List<Document> batch = documents.subList(i, Math.min(i + batchSize, documents.size()));
			
			List<com.azure.search.documents.SearchDocument> searchDocuments = batch.stream().map(this::toSearchDocument)
					.collect(Collectors.toList());
			
			IndexDocumentsResult result = this.searchClient.uploadDocuments(searchDocuments);
			
			log.info("Uploaded {} chunk documents. Success count: {}", batch.size(),
					result.getResults().stream().filter(r -> r.isSucceeded()).count());
		}
	}

	public void add(List<String> chunks, List<float[]> embeddings) {
		
		Assert.isTrue(chunks.size() == embeddings.size(), "Chunks and embeddings size mismatch");
		
		if (chunks.isEmpty()) {
			return;
		}
		
		int batchSize = 100;
		
		for (int i = 0; i < chunks.size(); i += batchSize) {
			
			List<com.azure.search.documents.SearchDocument> searchDocuments = new ArrayList<>();
			
			for (int j = i; j < Math.min(i + batchSize, chunks.size()); j++) {
				Document doc = new Document(chunks.get(j));
				searchDocuments.add(toSearchDocument(doc, embeddings.get(j)));
			}
			
			IndexDocumentsResult result = this.searchClient.uploadDocuments(searchDocuments);
			
			log.info("Uploaded {} chunk documents. Success count: {}", searchDocuments.size(),
					result.getResults().stream().filter(r -> r.isSucceeded()).count());
		}
	}

	private SearchDocument toSearchDocument(Document document) {
		float[] embedding = this.embeddingModel.embed(document);
		return toSearchDocument(document, embedding);
	}

	private SearchDocument toSearchDocument(Document document, float[] embedding) {

		// Create the Azure SearchDocument map
		com.azure.search.documents.SearchDocument searchDocument = new com.azure.search.documents.SearchDocument();
		try {
			searchDocument.put("id", document.getId());
			searchDocument.put("content", document.getText());
			searchDocument.put("embedding", embedding);
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

	List<String> extractEntities(Document document) {
		List<String> entities = textAnalyticsClient.recognizeEntities(document.getText()).stream()
				.map(entity -> entity.getText()).toList();
		return entities;

	}

	List<String> extractKeyPhrases(Document document) {
		// create an populated key phrases
		KeyPhrasesCollection key_phrases = textAnalyticsClient.extractKeyPhrases(document.getText());
		List<String> allKeyPhrases = new ArrayList<>();
		key_phrases.forEach(documentResult -> {
			allKeyPhrases.add(documentResult);
		});
		return allKeyPhrases;

	}

}