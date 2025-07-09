package org.aero.ingestion.entity;

import java.io.Serializable;

public record TextExtractedEvent(String filename, String text, String userId) implements Serializable{}
