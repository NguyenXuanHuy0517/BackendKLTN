# Host Service - Host/Landlord Management (Port 8082)

## 📋 Mục Đích

Host Service cung cấp API toàn bộ chức năng quản lý cho chủ trọ (Landlord).

## 🎯 Quyền Truy Cập

- ✅ Chủ trọ (HOST): Full access
- ❌ Người thuê (TENANT): No access (dùng tenant-service)
- ❌ Admin: Read-only access (dùng admin-service)

## 📡 API Endpoints

### 1. Areas (Khu Trọ)

#### GET /api/host/areas
**Lấy danh sách khu trọ của host**
- Authentication: Bắt buộc
- Query params: `hostId`

Response:
```json
{
  "success": true,
  "data": [
    {
      "areaId": 1,
      "areaName": "Khu trọ Sài Gòn",
      "address": "123 Nguyễn Hữu Cảnh, Q.Bình Thạnh",
      "ward": "Bình Thạnh",
      "district": "Bình Thạnh",
      "city": "TP.HCM",
      "latitude": 10.8109,
      "longitude": 106.7248,
      "description": "...",
      "totalRooms": 20,
      "occupiedRooms": 15,
      "availableRooms": 5
    }
  ]
}
```

#### POST /api/host/areas
**Tạo khu trọ mới**

Request:
```json
{
  "hostId": 1,
  "areaName": "Khu trọ Đà Nẵng",
  "address": "456 Lê Lợi, Q.Hải Châu",
  "ward": "Hải Châu",
  "district": "Hải Châu",
  "city": "Đà Nẵng",
  "latitude": 16.0544,
  "longitude": 108.2022,
  "description": "Khu trọ cao cấp..."
}
```

#### PUT /api/host/areas/{areaId}
**Cập nhật thông tin khu trọ**

#### DELETE /api/host/areas/{areaId}
**Xóa khu trọ (soft delete)**

---

### 2. Rooms (Phòng)

#### GET /api/host/rooms
**Lấy danh sách phòng của host**

Query params: `hostId`

Response:
```json
{
  "data": [
    {
      "roomId": 1,
      "roomCode": "A101",
      "floor": 1,
      "basePrice": 3000000,
      "elecPrice": 3500,
      "waterPrice": 15000,
      "areaSize": 25.5,
      "status": "RENTED",
      "amenities": "[\"WiFi\", \"AC\", \"Tủ lạnh\"]",
      "imagesList": ["url1", "url2", "url3"],
      "description": "Phòng spacious...",
      "hostName": "Nguyễn Văn A",
      "hostAvatar": "https://...",
      "currentTenantName": "Trần Văn B"
    }
  ]
}
```

#### GET /api/host/rooms/{roomId}
**Lấy chi tiết phòng + ảnh + avatar chủ trọ**

#### GET /api/host/rooms/area/{areaId}
**Lấy phòng theo khu trọ**

#### POST /api/host/rooms
**Tạo phòng mới**

Request:
```json
{
  "areaId": 1,
  "roomCode": "A102",
  "floor": 1,
  "basePrice": 3000000,
  "elecPrice": 3500,
  "waterPrice": 15000,
  "areaSize": 25.5,
  "amenities": "[\"WiFi\", \"AC\"]",
  "images": "[\"url1\", \"url2\"]",
  "description": "..."
}
```

#### PUT /api/host/rooms/{roomId}
**Cập nhật thông tin phòng**

#### PATCH /api/host/rooms/{roomId}/status
**Thay đổi trạng thái phòng**

Request:
```json
{
  "status": "MAINTENANCE"
}
```

Statuses: `AVAILABLE`, `RENTED`, `MAINTENANCE`, `DEPOSITED`

#### GET /api/host/rooms/{roomId}/history
**Lấy lịch sử thay đổi trạng thái phòng**

---

### 3. Deposits (Tiền Cọc)

#### GET /api/host/deposits
**Lấy danh sách tiền cọc của host**

Status: `PENDING`, `CONFIRMED`, `COMPLETED`, `REFUNDED`, `FORFEITED`, `EXPIRED`

#### POST /api/host/deposits
**Tạo phiếu cọc mới**

