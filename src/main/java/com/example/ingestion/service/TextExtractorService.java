package com.example.ingestion.service;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentLine;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentPage;
import com.azure.core.util.BinaryData;
import com.example.ingestion.etl.Extractor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

@Service
public class TextExtractorService implements Extractor<InputStream, String> {

    private final DocumentAnalysisClient documentClient;
    private final Tika tika = new Tika();

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
                return tika.parseToString(new ByteArrayInputStream(data));
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
