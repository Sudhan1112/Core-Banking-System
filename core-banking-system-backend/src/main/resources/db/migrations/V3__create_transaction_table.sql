CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    transaction_reference VARCHAR(30) NOT NULL UNIQUE,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    source_account_id BIGINT,
    destination_account_id BIGINT,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    description TEXT,
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    balance_after DECIMAL(19,4),
    fee_amount DECIMAL(19,4),
    related_transaction_id BIGINT,
    CONSTRAINT fk_transaction_source_account FOREIGN KEY (source_account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_transaction_destination_account FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_transaction_related FOREIGN KEY (related_transaction_id) REFERENCES transactions(transaction_id),
    CONSTRAINT chk_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_fee_non_negative CHECK (fee_amount >= 0),
    CONSTRAINT chk_source_or_destination CHECK (source_account_id IS NOT NULL OR destination_account_id IS NOT NULL)
);

CREATE INDEX idx_transaction_source_account ON transactions(source_account_id);
CREATE INDEX idx_transaction_destination_account ON transactions(destination_account_id);
CREATE INDEX idx_transaction_user ON transactions(user_id);
CREATE INDEX idx_transaction_status ON transactions(status);
CREATE INDEX idx_transaction_type ON transactions(transaction_type);
CREATE INDEX idx_transaction_date ON transactions(transaction_date);
CREATE INDEX idx_transaction_reference ON transactions(transaction_reference);