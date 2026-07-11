DROP TABLE IF EXISTS transaction;

CREATE TABLE transaction (
    id BIGINT NOT NULL AUTO_INCREMENT,
    wallet_id BIGINT,
    type ENUM('DEBIT','CREDIT','WITHDRAWAL','DIPOSIT') NOT NULL,
    amount DECIMAL(38,2) NOT NULL,
    status ENUM('SUCCESS','FAILURE','PENDING') NOT NULL,
    sender_user_id BIGINT,
    receiver_user_id BIGINT,
    reference_id VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME(6),

    PRIMARY KEY (id),

    CONSTRAINT ref_id UNIQUE (type, reference_id),

    INDEX idx_transaction_type (type)
);

