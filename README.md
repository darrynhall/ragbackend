# ragbackend

This project provides services for a retrieval augmented generation (RAG) backend. It uses Spring Boot and ActiveMQ for its messaging pipeline.

## Development

Run the application with Maven:

```bash
mvn spring-boot:run
```

The default configuration expects an ActiveMQ instance running on `localhost:61616` with the default `admin`/`admin` credentials.
