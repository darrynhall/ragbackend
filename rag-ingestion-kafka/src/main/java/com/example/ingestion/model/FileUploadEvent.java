package com.example.ingestion.model;

public record FileUploadEvent(String filename, byte[] fileBytes) {}
