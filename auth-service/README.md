# Auth Service - Authentication & Authorization (Port 8081)

## 📋 Mục Đích

Auth Service cung cấp tất cả chức năng xác thực & phát hành JWT token cho hệ thống.

## 🔑 Authentication Mechanism

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
  }
}
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
- 200 - Đăng nhập thành công
- 400 - Email/password không hợp lệ
- 401 - Tài khoản bị khóa
- 404 - Không tìm thấy người dùng

---

### 2. POST /api/auth/register
**Mục đích:** Tạo tài khoản mới (TENANT)

**Request:**
```json
{
  "fullName": "Nguyễn Văn B",
  "email": "tenant@example.com",
  "password": "securepass123",
  "phoneNumber": "0912345678",
  "idCardNumber": "012345678901"
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
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWI..."
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

### 5. POST /api/auth/forgot-password
**Mục đích:** Yêu cầu reset mật khẩu

**Request:**
```json
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
**Mục đích:** Đặt lại mật khẩu

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
│   Client     │
│  Receives:   │
│  - areas []  │
└──────────────┘
```

## ⚠️ Error Handling

| Scenario | Status | Message |
|----------|--------|---------|
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
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJob3N0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzExMjM0NTY3LCJleHAiOjE3MTEzMjA5Njd9.PFcsHTkldAnWhsoeeH_fHoKgext6EhZMp1sJYAnHqRA"
  }
}
```

### Step 2: Sử dụng token gọi API
```bash
curl -X GET http://localhost:8082/api/host/areas \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJob3N0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzExMjM0NTY3LCJleHAiOjE3MTEzMjA5Njd9.PFcsHTkldAnWhsoeeH_fHoKgext6EhZMp1sJYAnHqRA"
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

