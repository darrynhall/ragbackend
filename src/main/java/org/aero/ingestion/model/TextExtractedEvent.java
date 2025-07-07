package org.aero.ingestion.model;

import java.io.Serializable;

public record TextExtractedEvent(String filename, String text, String userId) implements Serializable{}
