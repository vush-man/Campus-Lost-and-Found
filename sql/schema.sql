CREATE DATABASE IF NOT EXISTS campus_lost_found;
USE campus_lost_found;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password CHAR(64) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT'
);

CREATE TABLE IF NOT EXISTS items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('LOST', 'FOUND') NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(80) NOT NULL,
    location VARCHAR(150) NOT NULL,
    event_date DATE NOT NULL,
    status ENUM('OPEN', 'CLAIMED', 'RETURNED') NOT NULL DEFAULT 'OPEN',
    reported_by INT NOT NULL,
    claimed_by INT NULL,
    claim_note VARCHAR(500) NULL,
    CONSTRAINT fk_items_reporter FOREIGN KEY (reported_by) REFERENCES users(id),
    CONSTRAINT fk_items_claimer FOREIGN KEY (claimed_by) REFERENCES users(id)
);

-- Create an admin through the app's registration flow, then run:
-- UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
