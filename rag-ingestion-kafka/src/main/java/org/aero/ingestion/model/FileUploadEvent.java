package org.aero.ingestion.model;

public record FileUploadEvent(String filename, byte[] fileBytes) {}
