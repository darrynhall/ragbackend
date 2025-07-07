package org.aero.ingestion.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class CustomDocumentReader implements DocumentReader {


  @Value("${input.directory:c:/temp/ingestion-files/}")
  private String inputDir;

  @Value("${input.filename.regex:*.{pdf,docx,txt,pages,csv}}")
  private String pattern;

  @SneakyThrows
  @Override
  public List<Document> get() {

    List<Document> documentList = new ArrayList<>(); 

    Files.newDirectoryStream(Path.of(inputDir), pattern).forEach(path -> {
      List<Document> documents = null;
      try {
        documents = new TikaDocumentReader(new ByteArrayResource(Files.readAllBytes(path))).get()
          .stream().peek(document -> {
             document.getMetadata().put("file_name", path.getFileName());
             document.getMetadata().put("file_uri", path.getRoot() + "/" + path.getFileName());

            
            log.info("Reading new document :: {}", path.getFileName());
          }).toList();
      } catch (IOException e) {
        throw new RuntimeException("Error while reading the file : " + path.toUri() + "::" + e);
      }
      documentList.addAll(documents);
    });
    return documentList;
  }
}