-- Initialize Stemdb2 database
CREATE DATABASE IF NOT EXISTS Stemdb2;
USE Stemdb2;

-- Create a simple test table to verify database connectivity
CREATE TABLE IF NOT EXISTS health_check (
    id INT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert a test record
INSERT INTO health_check (status) VALUES ('Database initialized successfully');

-- Grant permissions to the user
GRANT ALL PRIVILEGES ON Stemdb2.* TO 'venlit'@'%';
FLUSH PRIVILEGES;