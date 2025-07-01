# ragbackend

This project provides services for a retrieval augmented generation (RAG) backend. It uses Spring Boot and ActiveMQ for its messaging pipeline.

## Development

Run the application with Maven:

```bash
mvn spring-boot:run
```

An embedded ActiveMQ broker will start automatically on `localhost:61616` with the default `admin`/`admin` credentials. You can still point the application at an external broker by overriding `spring.activemq.broker-url`.

## Text Extraction

PDF files are processed using Azure Document Intelligence. Other document
types are parsed locally with Apache Tika. Configure the Azure endpoint and
key in `application.yml` under `azure.document`.
