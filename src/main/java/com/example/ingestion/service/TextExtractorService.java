package com.example.ingestion.service;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentLine;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentPage;
import com.azure.core.util.BinaryData;
import com.example.ingestion.etl.Extractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

@Service
public class TextExtractorService implements Extractor<InputStream, String> {

    private final DocumentAnalysisClient documentClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${unstructured.api.url}")
    private String unstructuredUrl;

    @Value("${unstructured.api.key}")
    private String unstructuredKey;

    public TextExtractorService(DocumentAnalysisClient documentClient) {
        this.documentClient = documentClient;
    }

    public String extract(InputStream inputStream, String filename) {
        try {
            byte[] data = inputStream.readAllBytes();
            if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
                AnalyzeResult result = documentClient
                        .beginAnalyzeDocument("prebuilt-read", BinaryData.fromBytes(data))
                        .getFinalResult();
                StringBuilder sb = new StringBuilder();
                for (DocumentPage page : result.getPages()) {
                    for (DocumentLine line : page.getLines()) {
                        sb.append(line.getContent()).append(System.lineSeparator());
                    }
                }
                return sb.toString();
            } else {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(unstructuredKey);
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                ByteArrayResource resource = new ByteArrayResource(data) {
                    @Override
                    public String getFilename() {
                        return filename == null ? "file" : filename;
                    }
                };
                org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
                body.add("files", resource);
                HttpEntity<org.springframework.util.MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(unstructuredUrl, request, String.class);
                return response.getBody();
            }
        } catch (Exception e) {
            throw new RuntimeException("Text extraction failed", e);
        }
    }

    @Override
    public String extract(InputStream inputStream) {
        return extract(inputStream, "");
    }
}
