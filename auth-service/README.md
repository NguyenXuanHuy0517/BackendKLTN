- ⭕ Email service integration (send password reset link)
- ⭕ Redis integration (replace in-memory token blacklist)
- ⭕ OAuth2 support (Google, Facebook login)
- ⭕ Multi-factor authentication (2FA)
- ⭕ API key authentication (for mobile apps)
- ⭕ Role-based access control (RBAC) enforcement
## ✨ Features

- ✅ **JWT Token Authentication** (HS256)
- ✅ **Role-based Access** (ADMIN, HOST, TENANT)
- ✅ **Separate Register Endpoints** (for TENANT & HOST)
- ✅ **Token Refresh** (without re-login)
- ✅ **Token Blacklist** (logout functionality)
- ✅ **Password Reset Flow** (with email support)
- ✅ **Email Notifications** (optional - configurable)
- ✅ **Global Exception Handling**
- ✅ **CORS Configuration**

## 🚀 Quick Start

### 1. Configuration
```properties
server.port=8081
jwt.secret=PFcsHTkldAnWhsoeeH/fHoKgext6EhZMp1sJYAnHqRA=
jwt.expiration=86400000
email.send-reset-password=false  # Set true if email configured
```

### 2. Register & Login
```bash
# Register as TENANT
curl -X POST http://localhost:8081/api/auth/register/tenant \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyễn Văn A",
    "email": "tenant@example.com",
    "password": "password123",
    "phoneNumber": "0912345678",
    "idCardNumber": "012345678901"
  }'

# Register as HOST
curl -X POST http://localhost:8081/api/auth/register/host \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Trần Văn B",
    "email": "host@example.com",
    "password": "password123",
    "phoneNumber": "0987654321",
    "idCardNumber": "123456789012"
  }'

# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "tenant@example.com",
    "password": "password123"
  }'
```

### 3. Use Token in Requests
```bash
curl -X GET http://localhost:8082/api/host/areas \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 📝 TODO/Features Cần Bổ Sung
## 🚀 Cách Sử Dụng Token
### 7. GET /api/auth/debug/secret (Debug only)
### 6. POST /api/auth/reset-password
- Gửi email với reset token (TODO: Email service)
### 5. POST /api/auth/forgot-password
### 4. POST /api/auth/logout
### 3. POST /api/auth/refresh-token
**Validation:**
- Email: Unique, valid format
- PhoneNumber: Unique, 10-11 digits
- Password: Min 8 characters
- IdCardNumber: Unique, 12 digits
**Response (Success):**
### 2. POST /api/auth/register
**Mục đích:** Tạo tài khoản mới (TENANT)
# Auth Service - Authentication & Authorization (Port 8081)

## 📋 Mục Đích

Auth Service cung cấp tất cả chức năng xác thực & phát hành JWT token cho hệ thống.


### JWT Token Structure
```
Header:    { "alg": "HS256", "typ": "JWT" }
Payload:   { "sub": "user@email.com", "iat": 1234567890, "exp": 1234654290 }
Signature: HMACSHA256(Header + Payload, secret_key)
```

### Secret Key Configuration
- **Algorithm:** HS256
- **Key:** `PFcsHTkldAnWhsoeeH/fHoKgext6EhZMp1sJYAnHqRA=` (256-bit Base64)
- **Expiration:** 86400000 ms (24 hours)

## 📡 API Endpoints

### 1. POST /api/auth/login
**Mục đích:** Đăng nhập với email & mật khẩu

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "userId": 1,
    "fullName": "Nguyễn Văn A",
    "email": "user@example.com",
    "role": "HOST",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWI..."
### 2. POST /api/auth/register/tenant
**Mục đích:** Tạo tài khoản TENANT mới
```

**Response (Failure):**
```json
{
  "success": false,
  "message": "Email không tồn tại",
  "data": null
}
```

**Status Codes:**
**Response:**
```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "data": null
}
```

---

### 3. POST /api/auth/register/host
**Mục đích:** Tạo tài khoản HOST mới (Quản lý motel/phòng trọ)

**Request:**
```json
{
  "fullName": "Trần Văn C",
  "email": "host@example.com",
  "password": "securepass123",
  "phoneNumber": "0987654321",
  "idCardNumber": "123456789012"
}
```

