-- Insert Users (Admin and Tenants)
INSERT INTO user (id, email, password, user_role, username) VALUES
(1, 'admin@easypg.com', 'admin123', 'ROLE_ADMIN', 'admin'),
(2, 'john.doe@email.com', 'password123', 'ROLE_USER', 'john_doe'),
(3, 'jane.smith@email.com', 'password123', 'ROLE_USER', 'jane_smith'),
(4, 'mike.wilson@email.com', 'password123', 'ROLE_USER', 'mike_wilson'),
(5, 'sarah.brown@email.com', 'password123', 'ROLE_USER', 'sarah_brown'),
(6, 'david.jones@email.com', 'password123', 'ROLE_USER', 'david_jones');


INSERT INTO rooms (id, created_at, created_by, is_deleted, updated_at, updated_by, room_number, floor_number, capacity, rent_amount) VALUES
(101, '2024-01-01 10:00:00', 'admin', 0, '2024-01-01 10:00:00', 'admin', 'R101', 1, 2, 15000.00),
(102, '2024-01-01 10:00:00', 'admin', 0, '2024-01-01 10:00:00', 'admin', 'R102', 1, 1, 12000.00),
(103, '2024-01-01 10:00:00', 'admin', 0, '2024-01-01 10:00:00', 'admin', 'R103', 2, 2, 16000.00),
(104, '2024-01-01 10:00:00', 'admin', 0, '2024-01-01 10:00:00', 'admin', 'R104', 2, 1, 13000.00),
(105, '2024-01-01 10:00:00', 'admin', 0, '2024-01-01 10:00:00', 'admin', 'R105', 3, 2, 17000.00),
(106, '2024-01-01 10:00:00', 'admin', 0, '2024-01-01 10:00:00', 'admin', 'R106', 3, 1, 14000.00);

-- Insert Tenants (assuming you have rooms with ids 101, 102, 103, 104, 105)
INSERT INTO tenant (id, created_at, created_by, is_deleted, updated_at, updated_by, contact_number, gender, move_in_date, room_id) VALUES
(2, '2024-01-15 10:00:00', 'admin', 0, '2024-01-15 10:00:00', 'admin', '+1234567890', 'Male', '2024-01-15', 101),
(3, '2024-02-01 11:30:00', 'admin', 0, '2024-02-01 11:30:00', 'admin', '+1234567891', 'Female', '2024-02-01', 102),
(4, '2024-01-20 09:15:00', 'admin', 0, '2024-01-20 09:15:00', 'admin', '+1234567892', 'Male', '2024-01-20', 103),
(5, '2024-03-10 14:00:00', 'admin', 0, '2024-03-10 14:00:00', 'admin', '+1234567893', 'Female', '2024-03-10', 104),
(6, '2024-02-15 16:45:00', 'admin', 0, '2024-02-15 16:45:00', 'admin', '+1234567894', 'Male', '2024-02-15', 105);

-- Insert Leave Notices with different statuses
INSERT INTO leave_notices (created_at, created_by, is_deleted, updated_at, updated_by, additional_tenant_notes, move_out_date, notice_response_status, leave_reason, review_notes, settlement_id, tenant_id) VALUES
-- Pending leave notice
('2024-12-01 10:00:00', 'john_doe', 0, '2024-12-01 10:00:00', 'john_doe', 'Need to move for job relocation', '2024-12-31', 0, 'Job relocation to another city', NULL, NULL, 2),

-- Approved leave notice
('2024-11-15 14:30:00', 'jane_smith', 0, '2024-11-20 09:15:00', 'admin', 'Family emergency requires immediate move', '2024-12-15', 1, 'Family emergency', 'Approved due to emergency circumstances', NULL, 3),

-- Rejected leave notice
('2024-11-28 16:20:00', 'mike_wilson', 0, '2024-11-30 11:00:00', 'admin', 'Want to move to a different location', '2024-12-10', 2, 'Personal preference for location change', 'Rejected - insufficient notice period as per agreement', NULL, 4),

