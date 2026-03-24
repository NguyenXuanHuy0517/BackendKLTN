# Tenant Service - Tenant/Renter API (Port 8083)

## 📋 Mục Đích

Tenant Service cung cấp API cho người thuê (renter) để xem phòng, hóa đơn, khiếu nại, và tìm kiếm phòng.

## 🎯 Quyền Truy Cập

- ✅ Người thuê (TENANT): Full access to own data
- ❌ Chủ trọ (HOST): No access
- ❌ Admin: Read-only access

## 📡 API Endpoints

### 1. Profile (Hồ Sơ Cá Nhân)

#### GET /api/tenant/profile
**Lấy thông tin hồ sơ cá nhân**

Query: `userId`

Response:
```json
{
  "success": true,
  "data": {
    "userId": 5,
    "fullName": "Trần Văn B",
    "email": "tenant@example.com",
    "phoneNumber": "0987654321",
    "idCardNumber": "098765432109",
    "avatarUrl": "https://res.cloudinary.com/...",
    "role": "TENANT"
  }
}
```

#### PUT /api/tenant/profile
**Cập nhật hồ sơ cá nhân**

Request:
```json
{
  "fullName": "Trần Văn B - Updated",
  "phoneNumber": "0987654321",
  "avatarUrl": "https://..."
}
```

#### PUT /api/tenant/profile/password
**Thay đổi mật khẩu**

Request:
```json
{
  "currentPassword": "old_password",
  "newPassword": "new_password_123"
}
```

---

### 2. My Rooms (Phòng Hiện Tại)

#### GET /api/tenant/my-room
**Lấy phòng hiện tại người thuê**

Query: `userId`

Response:
```json
{
  "success": true,
  "data": {
    "roomId": 1,
    "roomCode": "A101",
    "floor": 1,
    "basePrice": 3000000,
    "elecPrice": 3500,
    "waterPrice": 15000,
    "areaSize": 25.5,
    "status": "RENTED",
    "amenities": ["WiFi", "AC", "Tủ lạnh"],
    "images": ["url1", "url2", "url3"],
    "areaName": "Khu trọ Sài Gòn",
    "hostName": "Nguyễn Văn A",
    "hostAvatar": "https://...",
    "description": "..."
  }
}
```

#### GET /api/tenant/my-contract
**Lấy hợp đồng hiện tại**

Query: `userId`

Response:
```json
{
  "success": true,
  "data": {
    "contractId": 1,
    "contractCode": "HD-001-2026",
    "roomCode": "A101",
    "startDate": "2026-04-01",
    "endDate": "2027-04-01",
    "actualRentPrice": 3000000,
    "elecPriceOverride": 3500,
    "waterPriceOverride": 15000,
    "status": "ACTIVE",
    "services": [
      {
        "serviceId": 1,
        "serviceName": "Dọn vệ sinh",
        "price": 100000,
        "unitName": "lần/tháng"
      }
    ]
  }
}
```

---

### 3. Invoices (Hóa Đơn)

#### GET /api/tenant/invoices
**Lấy danh sách hóa đơn của người thuê**

Query: `userId`

Response:
```json
{
  "success": true,
  "data": [
    {
      "invoiceId": 1,
      "invoiceCode": "INV-001-2026-04",
      "billingMonth": 4,
      "billingYear": 2026,
      "elecNew": 1500,
      "elecOld": 1400,
      "elecAmount": 100,
      "elecPrice": 3500,
      "elecCost": 350000,
      "waterNew": 450,
      "waterOld": 400,
      "waterAmount": 50,
      "waterPrice": 15000,
      "waterCost": 750000,
      "rentAmount": 3000000,
      "serviceAmount": 100000,
      "totalAmount": 4200000,
      "status": "UNPAID",
      "dueDate": "2026-05-05"
    }
  ]
}
```

#### GET /api/tenant/invoices/{invoiceId}
**Chi tiết hóa đơn**

Query: `userId` (để verify ownership)

#### GET /api/tenant/invoices/overdue
**Lấy hóa đơn quá hạn**

Query: `userId`

---

### 4. Issues (Khiếu Nại)

#### POST /api/tenant/issues
**Tạo khiếu nại mới**

Request (multipart/form-data):
```
userId: 5
title: "Quạt cây bị hỏng"
description: "Quạt cây trong phòng không chạy được"
priority: "HIGH"
images: [file1.jpg, file2.jpg]
```

Response:
```json
{
  "success": true,
  "data": {
    "issueId": 1,
    "title": "Quạt cây bị hỏng",
    "description": "...",
    "images": ["url1", "url2"],
    "priority": "HIGH",
    "status": "OPEN",
    "createdAt": "2026-04-15T10:30:00"
  }
}
```

#### GET /api/tenant/my-issues
**Lấy danh sách khiếu nại của người thuê**

Query: `userId`

Statuses: `OPEN`, `PROCESSING`, `RESOLVED`, `CLOSED`

#### GET /api/tenant/issues/{issueId}
**Chi tiết khiếu nại**

#### POST /api/tenant/issues/{issueId}/rating
**Đánh giá khiếu nại (sau khi resolved)**

Request:
```json
{
  "rating": 4,
  "comment": "Host xử lý nhanh, cảm ơn!"
}
```

---

### 5. Notifications

#### GET /api/tenant/notifications
**Lấy thông báo của người thuê**

Query: `userId`

Response:
```json
{
  "success": true,
  "data": [
    {
      "notificationId": 1,
      "type": "INVOICE_DUE",
      "title": "Hóa đơn sắp đến hạn",
      "body": "Hóa đơn tháng 4 có tổng tiền 4.200.000đ",
      "refType": "INVOICE",
      "refId": 1,
      "isRead": false,
      "createdAt": "2026-05-01T09:00:00"
    }
  ]
}
```

