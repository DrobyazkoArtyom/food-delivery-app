CREATE TABLE kitchens
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT  NOT NULL,
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

-- maybe this should have a composite key instead of id primary key
CREATE TABLE order_users
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL
);

CREATE TABLE order_items
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_id     BIGINT  NOT NULL REFERENCES order_users (id),
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

ALTER TABLE kitchens ADD CONSTRAINT fk_kitchens_users FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE order_users ADD CONSTRAINT fk_orders_users FOREIGN KEY (user_id) REFERENCES users(id);

CREATE TABLE authorities
(
    user_id   BIGINT      NOT NULL REFERENCES users (id),
    authority VARCHAR(50) NOT NULL
);

CREATE UNIQUE INDEX ix_auth_username ON authorities (user_id, authority);