**Response:**
- 400 - Email/password không hợp lệ
- 401 - Tài khoản bị khóa
- 404 - Không tìm thấy người dùng

---

### 2. POST /api/auth/register
**Mục đích:** Tạo tài khoản mới (TENANT)
---

### 4. POST /api/auth/register (Backward Compatibility)
**Mục đích:** Tạo tài khoản (mặc định TENANT)

**Note:** Endpoint này dùng endpoint `/api/auth/register/tenant` để đảm bảo backward compatibility
  "email": "tenant@example.com",
  "password": "securepass123",
  "phoneNumber": "0912345678",
### 5. POST /api/auth/refresh-token
### 5. POST /api/auth/refresh-token
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "data": null
}
```

**Validation:**
- Email: Unique, valid format
- PhoneNumber: Unique, 10-11 digits
- Password: Min 8 characters
- IdCardNumber: Unique, 12 digits

---

### 3. POST /api/auth/refresh-token
**Mục đích:** Gia hạn JWT token mà không cần đăng nhập lại

**Request:**
```json
{
### 6. POST /api/auth/logout
}
```

**Response:**
```json
{
  "success": true,
  "message": "Gia hạn token thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJuZXd..."
  }
}
```

**Notes:**
- Token phải còn hiệu lực
- Token đã bị logout không thể gia hạn

---

### 4. POST /api/auth/logout
**Mục đích:** Hủy token hiện tại
### 7. POST /api/auth/forgot-password
**Request:**
```
Header: Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Đăng xuất thành công",
  "data": null
}
```

**Implementation Note:**
- Token được thêm vào blacklist in-memory
- Production: Sử dụng Redis thay vì memory

---
- Gửi email với reset token (nếu email được cấu hình)
### 5. POST /api/auth/forgot-password
- Nếu email không được cấu hình, token sẽ được in ra logs
**Mục đích:** Yêu cầu reset mật khẩu

**Request:**
### 8. POST /api/auth/reset-password
{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Email reset mật khẩu đã được gửi",
  "data": null
}
```

**Notes:**
- Gửi email với reset token (TODO: Email service)
- Token có hiệu lực 24 giờ

---

### 6. POST /api/auth/reset-password
### 9. GET /api/auth/debug/secret (Debug only)

**Request:**
```json
{
  "token": "reset-token-from-email",
  "newPassword": "newpassword123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Mật khẩu đã được thay đổi",
  "data": null
}
```

---

### 7. GET /api/auth/debug/secret (Debug only)
**Mục đích:** Kiểm tra secret key đang sử dụng

**Response:**
```json
"AUTH secret: [PFcsHTkldAnWhsoeeH/fHoKgext6EhZMp1sJYAnHqRA=] length=44"
```

---

## 🏗️ Cấu Trúc Code

```
auth-service/
├── controller/
│   └── AuthController.java           # API endpoints
├── service/
│   └── AuthService.java              # Business logic
├── security/
│   ├── JwtUtils.java                 # Token generation/validation
│   ├── JwtAuthFilter.java            # JWT extraction from header
│   └── UserDetailsServiceImpl.java    # Load user for authentication
├── dto/
│   ├── LoginRequestDTO.java
│   ├── LoginResponseDTO.java
│   ├── RegisterRequestDTO.java
│   ├── RefreshTokenRequestDTO.java
│   ├── RefreshTokenResponseDTO.java
│   ├── ForgotPasswordRequestDTO.java
│   └── ResetPasswordRequestDTO.java
├── exception/
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── config/
│   ├── SecurityConfig.java           # Spring Security configuration
│   └── CorsConfig.java               # CORS setup
└── application.properties            # Configuration
```

## 🔐 Security Implementation

### Password Hashing
```java
// Login verification
boolean matches = passwordEncoder.matches(
    request.getPassword(), 
    user.getPasswordHash()  // BCrypt hashed
);
```

### JWT Generation
```java
// Create token
String token = Jwts.builder()
    .setSubject(userDetails.getUsername())  // email
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
    .signWith(getSignKey())
    .compact();
```

### JWT Validation
```java
// Verify signature
Jwts.parserBuilder()
    .setSigningKey(getSignKey())  // Same BASE64 decoded key
    .build()
    .parseClaimsJws(token)
    .getBody();
```

## 🔄 Authentication Flow Diagram

