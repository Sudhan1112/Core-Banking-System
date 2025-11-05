CREATE TABLE kyc (
    kyc_id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(50) NOT NULL,
    document_url VARCHAR(500),
    document_hash VARCHAR(64),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    submission_date TIMESTAMP,
    verification_date TIMESTAMP,
    verified_by BIGINT,
    rejection_reason TEXT,
    expiry_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    additional_info TEXT,
    is_primary_document BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_kyc_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_kyc_verifier FOREIGN KEY (verified_by) REFERENCES users(user_id),
    CONSTRAINT uq_kyc_user_document UNIQUE (user_id, document_type),
    CONSTRAINT uq_kyc_document_number UNIQUE (document_number),
    CONSTRAINT chk_kyc_status CHECK (status IN ('PENDING', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'EXPIRED', 'RESUBMISSION_REQUIRED'))
);

CREATE INDEX idx_kyc_user_id ON kyc(user_id);
CREATE INDEX idx_kyc_status ON kyc(status);
CREATE INDEX idx_kyc_document_type ON kyc(document_type);
CREATE INDEX idx_kyc_submission_date ON kyc(submission_date);
CREATE INDEX idx_kyc_expiry_date ON kyc(expiry_date);
CREATE INDEX idx_kyc_primary_document ON kyc(is_primary_document);