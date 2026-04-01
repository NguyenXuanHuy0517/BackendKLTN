USE smartroomms;
SET NAMES utf8mb4;

START TRANSACTION;

-- Clean up previous seed data from this script only.
DELETE FROM notifications
WHERE title LIKE '[SEED]%';

DELETE FROM issues
WHERE title LIKE '[SEED]%';

DELETE FROM invoices
WHERE invoice_code LIKE 'SRTEST-%';

DELETE FROM contract_services
WHERE contract_id IN (
    SELECT contract_id
    FROM contracts
    WHERE contract_code LIKE 'SRTEST-%'
);

DELETE FROM contracts
WHERE contract_code LIKE 'SRTEST-%';

DELETE FROM deposits
WHERE note LIKE '[SEED]%';

DELETE FROM room_status_history
WHERE note LIKE '[SEED]%';

DELETE FROM room_assets
WHERE room_id IN (
    SELECT room_id
    FROM rooms
    WHERE room_code LIKE 'SRTEST-%'
);

DELETE FROM equipments
WHERE serial_number LIKE 'SRTEST-%';

DELETE FROM services
WHERE service_name LIKE '[SEED]%';

DELETE FROM rooms
WHERE room_code LIKE 'SRTEST-%';

DELETE FROM motel_areas
WHERE area_name = '[SEED] Khu tro SmartRoom Demo'
  AND host_id IN (
      SELECT user_id
      FROM users
      WHERE email = 'host.seed@smartroomms.local'
  );

DELETE FROM users
WHERE email IN (
    'admin.seed@smartroomms.local',
    'host.seed@smartroomms.local',
    'tenant.active@smartroomms.local',
    'tenant.paid@smartroomms.local',
    'tenant.booking@smartroomms.local'
);

SET @seed_password_hash = '$2a$10$Hlay3eZCaZ8gN8xH3eqkEunFAnmAAzp0KSfxFqwkwKSZmF3JsayAO';

INSERT INTO users (
    role_id,
    full_name,
    phone_number,
    email,
    password_hash,
    id_card_number,
    avatar_url,
    is_active,
    created_at,
    updated_at
)
VALUES
(
    1,
    'Seed Admin Demo',
    '0909000000',
    'admin.seed@smartroomms.local',
    @seed_password_hash,
    '079001260000',
    'https://i.pravatar.cc/150?img=10',
    1,
    '2026-01-01 07:50:00',
    '2026-03-31 07:50:00'
),
(
    2,
    'Seed Host Demo',
    '0909000001',
    'host.seed@smartroomms.local',
    @seed_password_hash,
    '079001260001',
    'https://i.pravatar.cc/150?img=12',
    1,
    '2026-01-01 08:00:00',
    '2026-03-31 08:00:00'
),
(
    3,
    'Seed Tenant Active',
    '0909000002',
    'tenant.active@smartroomms.local',
    @seed_password_hash,
    '079001260002',
    'https://i.pravatar.cc/150?img=22',
    1,
    '2026-01-01 08:10:00',
    '2026-03-31 08:10:00'
),
(
    3,
    'Seed Tenant Paid',
    '0909000003',
    'tenant.paid@smartroomms.local',
    @seed_password_hash,
    '079001260003',
    'https://i.pravatar.cc/150?img=23',
    1,
    '2026-02-01 09:00:00',
    '2026-03-31 09:00:00'
),
(
    3,
    'Seed Tenant Booking',
    '0909000004',
    'tenant.booking@smartroomms.local',
    @seed_password_hash,
    '079001260004',
    'https://i.pravatar.cc/150?img=24',
    1,
    '2026-03-20 10:00:00',
    '2026-03-31 10:00:00'
);

SET @host_id = (
    SELECT user_id FROM users WHERE email = 'host.seed@smartroomms.local'
);
SET @tenant_active_id = (
    SELECT user_id FROM users WHERE email = 'tenant.active@smartroomms.local'
);
SET @tenant_paid_id = (
    SELECT user_id FROM users WHERE email = 'tenant.paid@smartroomms.local'
);
SET @tenant_booking_id = (
    SELECT user_id FROM users WHERE email = 'tenant.booking@smartroomms.local'
);