#### GET /api/tenant/notifications/unread-count
**Đếm thông báo chưa đọc**

#### PATCH /api/tenant/notifications/{notificationId}/read
**Đánh dấu thông báo đã đọc**

#### PATCH /api/tenant/notifications/read-all
**Đánh dấu tất cả thông báo đã đọc**

Query: `userId`

---

### 6. FCM Token (Push Notifications)

#### POST /api/tenant/fcm-token
**Lưu Firebase Cloud Messaging token**

Request:
```json
{
  "userId": 5,
  "fcmToken": "d-...firebase_token..."
}
```

Purpose:
- Server sử dụng token này để gửi push notification
- Hữu ích khi người dùng offline

---

### 7. Chatbot (Room Search)

#### POST /api/chatbot/ask
**Hỏi chatbot (AI) về tìm phòng**

Request:
```json
{
  "userId": 5,
  "question": "Tôi muốn tìm phòng trong khu Bình Thạnh có giá dưới 3 triệu"
}
```

Response:
```json
{
  "success": true,
  "data": {
    "answer": "Tôi tìm được 3 phòng phù hợp...",
    "suggestions": [
      {
        "roomId": 5,
        "roomCode": "B201",
        "price": 2800000,
        "area": "Khu trọ Bình Thạnh",
        "amenities": ["WiFi", "AC"]
      }
    ]
  }
}
```

#### GET /api/chatbot/history
**Lấy lịch sử chat**

Query: `userId`

#### DELETE /api/chatbot/history
**Xóa lịch sử chat**

Query: `userId`

---

### 8. Room Search

#### GET /api/rooms/search
**Tìm kiếm phòng trống**

Query params:
- `city`: Thành phố
- `district`: Quận
- `minPrice`: Giá tối thiểu
- `maxPrice`: Giá tối đa
- `amenities`: Tiện nghi (WiFi, AC, etc.)

Response:
```json
{
  "success": true,
  "data": [
    {
      "roomId": 1,
      "roomCode": "A101",
      "areaName": "Khu trọ A",
      "address": "123 Đường A, Quận X",
      "price": 2500000,
      "amenities": ["WiFi", "AC"],
      "images": ["url1", "url2"],
      "hostName": "Nguyễn Văn A",
      "hostAvatar": "https://..."
    }
  ]
}
```

#### GET /api/rooms/suggest
**Gợi ý phòng theo ngân sách**

Query: `budget=3000000`

#### GET /api/rooms/available
**Lấy phòng trống (không filter)**

---

## 🏗️ Cấu Trúc Code

```
tenant-service/
├── controller/
│   ├── TenantProfileController.java
│   ├── TenantRoomController.java
│   ├── TenantInvoiceController.java
│   ├── TenantIssueController.java
│   ├── TenantNotificationController.java
│   ├── FcmTokenController.java
│   ├── ChatbotController.java
│   └── RoomSearchController.java
├── service/
│   ├── TenantProfileService.java
│   ├── TenantBillingService.java
│   ├── TenantIssueService.java
│   ├── ChatbotService.java
│   └── RoomSearchService.java
├── integration/
│   ├── GeminiClient.java           # Google Gemini AI
│   └── OpenAiClient.java           # Fallback OpenAI
├── mapper/
│   ├── ProfileMapper.java
│   ├── InvoiceMapper.java
│   ├── IssueMapper.java
│   └── NotificationMapper.java
├── dto/
│   ├── profile/
│   ├── invoice/
│   ├── issue/
│   ├── notification/
│   ├── room/
│   └── chatbot/
└── config/
    ├── SecurityConfig.java
    └── CorsConfig.java
```

---

## 📊 Data Access Pattern

```
Tenant User ID = 5

Allowed queries:
✅ GET /api/tenant/invoices?userId=5         (Only own invoices)
✅ GET /api/tenant/issues?userId=5           (Only own issues)
✅ GET /api/tenant/my-room?userId=5          (Only current room)
✅ GET /api/tenant/profile?userId=5          (Only own profile)

Forbidden:
❌ GET /api/tenant/invoices?userId=6         (Other user's data)
❌ PATCH /api/tenant/profile?userId=6        (Modify other user)
```

---

## 🤖 Chatbot Integration

### Flow:

```
1. Tenant: "Tôi muốn tìm phòng giá rẻ ở Q1"
   ↓
2. ChatbotService.ask()
   ├─ Parse intent & entities
   ├─ Call GeminiClient.sendMessage()
   ├─ Get AI response
   └─ Extract room suggestions
   ↓
3. RoomSearchService.search()
   ├─ Filter rooms matching criteria
   └─ Return with images & host info
   ↓
4. Response to Tenant:
   {
     "answer": "Tôi tìm được 5 phòng...",
     "suggestions": [...]
   }
```

---

## 🔐 Security

- ✅ JWT authentication required
- ✅ Tenant chỉ thấy dữ liệu của chính mình
- ✅ Can't modify other user's profile
- ✅ Image upload với Cloudinary validation

---

## 📝 TODO/Features Cần Bổ Sung

- ⭕ Payment integration (VNPay, Stripe)
- ⭕ Deposit refund processing
- ⭕ Email notifications
- ⭕ SMS reminders for due invoices
- ⭕ Advanced room search filters (size, floor, etc.)
- ⭕ Save favorite rooms
- ⭕ Request viewing appointments
- ⭕ Lease renewal process
- ⭕ Utility consumption tracking
- ⭕ Maintenance request scheduling

