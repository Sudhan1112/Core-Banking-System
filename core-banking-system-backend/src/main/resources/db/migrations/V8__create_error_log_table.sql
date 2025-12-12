CREATE TABLE error_logs (
    error_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    error_level VARCHAR(20) NOT NULL,
    error_message TEXT NOT NULL,
    stack_trace LONGTEXT,
    user_id BIGINT,
    request_url VARCHAR(2048),
    request_method VARCHAR(10),
    ip_address VARCHAR(45),
    user_agent TEXT,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_error_level (error_level),
    INDEX idx_error_user_id (user_id),
    INDEX idx_error_created_at (created_at),
    INDEX idx_error_date_range (created_at, error_level)
);