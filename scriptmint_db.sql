-- ============================================================
--  ScriptMint Database Setup Script
--  Run this in MySQL before starting the application
-- ============================================================

CREATE DATABASE IF NOT EXISTS scriptmint_db;
USE scriptmint_db;

CREATE TABLE IF NOT EXISTS clients (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    hourly_rate INT          NOT NULL
);

CREATE TABLE IF NOT EXISTS work_logs (
    log_id      INT PRIMARY KEY AUTO_INCREMENT,
    client_id   INT          NOT NULL,
    description VARCHAR(150) NOT NULL,
    hours       INT          NOT NULL,
    is_billed   BOOLEAN      DEFAULT FALSE,
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- ============================================================
--  Optional: Sample Data to test immediately
-- ============================================================
INSERT INTO clients(name, hourly_rate) VALUES ('Alice Johnson', 500);
INSERT INTO clients(name, hourly_rate) VALUES ('Bob Tech Ltd.',  800);

INSERT INTO work_logs(client_id, description, hours) VALUES (1, 'Logo Design',          3);
INSERT INTO work_logs(client_id, description, hours) VALUES (1, 'Landing Page UI',      5);
INSERT INTO work_logs(client_id, description, hours) VALUES (2, 'Backend API Setup',    8);
INSERT INTO work_logs(client_id, description, hours) VALUES (2, 'Database Integration', 4);

SELECT 'Database ready! Run ScriptMintApp now.' AS Status;