Request:
```json
{
  "tenantId": 5,
  "roomId": 1,
  "amount": 3000000,
  "expectedCheckIn": "2026-04-01",
  "note": "..."
}
```

#### PATCH /api/host/deposits/{depositId}/confirm
**Xác nhận tiền cọc**

#### PATCH /api/host/deposits/{depositId}/refund
**Hoàn trả tiền cọc**

---

### 4. Contracts (Hợp Đồng)

#### GET /api/host/contracts
**Lấy danh sách hợp đồng của host**

#### POST /api/host/contracts
**Tạo hợp đồng mới**

Request:
```json
{
  "roomId": 1,
  "tenantId": 5,
  "depositId": 1,
  "startDate": "2026-04-01",
  "endDate": "2027-04-01",
  "actualRentPrice": 3000000,
  "elecPriceOverride": 3500,
  "waterPriceOverride": 15000
}
```

#### PUT /api/host/contracts/{contractId}
**Gia hạn hợp đồng**

#### PATCH /api/host/contracts/{contractId}/terminate
**Kết thúc hợp đồng sớm**

#### POST /api/host/contracts/{contractId}/services/{serviceId}
**Thêm dịch vụ vào hợp đồng**

#### DELETE /api/host/contracts/{contractId}/services/{serviceId}
**Xóa dịch vụ khỏi hợp đồng**

---

### 5. Invoices (Hóa Đơn)

#### GET /api/host/invoices
**Lấy danh sách hóa đơn của host**

Query: `hostId`

#### GET /api/host/invoices/{invoiceId}
**Chi tiết hóa đơn**

#### GET /api/host/invoices/overdue
**Lấy hóa đơn quá hạn**

#### PUT /api/host/invoices/{invoiceId}/meters
**Cập nhật số điện/nước**

Request:
```json
{
  "elecNew": 1500,
  "waterNew": 450
}
```

#### PATCH /api/host/invoices/{invoiceId}/pay
**Xác nhận thanh toán**

Query: `paidById` (User ID của người xác nhận)

---

### 6. Services (Dịch Vụ)

#### GET /api/host/services
**Lấy dịch vụ theo khu trọ**

Query: `areaId`

#### POST /api/host/services
**Tạo dịch vụ mới**

Request:
```json
{
  "areaId": 1,
  "serviceName": "Dọn vệ sinh",
  "price": 100000,
  "unitName": "lần/tháng",
  "description": "..."
}
```

#### PUT /api/host/services/{serviceId}
**Cập nhật dịch vụ**

#### DELETE /api/host/services/{serviceId}
**Xóa dịch vụ (soft delete)**

---

### 7. Tenants (Người Thuê)

#### GET /api/host/tenants
**Lấy danh sách người thuê của host**

Query: `hostId`

#### POST /api/host/tenants
**Thêm người thuê mới**

#### GET /api/host/tenants/{tenantId}
**Chi tiết người thuê**

#### PATCH /api/host/tenants/{tenantId}/toggle
**Kích hoạt/vô hiệu hóa tài khoản người thuê**

---

### 8. Issues (Khiếu Nại)

#### GET /api/host/issues
**Lấy khiếu nại trong phòng của host**

Query: `hostId`

#### GET /api/host/issues/{issueId}
**Chi tiết khiếu nại**

#### PATCH /api/host/issues/{issueId}/status
**Cập nhật trạng thái khiếu nại**

Request:
```json
{
  "status": "PROCESSING"
}
```

Statuses: `OPEN`, `PROCESSING`, `RESOLVED`, `CLOSED`

---

### 9. Reports (Báo Cáo)

#### GET /api/host/reports/dashboard
**Báo cáo tổng hợp**

Response:
```json
{
  "data": {
    "totalRevenue": 15000000,
    "thisMonthRevenue": 1500000,
    "totalRooms": 20,
    "occupiedRooms": 15,
    "occupancyRate": 75,
    "overdueInvoices": 3,
    "activeContracts": 15
  }
}
```

---

### 10. Notifications

#### GET /api/host/notifications
**Lấy thông báo của host**

Query: `userId`

#### PATCH /api/host/notifications/{notificationId}/read
**Đánh dấu thông báo đã đọc**

