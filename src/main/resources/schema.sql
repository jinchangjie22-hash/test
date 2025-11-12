CREATE TABLE IF NOT EXISTS orders (
    order_id VARCHAR(64) PRIMARY KEY,
    order_no VARCHAR(64) NULL,
    status VARCHAR(32) NULL,
    total_amount DECIMAL(19, 2) NULL,
    currency VARCHAR(16) NULL,
    buyer_name VARCHAR(128) NULL,
    receiver_name VARCHAR(128) NULL,
    receiver_phone VARCHAR(32) NULL,
    order_time DATETIME NULL,
    updated_at DATETIME NULL,
    raw_payload TEXT NULL
);
