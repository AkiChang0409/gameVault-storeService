DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

CREATE TABLE orders (
                        order_id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        order_date TIMESTAMP NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        payment_method VARCHAR(50),
                        final_amount NUMERIC(19,2)
);

CREATE TABLE order_items (
                             order_item_id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
                             user_id BIGINT NOT NULL,
                             order_date TIMESTAMP NOT NULL,
                             order_status VARCHAR(50) NOT NULL,
                             game_id BIGINT NOT NULL,
                             unit_price NUMERIC(19,2) NOT NULL
);
