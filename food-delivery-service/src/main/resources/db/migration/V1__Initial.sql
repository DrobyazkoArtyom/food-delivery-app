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

CREATE TABLE kitchens
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT  NOT NULL REFERENCES users (id),
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

CREATE TABLE orders
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT NOT NULL REFERENCES users (id),
    kitchen_id BIGINT NOT NULL REFERENCES kitchens (id)
);

CREATE TABLE order_items
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_id     BIGINT  NOT NULL REFERENCES orders (id),
    menu_item_id BIGINT  NOT NULL,
    name         VARCHAR NOT NULL,
    description  VARCHAR,
    unit_price   NUMERIC NOT NULL,
    quantity     INTEGER NOT NULL
);

CREATE TABLE order_status
(
    id     BIGINT PRIMARY KEY,
    order_status VARCHAR NOT NULL
);

CREATE TABLE order_status_history
(
    id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_id  BIGINT NOT NULL REFERENCES orders (id),
    order_status_id BIGINT NOT NULL REFERENCES order_status (id)
);

INSERT INTO order_status (id, order_status)
VALUES (1, 'CREATED'),
       (2, 'FINISHED');