```
┌──────────────┐
│   Client     │
└──────┬───────┘
       │
       │ 1. POST /api/auth/login
       │    { email, password }
       ↓
┌──────────────────────────┐
│  AuthController.login()  │
└──────┬───────────────────┘
       │
       │ 2. Call AuthService.login()
       ↓
┌──────────────────────────┐
│  AuthService.login()     │
│  - Find user by email    │
│  - Verify password       │
│  - Generate JWT token    │
└──────┬───────────────────┘
       │
       │ 3. Return token
       ↓
┌──────────────┐
│   Client     │
│  Receives:   │
│  - userId    │
│  - token     │
└──────┬───────┘
       │
       │ 4. Store token locally
       │
       │ 5. GET /api/host/areas
       │    Header: Authorization: Bearer <token>
       ↓
┌────────────────────────────┐
│ HostService /api/host/areas│
│ - JwtAuthFilter extracts   │
│   token from header        │
│ - JwtUtils.validateToken() │
│   validates signature      │
│ - Set SecurityContext      │
└────────────────────────────┘
       │
       │ 6. Processing request
       ↓
┌──────────────┐
# Server
server.port=8081

│   Client     │
│  Receives:   │
│  - areas []  │
└──────────────┘
```

## ⚠️ Error Handling
spring.datasource.password=root
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# Email Configuration (Optional - for password reset emails)
# Nếu không cấu hình, hệ thống vẫn hoạt động bình thường
# Reset token sẽ được in ra logs để test
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Email Feature Flag
email.send-reset-password=false  # Set to true nếu đã cấu hình email

# Frontend URL (dùng cho email reset password link)
app.frontend.url=http://localhost:3000

# Logging
logging.level.com.project=DEBUG
logging.level.org.springframework.security=DEBUG

| Scenario | Status | Message |
### Email Setup Guide (Gmail SMTP)

**Step 1: Tạo App Password cho Gmail**
1. Đăng nhập vào https://myaccount.google.com
2. Security → App passwords
3. Chọn Mail + Windows Computer
4. Copy password → Paste vào `spring.mail.password`

**Step 2: Enable Less Secure App Access (nếu cần)**
- https://myaccount.google.com/security → Less secure app access

**Step 3: Test Email Send**
```bash
POST http://localhost:8081/api/auth/forgot-password
{
  "email": "your-test-account@gmail.com"
}
```

Kiểm tra logs để xem email có được gửi không:
- `✅ Password reset email sent to: your-test-account@gmail.com`
- `❌ EmailService not available` → Email chưa được cấu hình

---
| Email không tồn tại | 404 | "Email không tồn tại" |
| Mật khẩu sai | 401 | "Mật khẩu không đúng" |
| Tài khoản bị khóa | 401 | "Tài khoản đã bị khóa" |
| Email đã tồn tại | 400 | "Email đã tồn tại" |
| Token không hợp lệ | 401 | "Token không hợp lệ" |
| Token hết hạn | 401 | "Token hết hạn" |

## 🔧 Configuration

**application.properties:**
```properties
# JWT
jwt.secret=PFcsHTkldAnWhsoeeH/fHoKgext6EhZMp1sJYAnHqRA=
jwt.expiration=86400000

# Database
spring.datasource.url=jdbc:mariadb://localhost:3306/smartroomms
spring.datasource.username=root
```

## 🚀 Cách Sử Dụng Token

### Step 1: Đăng nhập
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "host@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
---

## 🔐 Password Reset Flow

### Scenario: User quên mật khẩu

**Step 1: Gửi email reset password**
```bash
curl -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Email reset mật khẩu đã được gửi đến user@example.com",
  "data": null
}
```

**Email Content (cần implement EmailService):**
```
Tiêu đề: [SmartRoom] Đặt lại mật khẩu

Nội dung:
Xin chào User,

Chúng tôi nhận được yêu cầu reset mật khẩu. 
Vui lòng nhấp vào link sau để đặt lại mật khẩu:

http://frontend-url/reset-password?token=<reset-token>

Link này sẽ hết hạn trong 24 giờ.

Nếu bạn không yêu cầu, vui lòng bỏ qua email này.

---
SmartRoom Team
```

**Step 2: User nhấp link → Frontend gửi reset password request**
```bash
curl -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "550e8400-e29b-41d4-a716-446655440000",
    "newPassword": "newSecurePassword123"
  }'
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Mật khẩu đã được thay đổi thành công",
  "data": null
}
```

