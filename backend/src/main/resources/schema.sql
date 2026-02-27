-- ============================================================
-- BLACK PEARL SHIPYARD MANAGEMENT SYSTEM
-- MySQL Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS blackpearl_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blackpearl_db;

-- ── Users ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS app_users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    phone        VARCHAR(20),
    department   VARCHAR(100),
    role         ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   DATETIME,
    updated_at   DATETIME
);

-- ── Tenders ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tenders (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    tender_no      VARCHAR(50) NOT NULL UNIQUE,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    category       VARCHAR(100),
    value          DECIMAL(15,2),
    published_date DATE,
    closing_date   DATE,
    status         ENUM('OPEN','CLOSING_SOON','CLOSED','AWARDED') DEFAULT 'OPEN',
    created_at     DATETIME
);

-- ── Ship Orders ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS ship_orders (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    ship_type         VARCHAR(100) NOT NULL,
    tonnage           INT,
    material          VARCHAR(100),
    specifications    TEXT,
    expected_delivery DATE,
    status            ENUM('PENDING','APPROVED','IN_PROGRESS','COMPLETED','REJECTED') DEFAULT 'PENDING',
    admin_notes       TEXT,
    created_at        DATETIME,
    updated_at        DATETIME,
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
);

-- ── Ship Repairs ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS ship_repairs (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    vessel_name      VARCHAR(150) NOT NULL,
    issue_type       VARCHAR(100),
    description      TEXT,
    priority         ENUM('LOW','MEDIUM','HIGH','EMERGENCY') DEFAULT 'MEDIUM',
    status           ENUM('PENDING','IN_PROGRESS','COMPLETED','CANCELLED') DEFAULT 'PENDING',
    technician_notes TEXT,
    created_at       DATETIME,
    updated_at       DATETIME,
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
);

-- ── Inventory ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS inventory (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_code   VARCHAR(50) NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL,
    category    VARCHAR(100),
    quantity    INT DEFAULT 0,
    unit        VARCHAR(50),
    unit_price  DECIMAL(15,2),
    status      ENUM('AVAILABLE','LOW_STOCK','OUT_OF_STOCK') DEFAULT 'AVAILABLE',
    created_at  DATETIME,
    updated_at  DATETIME
);

-- ── Stock Exports ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS stock_exports (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    inventory_id     BIGINT,
    item_name        VARCHAR(200),
    quantity         INT,
    unit             VARCHAR(50),
    purpose          TEXT,
    delivery_address TEXT,
    status           ENUM('PENDING','APPROVED','PROCESSING','DISPATCHED','REJECTED') DEFAULT 'PENDING',
    created_at       DATETIME,
    updated_at       DATETIME,
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES inventory(id) ON DELETE SET NULL
);

-- ── Notifications ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT,  -- NULL = broadcast to all
    title      VARCHAR(255) NOT NULL,
    message    TEXT NOT NULL,
    type       ENUM('INFO','SUCCESS','WARNING','ALERT') DEFAULT 'INFO',
    is_read    BOOLEAN DEFAULT FALSE,
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
);

-- ── Seed Data ─────────────────────────────────────────────
-- Default admin user (password: admin123 – BCrypt encoded)
INSERT IGNORE INTO app_users (first_name, last_name, email, password, role, active, created_at)
VALUES ('Admin', 'User', 'admin@blackpearl.com',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa',
        'ADMIN', TRUE, NOW());

-- Default regular user (password: user123 – BCrypt encoded)
INSERT IGNORE INTO app_users (first_name, last_name, email, password, department, role, active, created_at)
VALUES ('John', 'Doe', 'user@blackpearl.com',
        '$2a$10$8K1p/a0dR1xqM8K3Qe6MReE7p2OJZtP.GEIfHFcnwZ3eCwIHnHi2',
        'ENGINEERING', 'USER', TRUE, NOW());


-- Sample tenders
INSERT IGNORE INTO tenders (tender_no, title, category, value, published_date, closing_date, status, created_at)
VALUES
  ('BP-TND-2025-001', 'Supply of Marine Grade Steel Plates', 'Materials', 45.50, '2025-01-15', '2025-03-15', 'OPEN', NOW()),
  ('BP-TND-2025-002', 'Dry Dock Maintenance Services', 'Maintenance', 120.00, '2025-01-20', '2025-02-28', 'OPEN', NOW()),
  ('BP-TND-2025-003', 'Hydraulic Equipment Procurement', 'Equipment', 78.25, '2025-02-01', '2025-04-01', 'OPEN', NOW());

-- Sample inventory
INSERT IGNORE INTO inventory (item_code, name, category, quantity, unit, unit_price, status, created_at)
VALUES
  ('INV-001', 'Marine Grade Steel Plates', 'Raw Materials', 250, 'Tons', 45000.00, 'AVAILABLE', NOW()),
  ('INV-002', 'Hydraulic Cylinders 200T', 'Equipment', 12, 'Units', 185000.00, 'AVAILABLE', NOW()),
  ('INV-003', 'Anti-Corrosion Paint (Marine)', 'Consumables', 8, 'Barrels', 12500.00, 'LOW_STOCK', NOW()),
  ('INV-004', 'Propeller Shaft Bearings', 'Spare Parts', 45, 'Units', 28000.00, 'AVAILABLE', NOW()),
  ('INV-005', 'Welding Electrodes E7018', 'Consumables', 0, 'Boxes', 3500.00, 'OUT_OF_STOCK', NOW());

-- ── Payments ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    payment_ref VARCHAR(100) NOT NULL UNIQUE,
    amount      DECIMAL(15,2) NOT NULL,
    method      ENUM('NEFT','RTGS','UPI','DD','CHEQUE') NOT NULL DEFAULT 'NEFT',
    status      ENUM('PENDING','COMPLETED','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    description TEXT,
    created_at  DATETIME,
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
);

