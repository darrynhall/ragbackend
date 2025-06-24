CREATE TABLE ingestion_status (
    filename VARCHAR(512) PRIMARY KEY,
    user_id VARCHAR(255),
    status VARCHAR(32),
    error_message TEXT,
    timestamp TIMESTAMP
);
