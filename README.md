# SmartRoom Motel Management Platform - Backend Documentation

## 📋 Tổng Quan Hệ Thống

SmartRoom là một platform quản lý ký túc xá/nhà trọ toàn diện, cung cấp các giải pháp cho chủ trọ (Host) và người thuê (Tenant).

### 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────────────────────────────────────────┐
│          SmartRoom Backend - Multi Microservice      │
└─────────────────────────────────────────────────────┘
         ↓
    ┌────────────────────────────────────────────┐
    │         Backend Application (Orchestrator) │
    │         Port: Run all 4 services          │
    └────────────────────────────────────────────┘
         ↓           ↓           ↓           ↓
    ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
    │  Auth    │ │  Host    │ │ Tenant   │ │  Admin   │
    │ Service  │ │ Service  │ │ Service  │ │ Service  │
    │ :8081    │ │ :8082    │ │ :8083    │ │ :8084    │
    └──────────┘ └──────────┘ └──────────┘ └──────────┘
         ↓           ↓           ↓           ↓
    ┌────────────────────────────────────────────────┐
    │  Data Layer (Shared Entities & Repositories)   │
    └────────────────────────────────────────────────┘
         ↓
    ┌────────────────────────────────────────────────┐
    │     MariaDB Database: smartroomms               │
    └────────────────────────────────────────────────┘
```

## 📦 Module Structure

### 1. **data-layer** (Shared Library)
- **Mục đích:** Định nghĩa tất cả Entity, Repository, DTO chung
- **Nội dung:**
  - JPA Entities: User, Role, Room, Contract, Invoice, etc.
  - Spring Data Repositories
  - Shared DTOs (ApiResponse, etc.)
  - Enums (RoomStatus, InvoiceStatus, etc.)

### 2. **auth-service** (Port 8081)
- **Mục đích:** Xác thực & Cấp phát JWT Token
- **Chức năng chính:**
  - Đăng nhập (Login)
  - Đăng ký (Register cho Tenant)
  - Gia hạn Token (Refresh Token)
  - Quên mật khẩu (Forgot Password)
  - Đặt lại mật khẩu (Reset Password)
  - Đăng xuất (Logout)
- **Security:**
  - JWT HS256 (256-bit Base64 encoded key)
  - Password: BCrypt hashing
  - Token expiration: 24 hours

### 3. **host-service** (Port 8082)
- **Mục đích:** Toàn bộ nghiệp vụ cho chủ trọ
- **Các Controller & Chức năng:**
  - **Areas** - Quản lý khu trọ
  - **Rooms** - Quản lý phòng (status, images, details, amenities)
  - **Deposits** - Quản lý tiền cọc
  - **Contracts** - Quản lý hợp đồng thuê
  - **Invoices** - Quản lý hóa đơn/tiền điện nước
  - **Services** - Quản lý dịch vụ phòng
  - **Equipment** - Quản lý tài sản phòng
  - **Tenants** - Quản lý người thuê
  - **Issues** - Quản lý khiếu nại/sự cố
  - **Reports** - Báo cáo doanh thu, thống kê
  - **Notifications** - Thông báo cho chủ trọ
  - **FileUpload** - Upload ảnh via Cloudinary

### 4. **tenant-service** (Port 8083)
- **Mục đích:** API riêng cho người thuê
- **Các Controller & Chức năng:**
  - **Profile** - Quản lý hồ sơ cá nhân
  - **My Rooms** - Xem phòng hiện tại & hợp đồng
  - **My Invoices** - Xem hóa đơn & lịch sử thanh toán
  - **My Issues** - Tạo & theo dõi khiếu nại
  - **Notifications** - Nhận thông báo
  - **FCM Token** - Đăng ký push notifications
  - **Room Search** - Tìm kiếm phòng trống (Chatbot)

### 5. **admin-service** (Port 8084)
- **Mục đích:** Quản trị platform
- **Các Controller & Chức năng:**
  - **Dashboard** - Thống kê tổng quan
  - **Hosts** - Quản lý chủ trọ (active/inactive)
  - **Rooms** - Xem tất cả phòng, kiểm soát chất lượng
  - **Revenue** - Thống kê doanh thu theo kỳ

## 🔐 Authentication Flow

```
1. User POST /api/auth/login (email, password)
   ↓
2. Auth-Service xác thực mật khẩu
   ↓
3. Trả về JWT token (eyJhbGc...)
   ↓
4. Client gửi token trong header: Authorization: Bearer <token>
   ↓
5. Host-Service/Tenant-Service xác minh JWT signature
   ↓
