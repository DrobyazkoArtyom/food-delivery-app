CREATE TABLE kitchens
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name    VARCHAR NOT NULL,
    address VARCHAR NOT NULL
);

CREATE TABLE menu_items
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    kitchen_id  BIGINT  NOT NULL REFERENCES kitchens (id),
    name        VARCHAR NOT NULL,
    description VARCHAR,
    price       NUMERIC NOT NULL,
    hidden      BOOLEAN NOT NULL
);

CREATE TABLE customer_orders
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL
);

CREATE TABLE order_items
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_id     BIGINT  NOT NULL REFERENCES customer_orders (id),
    menu_item_id BIGINT  NOT NULL,
    name         VARCHAR NOT NULL,
    description  VARCHAR,
    unit_price   NUMERIC NOT NULL,
    quantity     INTEGER NOT NULL
);

--spring security default schema uses varchar_ignorecase, might need to use a postgres collation to achieve that
CREATE TABLE users
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(500) NOT NULL,
    enabled  boolean      NOT NULL
);

CREATE TABLE authorities
(
    user_id   BIGINT      NOT NULL REFERENCES users (id),
    authority VARCHAR(50) NOT NULL
);

CREATE UNIQUE INDEX ix_auth_username ON authorities (user_id, authority);