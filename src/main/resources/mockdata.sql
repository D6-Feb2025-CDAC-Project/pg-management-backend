-- Test Data Population Script for PG Management System
-- Run this after creating the schema

-- Insert Users (Admin + Tenants)
INSERT INTO `user` (`id`, `email`, `password`, `username`, `user_role`) VALUES
(1, 'admin@pgmanagement.com', 'admin123', 'admin', 'ROLE_ADMIN'),
(2, 'john.doe@email.com', 'password123', 'john_doe', 'ROLE_USER'),
(3, 'jane.smith@email.com', 'password123', 'jane_smith', 'ROLE_USER'),
(4, 'mike.wilson@email.com', 'password123', 'mike_wilson', 'ROLE_USER'),
(5, 'sarah.johnson@email.com', 'password123', 'sarah_johnson', 'ROLE_USER'),
(6, 'david.brown@email.com', 'password123', 'david_brown', 'ROLE_USER'),
(7, 'emma.davis@email.com', 'password123', 'emma_davis', 'ROLE_USER');

-- Insert Rooms
INSERT INTO `rooms` (`id`, `room_no`, `floor`, `room_type`, `tenant_type`, `max_occupancy`, `current_occupancy`, 
                     `size`, `rent_amount`, `deposit`, `electricity_charges`, `maintenance_charges`, 
                     `is_available`, `is_deleted`, `created_at`, `created_by`) VALUES
(1, '101', 'Ground Floor', 'SINGLE_SHARING', 'MALE_ONLY', 1, 1, 150.0, 8000.0, 15000.0, 500.0, 200.0, b'0', b'0', NOW(), 'admin'),
(2, '102', 'Ground Floor', 'DOUBLE_SHARING', 'FEMALE_ONLY', 2, 2, 200.0, 6500.0, 12000.0, 400.0, 150.0, b'0', b'0', NOW(), 'admin'),
(3, '201', 'First Floor', 'DOUBLE_SHARING', 'MALE_ONLY', 2, 1, 180.0, 7000.0, 13000.0, 450.0, 180.0, b'1', b'0', NOW(), 'admin'),
(4, '202', 'First Floor', 'THREE_SHARING', 'UNISEX', 3, 2, 250.0, 5500.0, 10000.0, 300.0, 120.0, b'1', b'0', NOW(), 'admin'),
(5, '301', 'Second Floor', 'FOUR_SHARING', 'MALE_ONLY', 4, 1, 300.0, 4500.0, 8000.0, 250.0, 100.0, b'1', b'0', NOW(), 'admin');

-- Insert Tenants (Fix: Remove duplicate room_id constraint issue)
INSERT INTO `tenant` (`id`, `room_id`, `move_in_date`, `contact_number`, `gender`, `is_deleted`, `created_at`, `created_by`) VALUES
(2, 1, '2024-01-15', '+91-9876543210', 'Male', b'0', NOW(), 'admin'),
(3, 2, '2024-02-01', '+91-9876543211', 'Female', b'0', NOW(), 'admin'),
(4, 3, '2024-02-01', '+91-9876543212', 'Female', b'0', NOW(), 'admin'),
(5, 4, '2024-03-10', '+91-9876543213', 'Male', b'0', NOW(), 'admin'),
(6, 5, '2024-03-15', '+91-9876543214', 'Male', b'0', NOW(), 'admin'),
(7, NULL, '2024-04-01', '+91-9876543215', 'Female', b'0', NOW(), 'admin');

-- Insert Payments (Registration and Rent payments)
INSERT INTO `payments` (`id`, `tenant_id`, `payment_type`, `payment_amount`, `payment_date`, `payment_method`, 
                        `payment_status`, `razorpay_orderid`, `razorpay_paymentid`, `razorpay_signature`, 
                        `is_deleted`, `created_at`, `created_by`) VALUES
