# ragbackend

This project provides services for a retrieval augmented generation (RAG) backend. It uses Spring Boot and an embedded Artemis message broker for its messaging pipeline.

## Development

Run the application with Maven:

```bash
mvn spring-boot:run
```

An embedded Artemis broker will start automatically on `localhost:61616` with the default `admin`/`admin` credentials. You can still point the application at an external broker by overriding `spring.artemis.mode` and related properties.
