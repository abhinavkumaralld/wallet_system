CREATE TABLE wallet (
    id BIGINT NOT NULL AUTO_INCREMENT,
    balance DECIMAL(38,2),
    created_at DATETIME(6),
    user_id BIGINT,
    version BIGINT,

    PRIMARY KEY (id)
);