#### PATCH /api/host/notifications/mark-all-read
**Đánh dấu tất cả thông báo đã đọc**

Query: `userId`

---

### 11. File Upload

#### POST /api/host/upload/image
**Upload ảnh lên Cloudinary**

```bash
curl -X POST http://localhost:8082/api/host/upload/image \
  -H "Authorization: Bearer <token>" \
  -F "file=@room_photo.jpg"
```

Response:
```json
{
  "success": true,
  "data": "https://res.cloudinary.com/..."
}
```

---

## 🏗️ Cấu Trúc Code

```
host-service/
├── controller/
│   ├── AreaController.java
│   ├── RoomController.java
│   ├── ContractController.java
│   ├── InvoiceController.java
│   ├── TenantController.java
│   ├── ServiceController.java
│   ├── IssueController.java
│   ├── EquipmentController.java
│   ├── DepositController.java
│   ├── ReportController.java
│   ├── NotificationController.java
│   └── FileUploadController.java
├── service/
│   ├── AreaService.java
│   ├── RoomService.java
│   ├── ContractManagementService.java
│   ├── BillingService.java
│   ├── TenantService.java
│   ├── ServiceManagementService.java
│   ├── IssueService.java
│   ├── EquipmentService.java
│   ├── NotificationService.java
│   ├── FileUploadService.java
│   ├── EmailService.java
│   └── ReportService.java
├── scheduler/
│   ├── InvoiceScheduler.java         # Auto-create invoices on 1st of month
│   ├── NotificationScheduler.java    # Remind overdue invoices
│   ├── ReportScheduler.java          # Send monthly reports
│   └── ContractExpiryScheduler.java  # Auto-expire contracts
├── mapper/
│   ├── AreaMapper.java
│   ├── RoomMapper.java
│   ├── ContractMapper.java
│   ├── InvoiceMapper.java
│   └── ... (tất cả mappers)
├── dto/
│   ├── area/
│   ├── room/
│   ├── contract/
│   ├── invoice/
│   ├── tenant/
│   ├── service/
│   ├── equipment/
│   ├── notification/
│   └── report/
├── exception/
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
└── config/
    ├── SecurityConfig.java
    ├── CorsConfig.java
    ├── CloudinaryConfig.java
    └── JwtAuthFilter.java
```

## 🔄 Business Logic Flows

### Flow: Tạo Hợp Đồng Thuê

```
1. POST /api/host/contracts
   ├─ Validate room exists & is AVAILABLE
   ├─ Validate tenant exists
   ├─ Create Contract (status=ACTIVE)
   ├─ Update Room status → RENTED
   ├─ Auto-create first Invoice (DRAFT)
   └─ Send notification to tenant

2. Host cập nhật số điện/nước
   ├─ PUT /api/host/invoices/{id}/meters
   ├─ Calculate amounts
   └─ Update Invoice status → UNPAID

3. Scheduler kiểm tra hóa đơn quá hạn
   ├─ Daily check overdue invoices
   ├─ Update status → OVERDUE
   └─ Send reminder notification
```

### Flow: Xử Lý Khiếu Nại

```
1. Tenant tạo issue
   ├─ POST /api/tenant/issues
   ├─ Upload ảnh (Cloudinary)
   ├─ Create Issue (OPEN)
   └─ Notify Host

2. Host xem & xử lý
   ├─ GET /api/host/issues
   ├─ PATCH status → PROCESSING
   └─ Notify tenant

3. Issue resolved
   ├─ PATCH status → RESOLVED/CLOSED
   ├─ Tenant rate issue
   └─ Close issue
```

---

## 🔐 Security

- ✅ JWT authentication required
- ✅ Host chỉ thấy dữ liệu của chính mình
- ✅ Cloudinary image upload với validation
- ✅ Password hashing (BCrypt)

---

## 📝 TODO/Features Cần Bổ Sung

- ⭕ Email notifications (overdue invoices, contract expiry)
- ⭕ SMS notifications
- ⭕ Firebase Cloud Messaging (push notifications)
- ⭕ Monthly report PDF export
- ⭕ Payment gateway integration
- ⭕ Automated invoice generation (cron job)
- ⭕ Equipment maintenance scheduling
- ⭕ Tenant screening/rating system