**Response (Token hết hạn):**
```json
{
  "success": false,
  "message": "Link reset mật khẩu đã hết hạn",
  "data": null
}
```

**Step 3: User login lại với mật khẩu mới**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "newSecurePassword123"
  }'
```

---

## 🔄 Password Reset Diagram

```
┌──────────────┐
│   Frontend   │
│              │
│ User clicks: │
│ "Forgot      │
│  Password"   │
└──────┬───────┘
       │
       │ 1. POST /api/auth/forgot-password
       │    { "email": "user@example.com" }
       ↓
┌─────────────────────────────┐
│  AuthController             │
│  .forgotPassword()          │
└──────┬──────────────────────┘
       │
       │ 2. Call AuthService.forgotPassword()
       ↓
┌─────────────────────────────┐
│  AuthService                │
│  - Find user by email       │
│  - Generate reset token     │
│  - Save to memory/Redis     │
└──────┬──────────────────────┘
       │
       │ 3. Call EmailService.send()
       ↓
┌─────────────────────────────┐
│  EmailService               │
│  - Send email with token    │
│  - Expires in 24 hours      │
└──────┬──────────────────────┘
       │
       │ 4. Email sent to user
       ↓
┌──────────────┐
│   User       │
│              │
│ Receives     │
│ email with   │
│ reset link   │
└──────┬───────┘
       │
       │ 5. Click link in email
       │    frontend-url/reset?token=xxx
       ↓
┌──────────────┐
│   Frontend   │
│              │
│ User enters  │
│ new password │
│              │
│ POST /api/   │
│ auth/reset-  │
│ password     │
└──────┬───────┘
       │
       │ 6. POST /api/auth/reset-password
       │    { "token": "xxx", "newPassword": "..." }
       ↓
┌─────────────────────────────┐
│  AuthService                │
│  - Verify token exists      │
│  - Check not expired        │
│  - Hash new password        │
│  - Update user              │
│  - Remove token             │
└──────┬──────────────────────┘
       │
       │ 7. Success response
       ↓
┌──────────────┐
│   Frontend   │
│              │
│ Redirect to  │
│ login page   │
└──────┬───────┘
       │
       │ 8. User login with
       │    new password
       ↓
┌──────────────┐
│  Success!    │
└──────────────┘
```

### Step 3: Gia hạn token (nếu sắp hết hạn)
```bash
curl -X POST http://localhost:8081/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }'
```

## 🧪 Testing Guide

### Scenario 1: Complete User Registration & Login Flow

**Step 1: Register as TENANT**
```bash
curl -X POST http://localhost:8081/api/auth/register/tenant \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test Tenant",
    "email": "tenant-test@smartroom.com",
    "password": "TestPass123!",
    "phoneNumber": "0912345678",
    "idCardNumber": "123456789012"
  }'
```

Expected Response: `200 OK`
```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "data": null
}
```

**Step 2: Login**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "tenant-test@smartroom.com",
    "password": "TestPass123!"
  }'
```

Expected Response: `200 OK` with JWT token
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "userId": 1,
    "fullName": "Test Tenant",
    "email": "tenant-test@smartroom.com",
    "role": "TENANT",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZW5hbnQtdGVzdEBzbWFydHJvb20uY29tIiwiaWF0IjoxNzExNDU2OTk3LCJleHAiOjE3MTE1NDMzOTd9.4X-J5W..."
  }
}
```

**Step 3: Use Token in Request**
```bash
# Save token to variable
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# Call any protected endpoint
curl -X GET http://localhost:8082/api/host/areas \
  -H "Authorization: Bearer $TOKEN"
```

---

### Scenario 2: Password Reset Flow

**Step 1: Request Password Reset**
```bash
curl -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "tenant-test@smartroom.com"
  }'
```

Expected Response: `200 OK`
```json
{
  "success": true,
  "message": "Email reset mật khẩu đã được gửi",
  "data": null
}
```

Check logs:
- If email enabled: `✅ Password reset email sent to: tenant-test@smartroom.com`
- If email disabled: `ℹ️ Reset token available: 550e8400-e29b-41d4-a716-446655440000`

**Step 2: Reset Password (using token from logs)**
```bash
curl -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "550e8400-e29b-41d4-a716-446655440000",
    "newPassword": "NewTestPass456!"
  }'
