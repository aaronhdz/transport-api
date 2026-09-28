CREATE TABLE drivers (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    origin VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_orders_status CHECK (status IN ('CREATED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED'))
);

CREATE TABLE order_assignments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    driver_id UUID NOT NULL,
    assigned_at TIMESTAMP NOT NULL,
    document_path VARCHAR(500),
    image_path VARCHAR(500),
    CONSTRAINT fk_assignment_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_assignment_driver FOREIGN KEY (driver_id) REFERENCES drivers(id),
    CONSTRAINT uk_assignment_order UNIQUE (order_id)
);

CREATE INDEX idx_orders_status ON orders(status);

CREATE INDEX idx_orders_created_at ON orders(created_at);

CREATE INDEX idx_orders_origin ON orders(origin);

CREATE INDEX idx_orders_destination ON orders(destination);

CREATE INDEX idx_assignments_driver ON order_assignments(driver_id);
