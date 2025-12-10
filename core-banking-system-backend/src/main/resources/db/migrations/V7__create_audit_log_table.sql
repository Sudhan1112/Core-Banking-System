-- src/main/resources/db/migration/V7__create_audit_log_table.sql
CREATE TABLE audit_logs (
    audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_user_id (user_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_entity_type (entity_type),
    INDEX idx_audit_created_at (created_at),
    INDEX idx_audit_user_action (user_id, action),
    INDEX idx_audit_date_range (created_at, user_id)
);