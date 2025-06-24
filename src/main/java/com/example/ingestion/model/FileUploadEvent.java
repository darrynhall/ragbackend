package com.example.ingestion.model;

public record FileUploadEvent(String filename, String userId, long timestamp) {}
