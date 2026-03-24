# Admin Service - Platform Administration (Port 8084)

## 📋 Mục Đích

Admin Service cung cấp API cho admin platform để quản trị hệ thống, theo dõi doanh thu, và quản lý người dùng.

## 🎯 Quyền Truy Cập

- ✅ Admin: Full access
- ❌ Host: No access
- ❌ Tenant: No access

## 📡 API Endpoints

### 1. Dashboard (Bảng Điều Khiển)

#### GET /api/admin/dashboard
**Lấy thống kê tổng quan hệ thống**

Response:
```json
{
  "success": true,
  "data": {
    "totalUsers": 150,
    "totalHosts": 20,
    "totalTenants": 130,
    "totalRooms": 250,
    "totalContracts": 200,
    "occupancyRate": 80,
    "totalRevenue": 500000000,
    "thisMonthRevenue": 50000000,
    "overdueInvoices": 15,
    "activeContracts": 185
  }
}
```

**Metrics:**
- `occupancyRate` - % phòng đang thuê
- `totalRevenue` - Tổng doanh thu từ khi thành lập
- `thisMonthRevenue` - Doanh thu tháng này
- `overdueInvoices` - Số hóa đơn quá hạn
- `activeContracts` - Số hợp đồng hiện hành

---

### 2. Hosts Management

#### GET /api/admin/hosts
**Lấy danh sách tất cả chủ trọ**

Response:
```json
{
  "success": true,
  "data": [
    {
      "userId": 1,
      "fullName": "Nguyễn Văn A",
      "email": "host1@example.com",
      "phoneNumber": "0912345678",
      "avatarUrl": "https://...",
      "isActive": true,
      "totalAreas": 5,
      "totalRooms": 50
    }
  ]
}
```

#### GET /api/admin/hosts/{hostId}
**Chi tiết chủ trọ**

#### PATCH /api/admin/hosts/{hostId}/toggle
**Kích hoạt/vô hiệu hóa tài khoản chủ trọ**

Purpose: Block spamming hosts hoặc hosts violated policy

---

### 3. Rooms Management

#### GET /api/admin/rooms
**Lấy danh sách tất cả phòng trong hệ thống**

Response:
```json
{
  "success": true,
  "data": [
    {
      "roomId": 1,
      "roomCode": "A101",
      "areaName": "Khu trọ Sài Gòn",
      "hostName": "Nguyễn Văn A",
      "status": "RENTED",
      "basePrice": 3000000,
      "currentTenantName": "Trần Văn B",
      "daysWithoutInvoice": 0
    }
  ]
}
```

#### GET /api/admin/rooms/missing-invoices
**Tìm phòng không có hóa đơn trong tháng**

Purpose: Kiểm soát chất lượng dữ liệu, đảm bảo tất cả hóa đơn được tạo

---

### 4. Revenue Analytics

#### GET /api/admin/revenue
**Phân tích doanh thu theo kỳ**

Query params:
- `period`: `month` (mặc định), `quarter`, `year`

Response (period=month):
```json
{
  "success": true,
  "data": {
    "totalRevenue": 50000000,
    "averageRevenue": 1666667,
    "revenueByPeriod": {
      "2026-04": 50000000,
      "2026-03": 48000000,
      "2026-02": 45000000,
      "2026-01": 42000000
    }
  }
}
```

Response (period=quarter):
```json
{
  "totalRevenue": 145000000,
  "averageRevenue": 48333333,
  "revenueByPeriod": {
    "Q1-2026": 135000000,
    "Q4-2025": 140000000,
    "Q3-2025": 130000000
  }
}
```

---

## 🏗️ Cấu Trúc Code

```
admin-service/
├── controller/
│   ├── AdminDashboardController.java
│   ├── AdminHostController.java
│   ├── AdminRoomController.java
│   └── AdminRevenueController.java
├── service/
│   ├── AdminDashboardService.java
│   ├── AdminHostService.java
│   ├── AdminRoomService.java
│   └── AdminRevenueService.java
├── dto/
│   ├── dashboard/
│   │   └── AdminDashboardDTO.java
│   ├── host/
│   │   └── AdminHostResponseDTO.java
│   ├── room/
│   │   └── AdminRoomResponseDTO.java
│   └── revenue/
│       └── AdminRevenueDTO.java
├── exception/
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
└── config/
    ├── SecurityConfig.java
    └── CorsConfig.java
```

