CREATE TABLE kitchen
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name    VARCHAR NOT NULL,
    address VARCHAR NOT NULL,
    CONSTRAINT pk_kitchen PRIMARY KEY (id)
);

CREATE TABLE menu_item
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    kitchen_id  BIGINT REFERENCES kitchen (id),
    name        VARCHAR NOT NULL,
    description VARCHAR,
    price       numeric NOT NULL,
    hidden      BOOLEAN NOT NULL,
    CONSTRAINT pk_menu_item PRIMARY KEY (id)
);

CREATE TABLE order
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT  NOT NULL,
    status  VARCHAR NOT NULL,
    CONSTRAINT pk_order PRIMARY KEY (id)
);

CREATE TABLE order_item
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    menu_item_id BIGINT  NOT NULL,
    order_id     BIGINT REFERENCES order (id),
    name         VARCHAR NOT NULL,
    description  VARCHAR,
    unit_price   numeric NOT NULL,
    quantity     INTEGER NOT NULL,
    CONSTRAINT pk_order_item PRIMARY KEY (id)
);