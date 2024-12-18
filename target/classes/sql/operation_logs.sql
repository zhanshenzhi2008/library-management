CREATE TABLE IF NOT EXISTS operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    operation VARCHAR(100) NOT NULL,
    details TEXT,
    timestamp DATETIME NOT NULL,
    INDEX idx_username (username),
    INDEX idx_timestamp (timestamp)
); 