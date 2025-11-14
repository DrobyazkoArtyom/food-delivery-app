CREATE TABLE kitchen
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name    VARCHAR NOT NULL,
    address VARCHAR NOT NULL
);

CREATE TABLE menu_item
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    kitchen_id  BIGINT REFERENCES kitchen (id),
    name        VARCHAR NOT NULL,
    description VARCHAR,
    price       numeric NOT NULL,
    hidden      BOOLEAN NOT NULL
);

CREATE TABLE customer_order
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT  NOT NULL,
    status  VARCHAR NOT NULL
);

CREATE TABLE order_item
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    menu_item_id BIGINT  NOT NULL,
    order_id     BIGINT REFERENCES customer_order (id),
    name         VARCHAR NOT NULL,
    description  VARCHAR,
    unit_price   numeric NOT NULL,
    quantity     INTEGER NOT NULL
);