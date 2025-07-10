package org.aero.ingestion.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentPage;
import com.azure.ai.formrecognizer.documentanalysis.models.OperationResult;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * DocumentReader implementation that reads the uploaded file from the
 * configured {@link FileStorageService} and uses the
 * {@link TextExtractorService} to extract text.
 */
@RequiredArgsConstructor
@Log4j2

public class CustomDocumentReader implements DocumentReader {

	private final String filename;
	private final FileStorageService storageService;
	private final DocumentAnalysisClient documentAnalysisClient;
	private final boolean useAzure;

	@Override
	public List<Document> get() {
		List<Document> documentList = new ArrayList<>();

		try {
			InputStream is = storageService.getFileInputStream(filename);
			if (useAzure) {
				// get length of stream
				Long contentLength = (long) is.available();
				is.reset();
				BinaryData binaryData = BinaryData.fromStream(is, contentLength);

				// Azure Document Intelligence extraction
				SyncPoller<OperationResult, AnalyzeResult> analyzeResultPoller = documentAnalysisClient
						.beginAnalyzeDocument("prebuilt-read", binaryData);

				List<DocumentPage> pages = analyzeResultPoller.getFinalResult().getPages();

				StringBuilder sb = new StringBuilder();
				for (DocumentPage page : pages) {
					//EXPLAI
					sb.append(page.getLines().stream().map(l -> l.getContent()).reduce("", (a, b) -> a + "\n" + b));
				}
				Document doc = new Document(sb.toString());
				doc.getMetadata().put("file_name", filename);
				doc.getMetadata().put("file_uri", filename);
				log.info("[Azure] Reading new document :: {}", filename);
				documentList.add(doc);
			} else {
				// Tika extraction
				List<Document> documents = new TikaDocumentReader(convert(is)).get().stream().peek(document -> {
					document.getMetadata().put("file_name", filename);
					log.info("[Tika] Reading new document :: {}", filename);
				}).toList();
				documentList.addAll(documents);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while reading the file : " + filename + "::" + e);
		}
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