6. Cho phép truy cập nếu token hợp lệ
```

## 📊 Database Schema

### Main Entities:
- **Users** - Người dùng (ADMIN, HOST, TENANT)
- **Roles** - Vai trò hệ thống
- **MotelAreas** - Khu trọ của chủ trọ
- **Rooms** - Phòng trong khu trọ
- **Contracts** - Hợp đồng thuê phòng
- **Invoices** - Hóa đơn điện nước/dịch vụ
- **Deposits** - Tiền cọc
- **Issues** - Khiếu nại/sự cố
- **Notifications** - Thông báo cho người dùng
- **Services** - Dịch vụ phòng (nước, điện, vệ sinh, etc.)
- **Equipment** - Tài sản phòng (tủ lạnh, máy giặt, etc.)

## 🔄 Luồng Xử Lý Chính

### 1. **Luồng Đăng Ký Người Thuê**
```
Tenant → POST /api/auth/register
  → AuthService: Create User with TENANT role
  → Save to DB
  → Return userId + token
```

### 2. **Luồng Tạo Hợp Đồng Thuê**
```
Host → POST /api/host/contracts
  → Chọn phòng, người thuê, thời hạn
  → ContractManagementService: Validate & Create
  → Auto create first Invoice (DRAFT)
  → Phòng → RENTED status
  → Return contract details
```

### 3. **Luồng Tạo Hóa Đơn Tháng**
```
Daily Scheduler (00:00 ngày 1 hàng tháng)
  → InvoiceScheduler: Duyệt tất cả ACTIVE contracts
  → Create Invoice DRAFT với meter readings cũ
  → Host nhập số điện/nước mới
  → PUT /api/host/invoices/{id}/meters
  → Invoice → UNPAID
  → Scheduler gửi email nhắc nhở
```

### 4. **Luồng Khiếu Nại**
```
Tenant → POST /api/tenant/issues (kèm ảnh)
  → FileUploadService: Upload ảnh to Cloudinary
  → Create Issue (OPEN)
  → Notify Host via Notification
  → Host: PATCH /api/host/issues/{id}/status
  → Issue → PROCESSING/RESOLVED/CLOSED
  → Tenant rate issue
```

## 🚀 Cách Chạy

### Option 1: Run tất cả services qua BackendApplication
```bash
cd Backend
mvn clean install -DskipTests=true
mvn spring-boot:run
```

### Option 2: Run từng service riêng
```bash
# Terminal 1: Auth Service
cd Backend/auth-service
mvn spring-boot:run

# Terminal 2: Host Service
cd Backend/host-service
mvn spring-boot:run

# Terminal 3: Tenant Service
cd Backend/tenant-service
mvn spring-boot:run

# Terminal 4: Admin Service
cd Backend/admin-service
mvn spring-boot:run
```

## 🔧 Cấu Hình

### JWT Configuration
- **Secret Key:** `PFcsHTkldAnWhsoeeH/fHoKgext6EhZMp1sJYAnHqRA=` (256-bit Base64)
- **Algorithm:** HS256
- **Expiration:** 86400000 ms (24 hours)

### Cloudinary Configuration
- **Cloud Name:** `dbj3kf54f`
- **API Key:** `161871263642373`
- **API Secret:** `5qNcCOZlfiGL2hL9M6XRZ61JLQY`

### Database
- **URL:** `jdbc:mariadb://localhost:3306/smartroomms`
- **User:** `root`
- **Password:** (empty)

## 📚 API Documentation

Chi tiết API của từng module xem trong:
- `data-layer/README.md`
- `auth-service/README.md`
- `host-service/README.md`
- `tenant-service/README.md`
- `admin-service/README.md`

## ✅ Features Hoàn Thành

- ✅ JWT Authentication (Login, Register, Refresh, Logout)
- ✅ Host Management (Areas, Rooms, Deposits, Contracts, Invoices)
- ✅ Tenant Management (Profile, Invoices, Issues, Room Search)
- ✅ File Upload (Cloudinary Integration)
- ✅ Notifications (Database Storage)
- ✅ Admin Dashboard (Statistics)
- ✅ CORS Configuration
- ✅ Exception Handling
- ✅ Logging (Log4j2)

## 🔜 Features Cần Bổ Sung

- ⭕ Firebase Cloud Messaging (Push Notifications)
- ⭕ Email Service (Password Reset, Invoices)
- ⭕ Chatbot Integration (Gemini AI)
- ⭕ Payment Gateway Integration
- ⭕ Report Export (PDF/Excel)
- ⭕ Advanced Analytics

## 🐛 Known Issues & Fixes

### JWT Signature Mismatch (FIXED ✅)
- **Issue:** Different secret keys between services
- **Fix:** Synchronized 256-bit BASE64 key across all services

### Insufficient Key Length (FIXED ✅)
- **Issue:** Secret key < 256 bits
- **Fix:** Changed from UTF-8 to BASE64 decoding

## 📞 Support & Documentation

Để xem chi tiết từng module, tham khảo:
- `data-layer/README.md` - Entity models & database schema
- `auth-service/README.md` - Authentication endpoints
- `host-service/README.md` - Host business logic APIs
- `tenant-service/README.md` - Tenant-specific APIs
- `admin-service/README.md` - Admin dashboard APIs

