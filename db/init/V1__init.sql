-- 枚举类型
CREATE TYPE order_status AS ENUM ('PENDING','PAID','FAILED','CANCELLED','COMPLETED');
CREATE TYPE payment_method AS ENUM ('CREDIT_CARD','DEBIT_CARD','PAYPAL','WALLET','OTHER');

-- 用户表
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    country VARCHAR(50)
);

-- 游戏表
CREATE TABLE games (
    game_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    developer VARCHAR(200),
    description TEXT,
    price NUMERIC(12,2) NOT NULL,
    image_url VARCHAR(255),
    discount_price NUMERIC(12,2),
    genre VARCHAR(50),
    platform VARCHAR(50),
    release_date DATE,
    is_active BOOLEAN DEFAULT TRUE
);

-- 购物车表
CREATE TABLE carts (
    cart_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    status VARCHAR(32) DEFAULT 'ACTIVE',
    payment_method payment_method,
    discount_amount NUMERIC(12,2) DEFAULT 0,
    final_amount NUMERIC(12,2) DEFAULT 0,
    created_date TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_date TIMESTAMP NOT NULL DEFAULT now()
);

-- 购物车条目
CREATE TABLE cart_item (
    cart_item_id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES carts(cart_id) ON DELETE CASCADE,
    game_id BIGINT NOT NULL REFERENCES games(game_id),
    price NUMERIC(12,2) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    added_date TIMESTAMP NOT NULL DEFAULT now()
);

-- 订单表
CREATE TABLE orders (
    order_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    status order_status NOT NULL DEFAULT 'PENDING',
    order_date TIMESTAMP NOT NULL DEFAULT now(),
    completed_date TIMESTAMP,
    final_amount NUMERIC(12,2) NOT NULL
);

-- 订单条目（每个订单条目对应一个独立激活实例）
CREATE TABLE order_item (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    game_id BIGINT NOT NULL REFERENCES games(game_id),
    unit_price NUMERIC(12,2) NOT NULL,
    activation_code VARCHAR(100) UNIQUE -- 预留激活码字段
);