INSERT INTO motel_areas (
    host_id,
    area_name,
    address,
    ward,
    district,
    city,
    latitude,
    longitude,
    description,
    is_active,
    created_at,
    updated_at
)
VALUES (
    @host_id,
    '[SEED] Khu tro SmartRoom Demo',
    '123 Demo Street, Ward 1, District 1',
    'Ward 1',
    'District 1',
    'Ho Chi Minh City',
    10.77688900,
    106.70089700,
    '[SEED] Demo area for testing host and tenant flows.',
    1,
    '2026-01-01 08:30:00',
    '2026-03-31 08:30:00'
);

SET @area_id = (
    SELECT area_id
    FROM motel_areas
    WHERE host_id = @host_id
      AND area_name = '[SEED] Khu tro SmartRoom Demo'
    ORDER BY area_id DESC
    LIMIT 1
);

INSERT INTO rooms (
    area_id,
    room_code,
    floor,
    area_size,
    base_price,
    elec_price,
    water_price,
    status,
    amenities,
    images,
    description,
    created_at,
    updated_at
)
VALUES
(
    @area_id,
    'SRTEST-A101',
    1,
    24.00,
    3200000.00,
    3800.00,
    17000.00,
    'RENTED',
    '["WiFi","May lanh","Nuoc nong","Bep"]',
    '["https://picsum.photos/seed/srtest-a101-1/800/600","https://picsum.photos/seed/srtest-a101-2/800/600"]',
    '[SEED] Active tenant room.',
    '2026-01-01 09:00:00',
    '2026-03-31 09:00:00'
),
(
    @area_id,
    'SRTEST-A102',
    1,
    26.00,
    3500000.00,
    4000.00,
    17000.00,
    'RENTED',
    '["WiFi","May giat","Tu lanh"]',
    '["https://picsum.photos/seed/srtest-a102-1/800/600","https://picsum.photos/seed/srtest-a102-2/800/600"]',
    '[SEED] Paid tenant room for host dashboard revenue.',
    '2026-02-01 09:00:00',
    '2026-03-31 09:00:00'
),
(
    @area_id,
    'SRTEST-A103',
    2,
    22.00,
    3000000.00,
    3500.00,
    16000.00,
    'DEPOSITED',
    '["WiFi","Cua so lon"]',
    '["https://picsum.photos/seed/srtest-a103-1/800/600"]',
    '[SEED] Deposit room awaiting move-in.',
    '2026-03-20 09:00:00',
    '2026-03-31 09:00:00'
),
(
    @area_id,
    'SRTEST-A104',
    2,
    20.00,
    2800000.00,
    3500.00,
    15000.00,
    'AVAILABLE',
    '["WiFi"]',
    '["https://picsum.photos/seed/srtest-a104-1/800/600"]',
    '[SEED] Available room.',
    '2026-03-10 09:00:00',
    '2026-03-31 09:00:00'
),
(
    @area_id,
    'SRTEST-A105',
    2,
    21.00,
    2900000.00,
    3500.00,
    15000.00,
    'MAINTENANCE',
    '["WiFi","Ban cong"]',
    '["https://picsum.photos/seed/srtest-a105-1/800/600"]',
    '[SEED] Maintenance room.',
    '2026-03-15 09:00:00',
    '2026-03-31 09:00:00'
);

SET @room_a101_id = (
    SELECT room_id FROM rooms WHERE area_id = @area_id AND room_code = 'SRTEST-A101'
);
SET @room_a102_id = (
    SELECT room_id FROM rooms WHERE area_id = @area_id AND room_code = 'SRTEST-A102'
);
SET @room_a103_id = (
    SELECT room_id FROM rooms WHERE area_id = @area_id AND room_code = 'SRTEST-A103'
);
SET @room_a104_id = (
    SELECT room_id FROM rooms WHERE area_id = @area_id AND room_code = 'SRTEST-A104'
);
SET @room_a105_id = (
    SELECT room_id FROM rooms WHERE area_id = @area_id AND room_code = 'SRTEST-A105'
);

