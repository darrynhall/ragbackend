package org.aero.ingestion.service;

import java.util.Arrays;
import java.util.List;

import org.aero.ingestion.controller.FileUploadController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.models.BM25SimilarityAlgorithm;
import com.azure.search.documents.indexes.models.HnswAlgorithmConfiguration;
import com.azure.search.documents.indexes.models.HnswParameters;
import com.azure.search.documents.indexes.models.LexicalAnalyzerName;
import com.azure.search.documents.indexes.models.SearchField;
import com.azure.search.documents.indexes.models.SearchFieldDataType;
import com.azure.search.documents.indexes.models.SearchIndex;
import com.azure.search.documents.indexes.models.SemanticConfiguration;
import com.azure.search.documents.indexes.models.SemanticField;
import com.azure.search.documents.indexes.models.SemanticPrioritizedFields;
import com.azure.search.documents.indexes.models.SemanticSearch;
import com.azure.search.documents.indexes.models.VectorSearch;
import com.azure.search.documents.indexes.models.VectorSearchAlgorithmConfiguration;
import com.azure.search.documents.indexes.models.VectorSearchAlgorithmMetric;
import com.azure.search.documents.indexes.models.VectorSearchProfile;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class IndexCreationService {

	@Autowired
	FileUploadController fileUploadController;

	private final SearchIndexClient searchIndexClient;

	@Value("${spring.ai.azure.search.index-name}")
	private String indexName;

	public IndexCreationService(SearchIndexClient searchIndexClient) {
		this.searchIndexClient = searchIndexClient;
	}

	@PostConstruct
	public void initi() {
		createOrUpdateIndex();
		log.info(fileUploadController.ingestFromTemp());
	}

	/**
	 * Creates or updates the search index based on the predefined structure.
	 */

	public void createOrUpdateIndex() {
		log.info("Starting index creation process for index: {}", indexName);

		// Build the SearchIndex object
		SearchIndex searchIndex = new SearchIndex(indexName).setFields(buildSearchFields())
				.setSimilarity(new BM25SimilarityAlgorithm().setK1(1.2).setB(0.75))
				.setSemanticSearch(buildSemanticSearch()).setVectorSearch(buildVectorSearch());

		// Use createOrUpdateIndex to avoid errors if the index already exists.
		// This is an idempotent operation.
		log.info("Sending request to Azure to create or update the index...");
		SearchIndex createdIndex = searchIndexClient.createOrUpdateIndex(searchIndex);
		log.info("Successfully created or updated index '{}' with eTag: {}", createdIndex.getName(),
				createdIndex.getETag());

		SearchIndex index = searchIndexClient.getIndex(indexName);

		System.out.println("Fields defined in index '" + indexName + "':");
		index.getFields().forEach(field -> {
			System.out.println("- " + field.getName() + " (Type: " + field.getType() + ")");
		});
	}

	/**
	 * Defines all the fields for the search index.
	 */
	// disable auto formatting
	// @formatter:off
	private List<SearchField> buildSearchFields() {
		return Arrays.asList(
				new SearchField("id", SearchFieldDataType.STRING)
				.setKey(true).setFilterable(true).setSortable(true).setFacetable(true) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
						
				new SearchField("file_name", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(true).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
						
				new SearchField("file_uri", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(false).setSortable(false).setFacetable(false) .setSearchable(false),
				
				new SearchField("processed_datetime", SearchFieldDataType.DATE_TIME_OFFSET)
				.setKey(false).setFilterable(true).setSortable(true).setFacetable(false) .setSearchable(false),
						
				new SearchField("chunk_file", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(true).setSortable(true).setFacetable(false) .setSearchable(false),
				
				new SearchField("file_class", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(true).setSortable(true).setFacetable(true) .setSearchable(false),
				
				new SearchField("uploader_badge", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(true).setSortable(false).setFacetable(true) .setSearchable(true),
				
				new SearchField("folder", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(true).setSortable(true).setFacetable(true) .setSearchable(false),
						
				new SearchField("tags", SearchFieldDataType.collection(SearchFieldDataType.STRING))
				.setKey(false).setFilterable(true).setSortable(false).setFacetable(true) .setSearchable(false),
				
				new SearchField("pages", SearchFieldDataType.collection(SearchFieldDataType.INT32))
				.setKey(false).setFilterable(false).setSortable(false).setFacetable(false) .setSearchable(false),					
				
				new SearchField("source", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(false).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
				
				new SearchField("allowed_users", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(true).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
				
				new SearchField("allowed_groups", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(true).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
				
				new SearchField("sharepoint_permissions", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(true).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
				
				new SearchField("title", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(false).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
						
				new SearchField("translated_title", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(false).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
				
				new SearchField("content", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(false).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
						
				new SearchField("entities", SearchFieldDataType.collection(SearchFieldDataType.STRING))
				.setKey(false).setFilterable(false).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
						
				new SearchField("key_phrases", SearchFieldDataType.collection(SearchFieldDataType.STRING))
				.setKey(false).setFilterable(false).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
						
				// added metadata field which is a default Document field
				new SearchField("metadata", SearchFieldDataType.STRING)
				.setKey(false).setFilterable(true).setSortable(false).setFacetable(false) .setSearchable(true)
				.setAnalyzerName(LexicalAnalyzerName.STANDARD_LUCENE),
				// changed contentVector to embedding which is a default Docum	 
			 
				// Vector Field Definition
				new SearchField("embedding", SearchFieldDataType.collection(SearchFieldDataType.SINGLE))
						.setSearchable(true).setVectorSearchDimensions(3072)
						.setVectorSearchProfileName("vector-config-profile"))
		;
	}
	// @formatter:on
	/**
	 * Defines the semantic search configuration.
	 */
	private SemanticSearch buildSemanticSearch() {
		return new SemanticSearch().setConfigurations(List.of(new SemanticConfiguration("default",
				new SemanticPrioritizedFields().setTitleField(new SemanticField("title"))
						.setContentFields(List.of(new SemanticField("content")))
						.setKeywordsFields(List.of(new SemanticField("key_phrases"), new SemanticField("entities"))))));
	}

	/**
	 * Defines the vector search configuration (algorithms and profiles).
	 */
	private VectorSearch buildVectorSearch() {
		// Define the HNSW algorithm configuration
		VectorSearchAlgorithmConfiguration hnswAlgorithm = new HnswAlgorithmConfiguration("vector-config")
				.setParameters(new HnswParameters().setMetric(VectorSearchAlgorithmMetric.COSINE).setM(4)
						.setEfConstruction(400).setEfSearch(500));

		// Define the profile that uses the algorithm
		VectorSearchProfile vectorProfile = new VectorSearchProfile("vector-config-profile", "vector-config");

		return new VectorSearch().setAlgorithms(List.of(hnswAlgorithm)).setProfiles(List.of(vectorProfile));
	}
}