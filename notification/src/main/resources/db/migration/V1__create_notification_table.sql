CREATE TABLE notification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    reference_id VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    processed_at DATETIME(6) DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_notification_reference_id UNIQUE (reference_id)
);