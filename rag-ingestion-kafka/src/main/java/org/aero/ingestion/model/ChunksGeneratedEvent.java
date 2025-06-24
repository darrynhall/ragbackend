package org.aero.ingestion.model;

import java.util.List;

public record ChunksGeneratedEvent(String filename, List<String> chunks) {}