INSERT INTO room_status_history (
    room_id,
    old_status,
    new_status,
    changed_by,
    note,
    changed_at
)
VALUES
(@room_a101_id, NULL, 'RENTED', @host_id, '[SEED] Initial room status', '2026-01-01 09:05:00'),
(@room_a102_id, NULL, 'RENTED', @host_id, '[SEED] Initial room status', '2026-02-01 09:05:00'),
(@room_a103_id, NULL, 'DEPOSITED', @host_id, '[SEED] Initial room status', '2026-03-20 09:05:00'),
(@room_a104_id, NULL, 'AVAILABLE', @host_id, '[SEED] Initial room status', '2026-03-10 09:05:00'),
(@room_a105_id, NULL, 'MAINTENANCE', @host_id, '[SEED] Initial room status', '2026-03-15 09:05:00');

INSERT INTO services (
    area_id,
    service_name,
    price,
    unit_name,
    description,
    is_active,
    created_at,
    updated_at
)
VALUES
(
    @area_id,
    '[SEED] WiFi',
    100000.00,
    'Thang',
    '[SEED] Monthly WiFi service.',
    1,
    '2026-01-01 10:00:00',
    '2026-03-31 10:00:00'
),
(
    @area_id,
    '[SEED] Gui xe',
    50000.00,
    'Nguoi',
    '[SEED] Motorbike parking.',
    1,
    '2026-01-01 10:05:00',
    '2026-03-31 10:05:00'
),
(
    @area_id,
    '[SEED] Don ve sinh',
    80000.00,
    'Lan',
    '[SEED] Optional cleaning service.',
    1,
    '2026-01-01 10:10:00',
    '2026-03-31 10:10:00'
);

SET @svc_wifi_id = (
    SELECT service_id FROM services WHERE area_id = @area_id AND service_name = '[SEED] WiFi'
);
SET @svc_parking_id = (
    SELECT service_id FROM services WHERE area_id = @area_id AND service_name = '[SEED] Gui xe'
);
SET @svc_cleaning_id = (
    SELECT service_id FROM services WHERE area_id = @area_id AND service_name = '[SEED] Don ve sinh'
);

INSERT INTO deposits (
    tenant_id,
    room_id,
    amount,
    expected_check_in,
    status,
    note,
    confirmed_at,
    confirmed_by,
    deposit_date
)
VALUES
(
    @tenant_active_id,
    @room_a101_id,
    3200000.00,
    '2026-01-01',
    'COMPLETED',
    '[SEED] Completed deposit for active tenant',
    '2026-01-01 11:00:00',
    @host_id,
    '2025-12-28 17:00:00'
),
(
    @tenant_booking_id,
    @room_a103_id,
    3000000.00,
    '2026-04-05',
    'CONFIRMED',
    '[SEED] Confirmed deposit awaiting move-in',
    '2026-03-25 14:00:00',
    @host_id,
    '2026-03-24 16:00:00'
);

SET @deposit_active_id = (
    SELECT deposit_id
    FROM deposits
    WHERE note = '[SEED] Completed deposit for active tenant'
    ORDER BY deposit_id DESC
    LIMIT 1
);

INSERT INTO contracts (
    contract_code,
    room_id,
    tenant_id,
    deposit_id,
    start_date,
    end_date,
    actual_rent_price,
    elec_price_override,
    water_price_override,
    penalty_terms,
    status,
    digital_signature_url,
    created_at,
    updated_at
)
VALUES
(
    'SRTEST-HD-A101',
    @room_a101_id,
    @tenant_active_id,
    @deposit_active_id,
    '2026-01-01',
    '2026-12-31',
    3200000.00,
    3800.00,
    17000.00,
    '[SEED] 1 month deposit. 30-day notice before move-out.',
    'ACTIVE',
    'https://example.com/signatures/srtest-hd-a101.png',
    '2026-01-01 11:10:00',
    '2026-03-31 11:10:00'
),
(
    'SRTEST-HD-A102',
    @room_a102_id,
    @tenant_paid_id,
    NULL,
    '2026-02-01',
    '2026-12-31',
    3500000.00,
    4000.00,
    17000.00,
    '[SEED] Paid tenant contract for dashboard revenue.',
    'ACTIVE',
    'https://example.com/signatures/srtest-hd-a102.png',
    '2026-02-01 11:15:00',
    '2026-03-31 11:15:00'
);

