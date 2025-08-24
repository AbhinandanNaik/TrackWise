-- Add the new columns to the existing users table
ALTER TABLE users ADD COLUMN status VARCHAR(255);
ALTER TABLE users ADD COLUMN employee_id UUID UNIQUE;

-- Add the foreign key constraint to link to the employees table
ALTER TABLE users ADD CONSTRAINT fk_user_employee
FOREIGN KEY (employee_id) REFERENCES employees(id);