```

Expected Response: `200 OK`
```json
{
  "success": true,
  "message": "Mật khẩu đã được thay đổi thành công",
  "data": null
}
```

**Step 3: Login with New Password**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "tenant-test@smartroom.com",
    "password": "NewTestPass456!"
  }'
```

---

### Scenario 3: Token Refresh

**Step 1: Get Token**
```bash
# Login to get token (see above)
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

**Step 2: Refresh Token**
```bash
curl -X POST http://localhost:8081/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }'
```

Expected Response: `200 OK`
```json
{
  "success": true,
  "message": "Gia hạn token thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZW5hbnQtdGVzdEBzbWFydHJvb20uY29tIiwiaWF0IjoxNzExNDU3MDAwLCJleHAiOjE3MTE1NDM0MDB9.new_signature_here"
  }
}
```

---

### Scenario 4: Logout

**Step 1: Logout with Token**
```bash
curl -X POST http://localhost:8081/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

Expected Response: `200 OK`
```json
{
  "success": true,
  "message": "Đăng xuất thành công",
  "data": null
}
```

**Step 2: Verify Token is Blacklisted**
```bash
# Try to use the same token again
curl -X GET http://localhost:8082/api/host/areas \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

Expected: `401 Unauthorized` - Token blacklisted

---

### Scenario 5: Error Cases

**Case 1: Invalid Credentials**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@smartroom.com",
    "password": "wrongpass"
  }'
```

Response: `404 Not Found`
```json
{
  "success": false,
  "message": "Email không tồn tại",
  "data": null
}
```

**Case 2: Email Already Exists**
```bash
curl -X POST http://localhost:8081/api/auth/register/tenant \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Another User",
    "email": "tenant-test@smartroom.com",
    "password": "Pass123!",
    "phoneNumber": "0909090909",
    "idCardNumber": "987654321098"
  }'
```

Response: `400 Bad Request`
```json
{
  "success": false,
  "message": "Email đã tồn tại",
  "data": null
}
```

**Case 3: Invalid Reset Token**
```bash
curl -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "invalid-token-xyz",
    "newPassword": "NewPass123!"
  }'
```

Response: `401 Unauthorized`
```json
{
  "success": false,
  "message": "Token không hợp lệ hoặc đã hết hạn",
  "data": null
}
```

---

## 🐛 Debugging
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJob3N0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzExMjM0NTY3LCJleHAiOjE3MTEzMjA5Njd9.PFcsHTkldAnWhsoeeH_fHoKgext6EhZMp1sJYAnHqRA"
### Check Active Secret Key
```bash
curl -X GET http://localhost:8081/api/auth/debug/secret
```
curl -X GET http://localhost:8082/api/host/areas \
Response:
```
"AUTH secret: [PFcsHTkldAnWhsoeeH/fHoKgext6EhZMp1sJYAnHqRA=] length=44"
```
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJob3N0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzExMjM0NTY3LCJleHAiOjE3MTEzMjA5Njd9.PFcsHTkldAnWhsoeeH_fHoKgext6EhZMp1sJYAnHqRA"
### Enable Debug Logging
Add to `application.properties`:
```properties
logging.level.com.project.authservice=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Check Database Users
```sql
SELECT * FROM user;
SELECT * FROM role;
```

---

## 📞 Support

Nếu gặp lỗi, kiểm tra:
1. **Logs** - `/logs/auth-service.log`
2. **JWT Secret** - Phải >= 256 bits (32 bytes)
3. **Database Connection** - `spring.datasource.url`
4. **Email Configuration** - Nếu sử dụng password reset
5. **CORS Settings** - Nếu gọi từ frontend khác domain
```

### Step 3: Gia hạn token (nếu sắp hết hạn)
```bash
curl -X POST http://localhost:8081/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }'
```

## 📝 TODO/Features Cần Bổ Sung

- ⭕ Email service integration (send password reset link)
- ⭕ Redis integration (replace in-memory token blacklist)
- ⭕ OAuth2 support (Google, Facebook login)
- ⭕ Multi-factor authentication (2FA)
- ⭕ API key authentication (for mobile apps)
- ⭕ Role-based access control (RBAC) enforcement