---

## 📊 Dashboard Metrics Calculation

### Occupancy Rate
```
occupancyRate = (rentedRooms / totalRooms) * 100

Example:
- Total rooms: 250
- Rented rooms: 200
- Occupancy: (200/250)*100 = 80%
```

### Revenue Calculation
```
totalRevenue = SUM(Invoice.totalAmount where status=PAID)

thisMonthRevenue = SUM(Invoice.totalAmount 
                       where status=PAID 
                       AND billingMonth=currentMonth)
```

### Overdue Invoices
```
overdueInvoices = COUNT(Invoice 
                        where status=UNPAID 
                        AND dueDate < TODAY)
```

---

## 🔄 Admin Workflows

### Workflow 1: Monitor System Health

```
1. Admin → GET /api/admin/dashboard
   ├─ Check occupancy rate
   ├─ Monitor revenue trends
   ├─ Alert if overdue invoices > threshold
   └─ Check active contracts count

2. If issues detected:
   ├─ Drill down: GET /api/admin/rooms/missing-invoices
   ├─ Investigate specific hosts: GET /api/admin/hosts/{hostId}
   ├─ Take action: PATCH /api/admin/hosts/{hostId}/toggle
```

### Workflow 2: Block Violation Hosts

```
1. Admin detects policy violation (multiple disputes, unpaid invoices)

2. PATCH /api/admin/hosts/{hostId}/toggle
   └─ Account disabled
   └─ Tenants notified
   └─ Can't create new contracts
   └─ Existing contracts continue until end date
```

### Workflow 3: Analyze Revenue Trends

```
1. GET /api/admin/revenue?period=month
   ├─ Last 12 months data
   ├─ Identify trends
   └─ Plan business strategy

2. GET /api/admin/revenue?period=quarter
   ├─ Quarterly comparison
   └─ Identify seasonal patterns
```

---

## 🔐 Security

- ✅ JWT authentication required
- ✅ Admin-only endpoints (no tenant/host access)
- ✅ Read-only operations (no delete/modify)
- ✅ Audit logging (who accessed what)

---

## ⚠️ Error Handling

| Scenario | Status | Message |
|----------|--------|---------|
| Unauthorized (not admin) | 403 | "Access denied" |
| Host not found | 404 | "Không tìm thấy chủ trọ" |
| Invalid period param | 400 | "Period must be month/quarter/year" |

---

## 📈 Performance Considerations

- Dashboard queries should be cached (Redis)
- Revenue calculations can be pre-computed nightly
- Room list should support pagination
- Consider database views for complex aggregations

---

## 🚀 Usage Examples

### Example 1: Check Daily Dashboard

```bash
curl -X GET http://localhost:8084/api/admin/dashboard \
  -H "Authorization: Bearer <admin_token>"
```

### Example 2: Monitor Monthly Revenue

```bash
curl -X GET "http://localhost:8084/api/admin/revenue?period=month" \
  -H "Authorization: Bearer <admin_token>"
```

### Example 3: Block Violation Host

```bash
curl -X PATCH http://localhost:8084/api/admin/hosts/15/toggle \
  -H "Authorization: Bearer <admin_token>"
```

---

## 📝 TODO/Features Cần Bổ Sung

- ⭕ User activity audit log
- ⭕ Platform-wide rules & policies management
- ⭕ Automated dispute resolution
- ⭕ Payment verification
- ⭕ Host performance rating
- ⭕ Tenant complaint categories & analysis
- ⭕ Tax report generation
- ⭕ Export data to Excel/PDF
- ⭕ Real-time notifications (new complaints, high revenue, etc.)
- ⭕ Advanced analytics (heatmaps, forecasting)

---

## 🔗 Related Services

- **auth-service** (8081) - User authentication
- **host-service** (8082) - Host data source
- **tenant-service** (8083) - Tenant data source
- **data-layer** - Shared entities & repositories