SET @contract_a101_id = (
    SELECT contract_id FROM contracts WHERE contract_code = 'SRTEST-HD-A101'
);
SET @contract_a102_id = (
    SELECT contract_id FROM contracts WHERE contract_code = 'SRTEST-HD-A102'
);

INSERT INTO contract_services (
    contract_id,
    service_id,
    quantity,
    price_snapshot,
    registered_at
)
VALUES
(@contract_a101_id, @svc_wifi_id, 1, 100000.00, '2026-01-01 11:20:00'),
(@contract_a101_id, @svc_parking_id, 1, 50000.00, '2026-01-01 11:20:30'),
(@contract_a102_id, @svc_wifi_id, 1, 100000.00, '2026-02-01 11:21:00');

INSERT INTO invoices (
    contract_id,
    invoice_code,
    billing_month,
    billing_year,
    due_date,
    elec_old,
    elec_new,
    elec_price,
    water_old,
    water_new,
    water_price,
    rent_amount,
    elec_amount,
    water_amount,
    service_amount,
    total_amount,
    status,
    note,
    paid_at,
    paid_by,
    created_at,
    updated_at
)
VALUES
(
    @contract_a101_id,
    'SRTEST-INV-A101-2026-01',
    1,
    2026,
    '2026-01-05',
    120,
    180,
    3800.00,
    40,
    55,
    17000.00,
    3200000.00,
    228000.00,
    255000.00,
    150000.00,
    3833000.00,
    'PAID',
    '[SEED] Paid invoice for tenant active.',
    '2026-01-04 18:00:00',
    @host_id,
    '2026-01-01 12:00:00',
    '2026-01-04 18:00:00'
),
(
    @contract_a101_id,
    'SRTEST-INV-A101-2026-02',
    2,
    2026,
    '2026-02-05',
    180,
    250,
    3800.00,
    55,
    75,
    17000.00,
    3200000.00,
    266000.00,
    340000.00,
    150000.00,
    3956000.00,
    'OVERDUE',
    '[SEED] Overdue invoice for tenant active.',
    NULL,
    NULL,
    '2026-02-01 12:00:00',
    '2026-03-10 09:00:00'
),
(
    @contract_a101_id,
    'SRTEST-INV-A101-2026-03',
    3,
    2026,
    '2026-04-05',
    250,
    330,
    3800.00,
    75,
    93,
    17000.00,
    3200000.00,
    304000.00,
    306000.00,
    150000.00,
    3960000.00,
    'UNPAID',
    '[SEED] Current unpaid invoice for tenant active.',
    NULL,
    NULL,
    '2026-03-01 12:00:00',
    '2026-03-31 12:00:00'
),
(
    @contract_a102_id,
    'SRTEST-INV-A102-2026-02',
    2,
    2026,
    '2026-03-05',
    40,
    90,
    4000.00,
    10,
    30,
    17000.00,
    3500000.00,
    200000.00,
    340000.00,
    100000.00,
    4140000.00,
    'PAID',
    '[SEED] Previous month paid invoice for host dashboard.',
    '2026-03-01 18:10:00',
    @host_id,
    '2026-02-01 12:15:00',
    '2026-03-01 18:10:00'
),
(
    @contract_a102_id,
    'SRTEST-INV-A102-2026-03',
    3,
    2026,
    '2026-04-05',
    90,
    140,
    4000.00,
    30,
    50,
    17000.00,
    3500000.00,
    200000.00,
    340000.00,
    100000.00,
    4140000.00,
    'PAID',
    '[SEED] Current month paid invoice for host dashboard.',
    '2026-03-28 18:20:00',
    @host_id,
    '2026-03-01 12:15:00',
    '2026-03-28 18:20:00'
);

