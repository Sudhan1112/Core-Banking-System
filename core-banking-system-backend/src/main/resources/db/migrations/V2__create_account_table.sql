CREATE TABLE accounts (
    account_id SERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL,
    balance DECIMAL(19,4) NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    branch_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_APPROVAL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    minimum_balance DECIMAL(19,4),
    interest_rate DECIMAL(5,4),
    overdraft_limit DECIMAL(19,4),
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0 OR overdraft_limit IS NOT NULL),
    CONSTRAINT chk_interest_rate_positive CHECK (interest_rate >= 0),
    CONSTRAINT chk_overdraft_non_negative CHECK (overdraft_limit >= 0)
);

CREATE INDEX idx_account_user_id ON accounts(user_id);
CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_account_status ON accounts(status);
CREATE INDEX idx_account_type ON accounts(account_type);