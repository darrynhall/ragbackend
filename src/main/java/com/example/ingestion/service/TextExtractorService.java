package com.example.ingestion.service;

 

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentLine;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentPage;
import com.azure.core.util.BinaryData;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TextExtractorService {
	
	private static int MAX_CHARACTERS_TO_LOG = 1000;

	private final DocumentAnalysisClient documentClient;
	private final Tika tika = new Tika();

	public TextExtractorService(DocumentAnalysisClient documentClient) {
		this.documentClient = documentClient;
	}

	public String extract(InputStream inputStream, String filename) {
		try {
			String extractedText = null;
			byte[] data = inputStream.readAllBytes();
			if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
				AnalyzeResult result = documentClient.beginAnalyzeDocument("prebuilt-read", BinaryData.fromBytes(data))
						.getFinalResult();
				StringBuilder sb = new StringBuilder();
				for (DocumentPage page : result.getPages()) {
					for (DocumentLine line : page.getLines()) {
						sb.append(line.getContent()).append(System.lineSeparator());
					}
				}

				extractedText = sb.toString();
			} else {
				extractedText = tika.parseToString(new ByteArrayInputStream(data));
			}
			log.info("Extracted text from {}: {}", filename, extractedText.length() > MAX_CHARACTERS_TO_LOG ? extractedText.substring(0, MAX_CHARACTERS_TO_LOG) + "..." : extractedText);
			return extractedText;
		} catch (Exception e) {
			throw new RuntimeException("Text extraction failed", e);
		}
	}

	public String extract(InputStream inputStream) {
		return extract(inputStream, "");
	}
}
