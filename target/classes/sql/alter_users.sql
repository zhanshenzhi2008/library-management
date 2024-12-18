ALTER TABLE users
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

-- Update existing admin users if any
UPDATE users SET role = 'ADMIN' WHERE is_admin = TRUE;

-- Drop the old is_admin column
ALTER TABLE users DROP COLUMN is_admin; 