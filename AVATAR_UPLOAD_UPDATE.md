# Avatar Upload Update - Tenant Service

## 📋 Summary
Cập nhật Avatar Service và Controller cho tenant-service để hỗ trợ upload avatar trực tiếp lên Cloudinary, tương tự như host-service.

## 🔧 Changes Made

### 1. AvatarService (`tenant-service/src/main/java/.../service/AvatarService.java`)

**Thêm:**
- `updateAvatar(Long userId, MultipartFile file)` - Upload file trực tiếp lên Cloudinary
- `uploadToCloudinary(MultipartFile file, Long userId)` - Helper upload file
- `validateImageFile(MultipartFile file)` - Validate file (chỉ ảnh, max 5MB)
- `extractPublicId(String url)` - Trích publicId từ Cloudinary URL

**Tính năng:**
- ✅ Upload ảnh mới (tự động crop 400×400)
- ✅ Xoá ảnh cũ trên Cloudinary (tiết kiệm storage)
- ✅ Validate kích thước file (max 5 MB)
- ✅ Validate định dạng file (chỉ image/*)
- ✅ Overwrite ảnh cũ cùng userId

**Cloudinary Configuration:**
- Folder: `smartroom/avatars`
- Public ID: `user_{userId}` (overwrite = true)
- Transform: `c_fill,g_face,h_400,w_400,q_auto,f_auto` (crop khuôn mặt 400×400)

### 2. AvatarController (`tenant-service/src/main/java/.../controller/AvatarController.java`)

**Endpoints:**

| Method | URL | Body | Description |
|--------|-----|------|-------------|
| POST | `/api/tenant/avatar?userId={id}` | multipart/form-data (file) | Upload avatar trực tiếp |
| PUT | `/api/tenant/avatar?userId={id}` | `{"avatarUrl": "..."}` | Cập nhật via URL |
| DELETE | `/api/tenant/avatar?userId={id}` | - | Xoá avatar |

**Response:**
```json
{
  "success": true,
  "data": "https://res.cloudinary.com/dbj3kf54f/image/upload/v.../smartroom/avatars/user_5.jpg",
  "message": null
}
```

## 📱 Flutter Usage

### Flow 1: Upload trực tiếp
```dart
// Gọi endpoint upload file
POST /api/tenant/avatar?userId=123
Content-Type: multipart/form-data
Body: 
  file: <binary image data>

// Response
{
  "success": true,
  "data": "https://res.cloudinary.com/..."
}

// Lưu URL vào SharedPreferences
```

### Flow 2: Cập nhật via URL (nếu Flutter tự upload)
```dart
// Flutter upload lên Cloudinary bằng unsigned preset
// Lấy URL về, rồi gọi:
PUT /api/tenant/avatar?userId=123
Content-Type: application/json
Body: {"avatarUrl": "https://res.cloudinary.com/..."}
```

### Flow 3: Xoá avatar
```dart
DELETE /api/tenant/avatar?userId=123
```

## 🔐 Configuration

Tenant-service đã có `CloudinaryConfig` và Cloudinary credentials trong `application.properties`:
```properties
cloudinary.cloud-name=dbj3kf54f
cloudinary.api-key=161871263642373
cloudinary.api-secret=5qNcCOZlfiGL2hL9M6XRZ61JLQY
```

## 🧪 Testing

### Test Upload Avatar
```bash
curl -X POST "http://localhost:8083/api/tenant/avatar?userId=1" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/image.jpg"
```

### Test Update Avatar URL
```bash
curl -X PUT "http://localhost:8083/api/tenant/avatar?userId=1" \
  -H "Content-Type: application/json" \
  -d '{"avatarUrl":"https://res.cloudinary.com/..."}'
```

### Test Delete Avatar
```bash
curl -X DELETE "http://localhost:8083/api/tenant/avatar?userId=1"
```

## 📝 Notes

- Service tự động validate file: phải là image/*, max 5 MB
- File cũ được xoá tự động khi upload file mới
- URL được lưu vào `users.avatar_url` trong database
- Cloudinary credentials lấy từ `application.properties` (đã có sẵn)
- Transform Cloudinary: crop 400×400 focus vào khuôn mặt, auto quality & format

## ✅ Status
- ✅ AvatarService updated
- ✅ AvatarController updated
- ✅ Cloudinary config already exists
- ✅ Ready for testing

