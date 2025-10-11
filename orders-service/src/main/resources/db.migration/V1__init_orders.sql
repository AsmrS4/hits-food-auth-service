CREATE TABLE order_rating (
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    rating DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (user_id, product_id)
);