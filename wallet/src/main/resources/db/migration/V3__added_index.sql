-- Transaction history queries always filter by wallet_id
-- Without this index → full table scan on every paginated request
CREATE INDEX idx_transaction_wallet_id
ON transaction(wallet_id);

-- Transaction history sorted by created_at DESC
-- Composite index covers both filter + sort in one index
CREATE INDEX idx_transaction_wallet_created
ON transaction(wallet_id, created_at DESC);

-- Reference ID lookups for idempotency checks
CREATE INDEX idx_transaction_reference_id
ON transaction(reference_id);

-- Wallet lookup by user_id happens on every request
CREATE INDEX idx_wallet_user_id
ON wallet(user_id);


