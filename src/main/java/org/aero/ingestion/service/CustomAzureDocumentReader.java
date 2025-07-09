package org.aero.ingestion.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * DocumentReader implementation that reads the uploaded file from the
 * configured {@link FileStorageService} and uses the
 * {@link TextExtractorService} to extract text.
 */
@RequiredArgsConstructor
@Log4j2

public class CustomAzureDocumentReader implements DocumentReader {

	private final String filename;
	private final FileStorageService storageService;

	@Override
	public List<Document> get() {
		List<Document> documentList = new ArrayList<>();

		List<Document> documents = null;
		try {
			InputStream is = storageService.getFileInputStream(filename);
			documents = new TikaDocumentReader(convert(is)).get().stream().peek(document -> {
				document.getMetadata().put("file_name", filename);
				document.getMetadata().put("file_uri", filename);

				log.info("Reading new document :: {}", filename);
			}).toList();
		} catch (Exception e) {
			throw new RuntimeException("Error while reading the file : " + filename + "::" + e);
		}
		documentList.addAll(documents);
		return documentList;

	}

	@Override
	public List<Document> read() {
		return get();
	}

	public static Resource convert(InputStream inputStream) {
		if (inputStream == null) {
			throw new IllegalArgumentException("InputStream cannot be null.");
		}
		return new InputStreamResource(inputStream);
	}
}