-- Registration payments
(1, 2, 'REGISTRATION_AMOUNT', 15000.00, '2024-01-15 10:30:00', 'UPI', 'SUCCESS', 'order_reg_001', 'pay_reg_001', 'sig_reg_001', b'0', '2024-01-15 10:30:00', 'admin'),
(2, 3, 'REGISTRATION_AMOUNT', 12000.00, '2024-02-01 11:15:00', 'NETBANKING', 'SUCCESS', 'order_reg_002', 'pay_reg_002', 'sig_reg_002', b'0', '2024-02-01 11:15:00', 'admin'),
(3, 4, 'REGISTRATION_AMOUNT', 12000.00, '2024-02-01 11:45:00', 'CARD', 'SUCCESS', 'order_reg_003', 'pay_reg_003', 'sig_reg_003', b'0', '2024-02-01 11:45:00', 'admin'),
(4, 5, 'REGISTRATION_AMOUNT', 10000.00, '2024-03-10 14:20:00', 'UPI', 'SUCCESS', 'order_reg_004', 'pay_reg_004', 'sig_reg_004', b'0', '2024-03-10 14:20:00', 'admin'),
(5, 6, 'REGISTRATION_AMOUNT', 10000.00, '2024-03-15 16:30:00', 'WALLET', 'SUCCESS', 'order_reg_005', 'pay_reg_005', 'sig_reg_005', b'0', '2024-03-15 16:30:00', 'admin'),
(6, 7, 'REGISTRATION_AMOUNT', 8000.00, '2024-04-01 09:45:00', 'UPI', 'SUCCESS', 'order_reg_006', 'pay_reg_006', 'sig_reg_006', b'0', '2024-04-01 09:45:00', 'admin'),
-- Monthly rent payments
(7, 2, 'RENT', 8000.00, '2024-02-01 10:00:00', 'UPI', 'SUCCESS', 'order_rent_001', 'pay_rent_001', 'sig_rent_001', b'0', '2024-02-01 10:00:00', 'tenant'),
(8, 2, 'RENT', 8000.00, '2024-03-01 10:00:00', 'UPI', 'SUCCESS', 'order_rent_002', 'pay_rent_002', 'sig_rent_002', b'0', '2024-03-01 10:00:00', 'tenant'),
(9, 3, 'RENT', 6500.00, '2024-03-01 11:30:00', 'NETBANKING', 'SUCCESS', 'order_rent_003', 'pay_rent_003', 'sig_rent_003', b'0', '2024-03-01 11:30:00', 'tenant'),
(10, 5, 'RENT', 5500.00, '2024-04-10 12:15:00', 'UPI', 'SUCCESS', 'order_rent_004', 'pay_rent_004', 'sig_rent_004', b'0', '2024-04-10 12:15:00', 'tenant');

-- Insert Settlements (for processed leave notices) - INSERT AFTER PAYMENTS
INSERT INTO `settlements` (`id`, `payment_id`, `settlement_amount`, `deduction_amount`, `settlement_status`, 
                          `settlement_processed_date`, `razorpay_refund_id`, `additional_comments`, 
                          `is_deleted`, `created_at`, `created_by`) VALUES
(1, 1, 12000.00, 3000.00, 'REFUNDED', '2024-07-20', 'refund_001', 'Deducted for room damage and cleaning charges', b'0', '2024-07-15 14:30:00', 'admin'),
(2, 4, 8500.00, 1500.00, 'REFUND_INITIATED', NULL, 'refund_002', 'Deducted for early termination penalty', b'0', '2024-08-05 10:15:00', 'admin');

-- Insert Leave Notices (Various scenarios for testing)
INSERT INTO `leave_notices` (`id`, `tenant_id`, `settlement_id`, `move_out_date`, `leave_reason`, 
                            `notice_response_status`, `additional_tenant_notes`, `review_notes`, 
                            `is_deleted`, `created_at`, `created_by`) VALUES
-- Approved and completed leave notice
(1, 2, 1, '2024-07-31', 'Job transfer to another city', 'APPROVED', 'Need to vacate by month end', 'All formalities completed. Settlement processed.', b'0', '2024-07-01 09:30:00', 'john_doe'),

-- Pending leave notice (current active notice)
(2, 3, NULL, '2024-09-30', 'Personal reasons', 'PENDING_REVIEW', 'Family emergency requires immediate relocation', NULL, b'0', '2024-08-07 16:45:00', 'jane_smith'),

-- Under review leave notice
(3, 5, 2, '2024-08-31', 'Higher studies abroad', 'UNDER_REVIEW', 'Got admission in foreign university', 'Checking deposit settlement details', b'0', '2024-08-01 11:20:00', 'mike_wilson'),

-- Rejected leave notice (soft deleted, tenant can create new one)
(4, 4, NULL, '2024-08-15', 'Moving to different accommodation', 'REJECTED', 'Found cheaper option nearby', 'Insufficient notice period. Minimum 30 days required.', b'1', '2024-07-20 14:10:00', 'jane_smith'),

-- New active leave notice for same tenant after rejection
(5, 4, NULL, '2024-09-15', 'Moving to different accommodation', 'PENDING_REVIEW', 'Providing proper 30-day notice as per policy', NULL, b'0', '2024-08-07 10:30:00', 'jane_smith'),

-- Payment processing leave notice
(6, 6, NULL, '2024-09-10', 'Marriage and relocation', 'PAYMENT_PROCESSING', 'Getting married next month', 'Approved. Processing settlement payment.', b'0', '2024-08-05 13:45:00', 'david_brown');

