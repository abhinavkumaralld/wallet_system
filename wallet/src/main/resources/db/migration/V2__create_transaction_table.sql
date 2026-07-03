CREATE TABLE transaction (
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DECIMAL(38,2),
    created_at DATETIME(6),
    description VARCHAR(255),
    receiver_user_id BIGINT,
    reference_id VARCHAR(255),
    sender_user_id BIGINT,
    status ENUM('FAILURE', 'PENDING', 'SUCCESS'),
    type ENUM('CREDIT', 'DEBIT', 'DIPOSIT', 'WITHDRAWAL'),
    wallet_id BIGINT,

    PRIMARY KEY (id),
    INDEX idx_transaction_type (type)
);