CREATE DATABASE IF NOT EXISTS usbcommanderdb;

USE usbcommanderdb;

CREATE TABLE permissions (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(24) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE roles (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(24) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE role_permissions (
    perm_id BINARY(16) NOT NULL,
    role_id BINARY(16) NOT NULL,
    PRIMARY KEY (perm_id, role_id),
    FOREIGN KEY (perm_id) REFERENCES permissions(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    password VARCHAR(255) NOT NULL,
    disable BOOLEAN NOT NULL DEFAULT FALSE,
    role_id BINARY(16),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE sessions(
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    selector CHAR(36) NOT NULL,
    token CHAR(64) NOT NULL UNIQUE,
    blacklisted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE machine (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    ip VARCHAR(16) NOT NULL UNIQUE,
    reg_dt DATETIME NOT NULL,
    description VARCHAR(255),
    disable BOOLEAN NOT NULL DEFAULT FALSE,
    log_frec BIGINT UNSIGNED NOT NULL
);


CREATE TABLE log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id BINARY(16) NOT NULL,
    receive_date DATETIME NOT NULL,
    usb_value TINYINT NOT NULL,
    usb_allowed BOOLEAN NOT NULL,
    usb_list TEXT,
    log_code INT,
    needs_rev BOOLEAN NOT NULL,
    creation_date DATETIME NOT NULL,
    CONSTRAINT unique_creation_date_machine UNIQUE (creation_date, machine_id),
    FOREIGN KEY (machine_id) REFERENCES machine(id)
    
);

CREATE TABLE error_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id BINARY(16) NOT NULL,
    receive_date DATETIME NOT NULL,
    creation_date DATETIME NOT NULL,
    message TEXT,
    CONSTRAINT unique_error_creation_date_machine UNIQUE (creation_date, machine_id),
    FOREIGN KEY (machine_id) REFERENCES machine(id)
);

INSERT INTO permissions (id, name, description) VALUES
    (UNHEX(REPLACE(UUID(), '-', '')), 'USER_MANAGEMENT', 'Allows to create, edit, and delete users'),
    (UNHEX(REPLACE(UUID(), '-', '')), 'SOLVE_LOGS', 'Allows to mark warning and priority logs as resolved'),
    (UNHEX(REPLACE(UUID(), '-', '')), 'VIEW_LOGS', 'Allows to see logs from all machines'),
    (UNHEX(REPLACE(UUID(), '-', '')), 'MANAGE_ROLES', 'Allows the creation of other roles'),
    (UNHEX(REPLACE(UUID(), '-', '')), 'MACHINE_MANAGEMENT', 'Allows to modify the machines being monitored');

INSERT INTO roles (id, name, description) VALUES
    (UNHEX(REPLACE(UUID(), '-', '')), 'ADMIN', "Administrator role with all permissions");

INSERT INTO role_permissions (perm_id, role_id)
    SELECT p.id, r.id FROM permissions p, roles r WHERE r.name = 'ADMIN' AND p.name IN ('USER_MANAGEMENT', 'SOLVE_LOGS', 'VIEW_LOGS', 'MANAGE_ROLES', 'MACHINE_MANAGEMENT');
