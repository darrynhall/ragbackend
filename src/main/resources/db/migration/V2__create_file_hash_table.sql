CREATE TABLE file_hash (
    hash VARCHAR(255) PRIMARY KEY,
    filename VARCHAR(512),
    size BIGINT,
    ingested_at TIMESTAMP
);
