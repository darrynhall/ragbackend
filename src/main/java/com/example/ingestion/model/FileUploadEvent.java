package com.example.ingestion.model;

import java.io.Serializable;

public record FileUploadEvent(String filename, String userId, long timestamp) implements Serializable{}
