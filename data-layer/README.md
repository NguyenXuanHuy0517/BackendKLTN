# Data Layer - Shared Data Models & Repositories

## 📋 Mục Đích

Data Layer là shared library cung cấp:
- **JPA Entities** - Định nghĩa database schema
- **Spring Data Repositories** - CRUD operations
- **Common DTOs** - Shared data transfer objects
- **Enums** - Shared constants

## 🏗️ Cấu Trúc Thư Mục

```
data-layer/
├── src/main/java/com/project/datalayer/
│   ├── entity/
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── MotelArea.java
│   │   ├── Room.java
│   │   ├── Contract.java
│   │   ├── Invoice.java
│   │   ├── Deposit.java
│   │   ├── Issue.java
│   │   ├── Service.java
│   │   ├── Equipment.java
│   │   ├── RoomAsset.java
│   │   ├── Notification.java
│   │   └── ChatbotHistory.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── RoomRepository.java
│   │   ├── ContractRepository.java
│   │   ├── InvoiceRepository.java
│   │   └── ... (tất cả repositories)
│   ├── dto/
│   │   └── common/
│   │       └── ApiResponse.java
│   └── enums/
│       ├── RoomStatus.java
│       ├── InvoiceStatus.java
│       └── ...
└── pom.xml
```

## 📊 Entity Relationships

```
User (ADMIN|HOST|TENANT)
  ├─→ Role
  ├─→ MotelArea (nếu HOST)
  ├─→ Contract (nếu TENANT)
  ├─→ Notification
  └─→ ChatbotHistory

MotelArea
  ├─→ User (Host)
  ├─→ Room[]
  └─→ Service[]

Room
  ├─→ MotelArea
  ├─→ RoomStatusHistory[]
  ├─→ RoomAsset[]
  └─→ Contract (current)

Contract
  ├─→ Room
  ├─→ User (Tenant)
  ├─→ Deposit
  ├─→ ContractService[]
  └─→ Invoice[]

Invoice
  ├─→ Contract
  └─→ InvoiceItem[]

Issue
  ├─→ Room
  └─→ User (Tenant)

Equipment
  ├─→ MotelArea
  └─→ RoomAsset[]
```

## 🔑 Core Entities

### 1. **User**
```java
// Mô tả: Tất cả người dùng hệ thống
@Entity
@Table(name = "users")
public class User {
    Long userId;           // PK
    Role role;             // FK: ADMIN, HOST, TENANT
    String fullName;
    String email;          // Unique
    String phoneNumber;    // Unique
    String passwordHash;   // BCrypt
    String idCardNumber;   // Unique
    String avatarUrl;      // Cloudinary URL
    String fcmToken;       // Firebase token
    boolean isActive;
    LocalDateTime lastLoginAt;
}
```

### 2. **Room**
```java
// Mô tả: Phòng trong khu trọ
@Entity
@Table(name = "rooms")
public class Room {
    Long roomId;              // PK
    MotelArea area;           // FK
    String roomCode;          // Unique per area
    Integer floor;
    BigDecimal basePrice;
    BigDecimal elecPrice;
    BigDecimal waterPrice;
    BigDecimal areaSize;
    String status;            // AVAILABLE, RENTED, MAINTENANCE
    String amenities;         // JSON: ["WiFi", "AC"]
    String images;            // JSON: ["url1", "url2"]
    String description;
}
```

### 3. **Contract**
```java
// Mô tả: Hợp đồng thuê phòng
@Entity
@Table(name = "contracts")
public class Contract {
    Long contractId;               // PK
    String contractCode;           // Unique
    Room room;                     // FK
    User tenant;                   // FK
    Deposit deposit;               // FK nullable
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal actualRentPrice;
    BigDecimal elecPriceOverride;  // nullable
    BigDecimal waterPriceOverride; // nullable
    String status;                 // ACTIVE, EXPIRED, TERMINATED_EARLY
}
```

### 4. **Invoice**
```java
// Mô tả: Hóa đơn điện nước/dịch vụ
@Entity
@Table(name = "invoices")
public class Invoice {
    Long invoiceId;          // PK
    Contract contract;       // FK
    String invoiceCode;      // Unique
    Integer billingMonth;
    Integer billingYear;
    Integer elecOld, elecNew, elecAmount;
    Integer waterOld, waterNew, waterAmount;
    BigDecimal rentAmount;
    BigDecimal serviceAmount;
    BigDecimal totalAmount;
    String status;           // DRAFT, UNPAID, PAID, OVERDUE
    LocalDateTime paidAt;    // nullable
}
```

### 5. **Issue**
```java
// Mô tả: Khiếu nại/sự cố phòng
@Entity
@Table(name = "issues")
public class Issue {
    Long issueId;          // PK
    Room room;             // FK
    User tenant;           // FK
    String title;
    String description;
    String images;         // JSON: ["url1", "url2"]
    String priority;       // LOW, MEDIUM, HIGH, URGENT
    String status;         // OPEN, PROCESSING, RESOLVED, CLOSED
    Integer rating;        // 1-5 star rating từ tenant
    LocalDateTime resolvedAt;
}
```

