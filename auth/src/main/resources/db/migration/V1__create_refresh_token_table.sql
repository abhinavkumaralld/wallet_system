CREATE TABLE refresh_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    refresh_token VARCHAR(255),
    expiry DATETIME(6),
    PRIMARY KEY (id)
);