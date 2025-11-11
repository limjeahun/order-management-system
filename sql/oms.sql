CREATE TABLE orders (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    member_id        BIGINT NOT NULL,
    status           VARCHAR(255) NOT NULL,
    order_date       DATETIME(6) NOT NULL,
    receiver_name    VARCHAR(255) NOT NULL,
    receiver_phone   VARCHAR(255) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) engine=InnoDB;
ALTER TABLE orders ADD COLUMN trace_id VARCHAR(36) NOT NULL UNIQUE AFTER id;
ALTER TABLE orders ADD UNIQUE INDEX uk_trace_id (trace_id);


CREATE TABLE order_item (
    id         BIGINT NOT NULL AUTO_INCREMENT,
    order_id   BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    price      DECIMAL(19, 2) NOT NULL,
    quantity   INT NOT NULL,
    PRIMARY KEY (id)
) engine=InnoDB;


CREATE TABLE product (
    id    BIGINT NOT NULL,
    name  VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    stock BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

ALTER TABLE product ADD COLUMN version BIGINT NOT NULL DEFAULT 0;


-- payment 테이블
CREATE TABLE payment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    payment_key VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    requested_at DATETIME(6) NOT NULL,
    approved_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_order_id (order_id),
    UNIQUE KEY uk_payment_payment_key (payment_key)
) ENGINE=InnoDB;

-- shipping 테이블
CREATE TABLE shipping (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL UNIQUE,          -- 해당 주문 ID (1:1 관계)
    tracking_number VARCHAR(255) UNIQUE,      -- 배송 추적 번호
    status VARCHAR(50) NOT NULL,              -- 배송 상태 (예: PREPARING, SHIPPED, DELIVERED)
    recipient_name VARCHAR(255) NOT NULL,
    recipient_phone VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    shipped_at DATETIME(6),                    -- 배송 시작 일시
    delivered_at DATETIME(6),                  -- 배송 완료 일시
    PRIMARY KEY (id),
    INDEX idx_shipping_order_id (order_id)    -- 주문 ID 인덱스
) engine=InnoDB;

-- member 테이블
CREATE TABLE member (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE, -- 로그인 ID
    password VARCHAR(255) NOT NULL,        -- 해시된 비밀번호
    role VARCHAR(50) NOT NULL,             -- 권한 (예: ROLE_USER)
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    INDEX idx_member_username (username)
) ENGINE=InnoDB;