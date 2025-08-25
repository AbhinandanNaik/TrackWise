INSERT INTO departments (id, name, location, created_at, updated_at) VALUES
(gen_random_uuid(), 'Engineering', 'Bengaluru, IN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Product Management', 'Mumbai, IN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Human Resources', 'Delhi, IN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'IT Support', 'Bengaluru, IN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Finance', 'Mumbai, IN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO asset_categories (id, name, description, created_at, updated_at) VALUES
(gen_random_uuid(), 'Laptop', 'Company-issued laptops', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Monitor', 'External displays', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Keyboard', 'External keyboards', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Mouse', 'External mice', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Projector', 'Shared meeting room projectors', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Step 2: Insert employees and link them to departments
INSERT INTO employees (id, first_name, last_name, email, phone, department_id, created_at, updated_at) VALUES
(gen_random_uuid(), 'Abhinandan', 'Naik', 'abhinandannaik2486@gmail.com', '9999999999', (SELECT id FROM departments WHERE name = 'Engineering' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Priya', 'Singh', 'priya.singh@example.com', '9876543210', (SELECT id FROM departments WHERE name = 'Engineering' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Amit', 'Kumar', 'amit.kumar@example.com', '8765432109', (SELECT id FROM departments WHERE name = 'Product Management' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Sneha', 'Patel', 'sneha.patel@example.com', '7654321098', (SELECT id FROM departments WHERE name = 'Human Resources' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Rajesh', 'Verma', 'rajesh.verma@example.com', '6543210987', (SELECT id FROM departments WHERE name = 'IT Support' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Step 3: Insert users with valid BCrypt hashes and link them to employees
-- Passwords are: adminpass, priya123, amit123, sneha123, rajesh123
INSERT INTO users (username, password, role, status, employee_id) VALUES
('abhinandan_admin', '$2a$10$vykQF55DgaseRm26m9xLbeSfPO6yyZUzEL05fOEXMnrgkPvTUvVCq', 'ROLE_ADMIN', 'ACTIVE', (SELECT id FROM employees WHERE email = 'abhinandannaik2486@gmail.com')),
('priya.s', '$2a$10$A8yTz7z4HvNUSTMhiF/BYuPrrIct.iXFn00cRkU4n9tpXDzStl3KO', 'ROLE_USER', 'ACTIVE', (SELECT id FROM employees WHERE email = 'priya.singh@example.com')),
('amit.k', '$2a$10$UyTR0KUglTEQmWu2.NGeY.wlvM0Z0nXHn7.nggwDKf9knKNa7YR7G', 'ROLE_USER', 'ACTIVE', (SELECT id FROM employees WHERE email = 'amit.kumar@example.com')),
('sneha.p', '$2a$10$x2akayb7tw1BSF.iwUdDy.5IwhNbMZWrrHdq1qj90G3GtZ.msXi6i', 'ROLE_USER', 'PENDING_APPROVAL', (SELECT id FROM employees WHERE email = 'sneha.patel@example.com')),
('rajesh.v', '$2a$10$ZLeVqnSocxeUliYLxFbuceCXlAKKIJu6OBxvpki37MdkjuUmgWBTG', 'ROLE_USER', 'ACTIVE', (SELECT id FROM employees WHERE email = 'rajesh.verma@example.com'));

-- Step 4: Insert assets
INSERT INTO assets (id, name, serial_number, purchase_date, status, category_id, employee_id, created_at, updated_at) VALUES
(gen_random_uuid(), 'Dell XPS 15', 'DXPS15-A1B2C3', '2024-06-01', 'ASSIGNED', (SELECT id FROM asset_categories WHERE name = 'Laptop'), (SELECT id FROM employees WHERE email = 'priya.singh@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'MacBook Pro 16"', 'MBP16-D4E5F6', '2025-01-10', 'ASSIGNED', (SELECT id FROM asset_categories WHERE name = 'Laptop'), (SELECT id FROM employees WHERE email = 'amit.kumar@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Lenovo ThinkVision 27"', 'LTV27-G7H8I9', '2023-11-15', 'AVAILABLE', (SELECT id FROM asset_categories WHERE name = 'Monitor'), NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Logitech MX Keys', 'LOGI-MXK-J0K1L2', '2024-08-01', 'UNDER_MAINTENANCE', (SELECT id FROM asset_categories WHERE name = 'Keyboard'), NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Epson Projector', 'EPS-PROJ-M3N4O5', '2022-05-20', 'RETIRED', (SELECT id FROM asset_categories WHERE name = 'Projector'), NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Step 5: Insert data into dependent log tables
INSERT INTO warranties (id, asset_id, start_date, end_date, vendor, created_at, updated_at) VALUES
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'DXPS15-A1B2C3'), '2024-06-01', '2027-05-31', 'Dell Premium Support', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'MBP16-D4E5F6'), '2025-01-10', '2028-01-09', 'AppleCare+', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'LTV27-G7H8I9'), '2023-11-15', '2026-11-14', 'Lenovo On-site', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'LOGI-MXK-J0K1L2'), '2024-08-01', '2025-07-31', 'Logitech Support', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'EPS-PROJ-M3N4O5'), '2022-05-20', '2024-05-19', 'Epson India', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO check_in_out_logs (id, asset_id, employee_id, action, check_out_time, check_in_time, created_at, updated_at) VALUES
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'DXPS15-A1B2C3'), (SELECT id FROM employees WHERE email = 'priya.singh@example.com'), 'CHECK_OUT', '2024-06-01T10:00:00Z', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'MBP16-D4E5F6'), (SELECT id FROM employees WHERE email = 'amit.kumar@example.com'), 'CHECK_OUT', '2025-01-10T09:30:00Z', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'DXPS15-A1B2C3'), (SELECT id FROM employees WHERE email = 'priya.singh@example.com'), 'CHECK_IN', '2024-06-01T10:00:00Z', '2025-08-20T18:00:00Z', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'LTV27-G7H8I9'), (SELECT id FROM employees WHERE email = 'rajesh.verma@example.com'), 'CHECK_OUT', '2023-12-01T14:00:00Z', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'EPS-PROJ-M3N4O5'), (SELECT id FROM employees WHERE email = 'amit.kumar@example.com'), 'CHECK_OUT', '2022-06-01T10:00:00Z', '2024-05-15T17:00:00Z', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO maintenance_logs (id, asset_id, description, maintenance_date, performed_by, created_at, updated_at) VALUES
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'LOGI-MXK-J0K1L2'), 'Keyboard keys sticking. Cleaned and tested.', '2024-08-01', 'IT Support', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'DXPS15-A1B2C3'), 'Upgraded RAM from 16GB to 32GB.', '2025-07-20', 'IT Support', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'MBP16-D4E5F6'), 'Screen calibration and software update.', '2025-06-10', 'Rajesh Verma', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'EPS-PROJ-M3N4O5'), 'Replaced projector lamp.', '2024-01-05', 'Epson Service', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM assets WHERE serial_number = 'LTV27-G7H8I9'), 'Firmware update.', '2024-03-22', 'IT Support', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notifications (id, recipient_id, message, read, created_at) VALUES
(gen_random_uuid(), (SELECT id FROM employees WHERE email = 'priya.singh@example.com'), 'Your new Dell XPS 15 has been assigned to you.', false, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM employees WHERE email = 'amit.kumar@example.com'), 'Reminder: Your MacBook Pro warranty is expiring soon.', false, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM employees WHERE email = 'abhinandannaik2486@gmail.com'), 'A new user, Sneha Patel, is awaiting your approval.', false, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM employees WHERE email = 'rajesh.verma@example.com'), 'Maintenance log created for Logitech MX Keys.', true, CURRENT_TIMESTAMP),
(gen_random_uuid(), (SELECT id FROM employees WHERE email = 'priya.singh@example.com'), 'Low battery warning for asset: Dell XPS 15', true, CURRENT_TIMESTAMP);
