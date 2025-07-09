package org.aero.ingestion.entity;

import java.io.Serializable;
import java.util.List;

public record ChunksGeneratedEvent(String filename, List<String> chunks, String userId) implements Serializable {}
