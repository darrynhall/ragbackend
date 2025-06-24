CREATE TABLE chunk_hash (
    hash VARCHAR(255) PRIMARY KEY,
    filename VARCHAR(512),
    ingested_at TIMESTAMP
);
