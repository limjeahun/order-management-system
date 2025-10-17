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
    PRIMARY KEY (id)
) ENGINE=InnoDB;