## 📋 Enum Values

### RoomStatus
- `AVAILABLE` - Sẵn sàng cho thuê
- `RENTED` - Đang được thuê
- `MAINTENANCE` - Bảo trì
- `DEPOSITED` - Có tiền cọc nhưng chưa hợp đồng

### ContractStatus
- `ACTIVE` - Hợp đồng hiệu lực
- `EXPIRED` - Hết hạn
- `TERMINATED_EARLY` - Kết thúc sớm

### InvoiceStatus
- `DRAFT` - Bản nháp (admin nhập số điện/nước)
- `UNPAID` - Chưa thanh toán
- `PAID` - Đã thanh toán
- `OVERDUE` - Quá hạn

### IssuePriority
- `LOW` - Ưu tiên thấp
- `MEDIUM` - Ưu tiên trung bình
- `HIGH` - Ưu tiên cao
- `URGENT` - Khẩn cấp

### IssueStatus
- `OPEN` - Mới tạo
- `PROCESSING` - Đang xử lý
- `RESOLVED` - Đã giải quyết
- `CLOSED` - Đóng (đã rated)

## 🔍 Repository Methods

### UserRepository
```java
Optional<User> findByEmail(String email);
Optional<User> findByPhoneNumber(String phoneNumber);
List<User> findByRole_RoleName(String roleName);  // Tìm HOST hoặc TENANT
```

### RoomRepository
```java
List<Room> findByArea_AreaId(Long areaId);
List<Room> findByArea_Host_UserId(Long hostId);  // Tất cả phòng của host
Long countByArea_AreaIdAndStatus(Long areaId, String status);
```

### ContractRepository
```java
List<Contract> findByRoom_Area_Host_UserId(Long hostId);
Optional<Contract> findByRoom_RoomIdAndStatus(Long roomId, String status);
List<Contract> findByStatusAndEndDateBefore(String status, LocalDate date);
```

### InvoiceRepository
```java
List<Invoice> findByContract_Room_Area_Host_UserId(Long hostId);
List<Invoice> findByStatusIn(List<String> statuses);
boolean existsByContractIdAndBillingMonthAndYear(Long contractId, Integer month, Integer year);
```

### IssueRepository
```java
List<Issue> findByRoom_Area_Host_UserId(Long hostId);  // Issues trong phòng của host
List<Issue> findByTenant_UserId(Long tenantId);        // Issues của tenant
List<Issue> findByStatus(String status);               // Tìm theo status
```

## 📦 Common DTOs

### ApiResponse<T>
```java
// Wrapper cho tất cả API responses
public class ApiResponse<T> {
    boolean success;      // true/false
    String message;       // "Thành công" hoặc error message
    T data;               // Actual response data
}
```

Ví dụ response:
```json
{
  "success": true,
  "message": "Lấy danh sách phòng thành công",
  "data": [
    { "roomId": 1, "roomCode": "A101", "status": "RENTED" },
    { "roomId": 2, "roomCode": "A102", "status": "AVAILABLE" }
  ]
}
```

## 🔄 Database Relationships

### One-to-Many
- User ← MotelArea (1 Host có nhiều khu trọ)
- MotelArea ← Room (1 khu trọ có nhiều phòng)
- Room ← Contract (1 phòng có nhiều hợp đồng qua thời gian)
- Contract ← Invoice (1 hợp đồng có nhiều hóa đơn theo tháng)
- User ← Issue (1 tenant báo cáo nhiều issues)

### Many-to-One
- Contract → Room, User (Tenant), Deposit
- Invoice → Contract
- Issue → Room, User
- RoomAsset → Room, Equipment

## 🛠️ Cách Sử Dụng

### Trong một Service
```java
@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;

    public RoomResponseDTO getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        
        // Map entity to DTO
        RoomResponseDTO dto = mapper.toDTO(room);
        
        // Load additional data
        contractRepository.findByRoom_RoomIdAndStatus(roomId, "ACTIVE")
            .ifPresent(contract -> dto.setCurrentTenantName(contract.getTenant().getFullName()));
        
        return dto;
    }
}
```

## 📈 Scaling Notes

- **Partitioning:** Invoice table có thể partition theo contract_id hoặc billing_year
- **Indexing:** user.email, room.area_id, contract.status, invoice.billing_month
- **Caching:** Room details, User profiles có thể cache với Redis
- **Archiving:** Invoices cũ > 1 năm có thể archive to separate table

## ✅ Validation Rules

- Email: Unique, valid format
- PhoneNumber: Unique, format 10-11 digits
- MotelArea.latitude/longitude: Valid GPS coordinates
- Invoice.elecAmount = (elecNew - elecOld) * elecPrice
- Contract.endDate > startDate
- Room.basePrice > 0

Xem chi tiết từng module service để hiểu cách sử dụng entities này.