-- Insert Complaints (for additional testing context)
INSERT INTO `complaints` (`id`, `tenant_id`, `title`, `issue`, `complaint_status`, `priority_level`, 
                         `action_taken`, `resolved_date`, `is_deleted`, `created_at`, `created_by`) VALUES
(1, 3, 'AC not working', 'Air conditioning in room 102 stopped working since yesterday', 'RESOLVED', 'HIGH', 'Technician called and AC repaired', '2024-08-06 15:30:00', b'0', '2024-08-05 09:15:00', 'jane_smith'),
(2, 5, 'Water leakage', 'Bathroom tap leaking continuously causing water wastage', 'IN_PROGRESS', 'MODERATE', 'Plumber scheduled for tomorrow', NULL, b'0', '2024-08-07 11:20:00', 'mike_wilson'),
(3, 7, 'Noise complaint', 'Loud music from neighboring room disturbing sleep', 'PENDING', 'LOW', NULL, NULL, b'0', '2024-08-07 22:30:00', 'emma_davis');

-- Insert Notice Board entries
INSERT INTO `notice_board` (`id`, `title`, `message`, `notice_from`, `priority_level`, 
                           `is_deleted`, `created_at`, `created_by`) VALUES
(1, 'Monthly Rent Due', 'Reminder: Monthly rent for August 2024 is due by 5th August. Please ensure timely payment to avoid late fees.', 'ADMIN', 'IMPORTANT', b'0', '2024-08-01 10:00:00', 'admin'),
(2, 'Maintenance Schedule', 'Weekly cleaning and maintenance scheduled for every Sunday 10 AM - 2 PM. Please cooperate with housekeeping staff.', 'HOUSEKEEPING', 'GENERAL', b'0', '2024-08-01 14:30:00', 'admin'),
(3, 'WiFi Upgrade', 'Internet connection will be upgraded to high-speed fiber this weekend. Brief interruption expected on Saturday 2-4 PM.', 'OWNER', 'MODERATE', b'0', '2024-08-05 16:15:00', 'admin');

-- Insert Room Facilities
INSERT INTO `facilities` (`id`, `room_id`, `name`, `category`, `is_deleted`, `created_at`, `created_by`) VALUES
(1, 1, 'Split AC', 'COOLING', b'0', NOW(), 'admin'),
(2, 1, 'Queen Size Bed', 'FURNITURE', b'0', NOW(), 'admin'),
(3, 1, 'Study Table', 'FURNITURE', b'0', NOW(), 'admin'),
(4, 1, 'Wardrobe', 'STORAGE', b'0', NOW(), 'admin'),
(5, 2, 'Ceiling Fan', 'COOLING', b'0', NOW(), 'admin'),
(6, 2, 'Twin Beds', 'FURNITURE', b'0', NOW(), 'admin'),
(7, 2, 'Study Tables', 'FURNITURE', b'0', NOW(), 'admin'),
(8, 2, 'Individual Wardrobes', 'STORAGE', b'0', NOW(), 'admin'),
(9, 3, 'Split AC', 'COOLING', b'0', NOW(), 'admin'),
(10, 3, 'Twin Beds', 'FURNITURE', b'0', NOW(), 'admin'),
(11, 4, 'Ceiling Fans', 'COOLING', b'0', NOW(), 'admin'),
(12, 4, 'Triple Beds', 'FURNITURE', b'0', NOW(), 'admin'),
(13, 5, 'Ceiling Fans', 'COOLING', b'0', NOW(), 'admin'),
(14, 5, 'Four Beds', 'FURNITURE', b'0', NOW(), 'admin');

-- Update room current_occupancy to reflect actual tenant count
UPDATE rooms SET current_occupancy = 1 WHERE id = 1; -- Tenant 2 (John)
UPDATE rooms SET current_occupancy = 1 WHERE id = 2; -- Tenant 3 (Jane) 
UPDATE rooms SET current_occupancy = 1 WHERE id = 3; -- Tenant 4 (Sarah)
UPDATE rooms SET current_occupancy = 1 WHERE id = 4; -- Tenant 5 (Mike)
UPDATE rooms SET current_occupancy = 1 WHERE id = 5; -- Tenant 6 (David)
-- Room 1: Available for more (max 1, current 1) - Full
-- Room 2: Available for more (max 2, current 1) - Available
-- Room 3: Available for more (max 2, current 1) - Available  
-- Room 4: Available for more (max 3, current 1) - Available
-- Room 5: Available for more (max 4, current 1) - Available

-- Update room availability based on current occupancy
UPDATE rooms SET is_available = (current_occupancy < max_occupancy);

COMMIT;