INSERT INTO issues (
    room_id,
    tenant_id,
    title,
    description,
    images,
    priority,
    status,
    handler_note,
    rating,
    tenant_feedback,
    resolved_at,
    closed_at,
    created_at,
    updated_at
)
VALUES
(
    @room_a101_id,
    @tenant_active_id,
    '[SEED] Ro ri voi nuoc',
    '[SEED] Voi nuoc trong nha tam bi ro ri tu toi qua.',
    '["https://picsum.photos/seed/srtest-issue-1/800/600"]',
    'HIGH',
    'OPEN',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    '2026-03-30 20:15:00',
    '2026-03-30 20:15:00'
),
(
    @room_a101_id,
    @tenant_active_id,
    '[SEED] May lanh kem lanh',
    '[SEED] May lanh chay yeu, can kiem tra lai.',
    '["https://picsum.photos/seed/srtest-issue-2/800/600"]',
    'MEDIUM',
    'RESOLVED',
    '[SEED] Da ve sinh dan lanh va nap them gas.',
    NULL,
    NULL,
    '2026-03-20 15:30:00',
    NULL,
    '2026-03-18 09:00:00',
    '2026-03-20 15:30:00'
),
(
    @room_a102_id,
    @tenant_paid_id,
    '[SEED] Bong den hanh lang hong',
    '[SEED] Bong den khu hanh lang truoc phong da tat.',
    '[]',
    'LOW',
    'PROCESSING',
    '[SEED] Host da len lich thay bong den vao sang mai.',
    NULL,
    NULL,
    NULL,
    NULL,
    '2026-03-29 19:45:00',
    '2026-03-31 08:45:00'
);

SET @issue_open_id = (
    SELECT issue_id
    FROM issues
    WHERE title = '[SEED] Ro ri voi nuoc'
    ORDER BY issue_id DESC
    LIMIT 1
);

SET @issue_resolved_id = (
    SELECT issue_id
    FROM issues
    WHERE title = '[SEED] May lanh kem lanh'
    ORDER BY issue_id DESC
    LIMIT 1
);

SET @invoice_march_unpaid_id = (
    SELECT invoice_id
    FROM invoices
    WHERE invoice_code = 'SRTEST-INV-A101-2026-03'
);

INSERT INTO notifications (
    user_id,
    sender_id,
    type,
    title,
    body,
    ref_type,
    ref_id,
    is_read,
    read_at,
    created_at
)
VALUES
(
    @tenant_active_id,
    @host_id,
    'INVOICE_DUE',
    '[SEED] Hoa don thang 3 can thanh toan',
    '[SEED] Vui long kiem tra hoa don thang 3 va thanh toan truoc han.',
    'INVOICE',
    @invoice_march_unpaid_id,
    0,
    NULL,
    '2026-03-31 08:00:00'
),
(
    @tenant_active_id,
    @host_id,
    'ISSUE_UPDATED',
    '[SEED] Khieu nai da duoc xu ly',
    '[SEED] Issue may lanh kem lanh da duoc host xu ly xong.',
    'ISSUE',
    @issue_resolved_id,
    0,
    NULL,
    '2026-03-20 16:00:00'
),
(
    @tenant_active_id,
    NULL,
    'CONTRACT_EXPIRING',
    '[SEED] Nhac gia han hop dong som',
    '[SEED] Chu dong lien he host neu ban muon gia han hop dong.',
    'CONTRACT',
    @contract_a101_id,
    1,
    '2026-03-25 09:00:00',
    '2026-03-24 18:00:00'
),
(
    @tenant_paid_id,
    @host_id,
    'HOST_ANNOUNCEMENT',
    '[SEED] Thong bao khu tro',
    '[SEED] Da co lich bao tri dinh ky khu hanh lang tuan nay.',
    'ROOM',
    @room_a102_id,
    0,
    NULL,
    '2026-03-30 10:00:00'
);

COMMIT;