-- Another pending leave notice
('2024-12-05 12:45:00', 'sarah_brown', 0, '2024-12-05 12:45:00', 'sarah_brown', 'Completing studies and moving back home', '2025-01-15', 0, 'End of academic term', NULL, NULL, 5),

-- Approved leave notice with settlement
('2024-10-20 08:30:00', 'david_jones', 0, '2024-10-25 15:20:00', 'admin', 'Got married, moving to spouse residence', '2024-11-30', 1, 'Marriage and relocation', 'Approved with best wishes', 1, 6);

-- If you have a settlement table, uncomment and adjust the following:
-- INSERT INTO settlement (id, created_at, created_by, is_deleted, updated_at, updated_by, amount, settlement_date, settlement_status) VALUES
-- (1, '2024-10-26 10:00:00', 'admin', 0, '2024-10-26 10:00:00', 'admin', 500.00, '2024-11-30', 'COMPLETED');

-- Optional: If you don't have rooms table, create some sample rooms
-- INSERT INTO room (id, room_number, floor, capacity, rent_amount) VALUES
-- (101, '101', 1, 2, 15000),
-- (102, '102', 1, 1, 12000),
-- (103, '103', 2, 2, 16000),
-- (104, '104', 2, 1, 13000),
-- (105, '105', 3, 2, 17000);

-- Verify the data
SELECT 'Users:' as TableName;
SELECT * FROM user;

SELECT 'Tenants:' as TableName;
SELECT * FROM tenant;

SELECT 'Leave Notices:' as TableName;
SELECT * FROM leave_notices;

-- Verify relationships
SELECT 'Leave Notices with Tenant Info:' as ViewName;
SELECT 
    ln.id as notice_id,
    ln.move_out_date,
    ln.leave_reason,
    ln.notice_response_status,
    ln.tenant_id,
    u.username as tenant_name,
    u.email as tenant_email
FROM leave_notices ln
JOIN tenant t ON ln.tenant_id = t.id
JOIN user u ON t.id = u.id;




INSERT INTO leave_notices (created_at, created_by, is_deleted, updated_at, updated_by, additional_tenant_notes, move_out_date, notice_response_status, leave_reason, review_notes, settlement_id, tenant_id) VALUES

-- 1. Pending leave notice from John Doe (Male in DOUBLE_SHARING room)
('2024-12-01 10:00:00', 'john_doe', 0, '2024-12-01 10:00:00', 'john_doe', 'Need to move for job relocation. Will ensure smooth handover.', '2024-12-31', 0, 'Job relocation to another city', NULL, NULL, 2),

-- 2. Approved leave notice from Jane Smith (Female in SINGLE_SHARING room)
('2024-11-15 14:30:00', 'jane_smith', 0, '2024-11-20 09:15:00', 'admin', 'Family emergency requires immediate move. Grateful for understanding.', '2024-12-15', 1, 'Family emergency', 'Approved due to emergency circumstances. Best wishes to tenant.', NULL, 3),

-- 3. Rejected leave notice from Mike Wilson (Male in DOUBLE_SHARING room)
('2024-11-28 16:20:00', 'mike_wilson', 0, '2024-11-30 11:00:00', 'admin', 'Want to move to a different location closer to workplace.', '2024-12-10', 2, 'Personal preference for location change', 'Rejected - insufficient notice period as per rental agreement', NULL, 4),

-- 4. Another pending leave notice from Sarah Brown (Female in SINGLE_SHARING room)
('2024-12-05 12:45:00', 'sarah_brown', 0, '2024-12-05 12:45:00', 'sarah_brown', 'Completing studies and moving back home. Thank you for great experience.', '2025-01-15', 0, 'End of academic term', NULL, NULL, 5),


-- 6. Pending leave notice from Emma Davis (Female in DOUBLE_SHARING UNISEX room)
('2024-12-08 09:20:00', 'emma_davis', 0, '2024-12-08 09:20:00', 'emma_davis', 'Got a new job in different city. Need 30 days to relocate.', '2025-01-08', 0, 'Job opportunity in new city', NULL, NULL, 7);