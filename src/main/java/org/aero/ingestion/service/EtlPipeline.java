package org.aero.ingestion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class EtlPipeline {

	private final CustomDocumentReader documentReader;
	private final CustomAzureVectorStore vectorStore;
	private final TextSplitter textSplitter;

	public EtlPipeline(CustomAzureVectorStore vectorStore, TextSplitter textSplitter, CustomDocumentReader documentReader) {
		this.vectorStore = vectorStore;
		this.textSplitter = textSplitter;
		this.documentReader = documentReader;
	}

	public String runIngestion() {

		List<String> messages = new ArrayList<>();

		messages.add("RunIngestion() started");
		log.info(messages.get(messages.size() - 1));

		messages.add("RunIngestion() - get files");
		log.info(messages.get(messages.size() - 1));
		List<Document> files = documentReader.get();
		messages.add("RunIngestion() - %s files retrieved".formatted(files.size()));
		log.info(messages.get(messages.size() - 1));

		messages.add("RunIngestion() - start chunking files");
		log.info(messages.get(messages.size() - 1));
		List<Document> chunks = textSplitter.apply(files);
		messages.add("RunIngestion() - %s chunks created".formatted(chunks.size()));
		log.info(messages.get(messages.size() - 1));

		// List<SearchDocument> searchDocumentChunks = preProcessChunks(chunks);

		messages.add("RunIngestion() - writing chunks to vector store");
		log.info(messages.get(messages.size() - 1));
		vectorStore.add(chunks);

		messages.add("RunIngestion() finished");
		log.info(messages.get(messages.size() - 1));

		messages.add("RunIngestion() - %s chunks written to vector store".formatted(chunks.size()));
		log.info(messages.get(messages.size() - 1));
		return String.join("\n", messages);
	}



}
