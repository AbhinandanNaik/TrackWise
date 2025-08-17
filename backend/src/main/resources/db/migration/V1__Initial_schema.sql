-- Creates the custom enum type for PostgreSQL
CREATE TYPE asset_status AS ENUM (
    'AVAILABLE',
    'ASSIGNED',
    'UNDER_MAINTENANCE',
    'RETIRED',
    'LOST'
);

-- Independent tables (no foreign keys)
CREATE TABLE departments (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE asset_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Tables with dependencies
CREATE TABLE employees (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255),
    department_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE assets (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    serial_number VARCHAR(255) NOT NULL UNIQUE,
    purchase_date DATE,
    status asset_status NOT NULL, -- Uses the custom enum type
    category_id UUID NOT NULL,
    employee_id UUID,
    warranty_expiry_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_asset_category FOREIGN KEY (category_id) REFERENCES asset_categories(id),
    CONSTRAINT fk_asset_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE warranties (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL UNIQUE,
    start_date DATE,
    end_date DATE,
    vendor VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_warranty_asset FOREIGN KEY (asset_id) REFERENCES assets(id)
);

CREATE TABLE check_in_out_logs (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    action VARCHAR(255) NOT NULL,
    check_out_time TIMESTAMP WITH TIME ZONE,
    check_in_time TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_log_asset FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT fk_log_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE maintenance_logs (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL,
    description TEXT,
    maintenance_date DATE NOT NULL,
    performed_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_maintenance_asset FOREIGN KEY (asset_id) REFERENCES assets(id)
);

CREATE TABLE iot_data (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL,
    temperature DOUBLE PRECISION,
    battery_level DOUBLE PRECISION,
    in_use BOOLEAN,
    "timestamp" TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_iot_asset FOREIGN KEY (asset_id) REFERENCES assets(id)
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    recipient_id UUID NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES employees(id)
);