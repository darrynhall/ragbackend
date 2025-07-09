package org.aero.ingestion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class EtlPipeline {

	private final DocumentReader documentReader;
	private final CustomAzureVectorStore vectorStore;
	private final TextSplitter textSplitter;

	public EtlPipeline(CustomAzureVectorStore vectorStore, TextSplitter textSplitter,
			DocumentReader documentReader) {
		this.vectorStore = vectorStore;
		this.textSplitter = textSplitter;
		this.documentReader = documentReader;
	}

	public String runIngestion() {
		List<String> messages = new ArrayList<>();
		messages.add("RunIngestion() - get files");
		displayLastMessage(messages);
		List<Document> files = documentReader.get();
		messages.add("RunIngestion() - %s files retrieved".formatted(files.size()));
		displayLastMessage(messages);
		return String.join("\n", messages);
	}

	public String runIngestion(List<Document> files) {

		List<String> messages = new ArrayList<>();

		messages.add("RunIngestion() - start chunking files");
		displayLastMessage(messages);
		List<Document> chunks = textSplitter.apply(files);
		messages.add("RunIngestion() - %s chunks created".formatted(chunks.size()));
		log.info(messages.get(messages.size() - 1));

		messages.add("RunIngestion() - adding vector store");
		displayLastMessage(messages);
		vectorStore.add(chunks);

		messages.add("RunIngestion() finished");
		displayLastMessage(messages);

		messages.add("RunIngestion() - %s chunks written to vector store".formatted(chunks.size()));
		displayLastMessage(messages);
		return String.join("\n", messages);
	}

	
	void displayLastMessage(List<String> messages) {
		log.info(messages.get(messages.size() - 1));
	}
}
