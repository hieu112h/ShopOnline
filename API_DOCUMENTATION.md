# 📚 TÀI LIỆU HỆ THỐNG BACKEND & HƯỚNG DẪN SỬ DỤNG API
### Ứng dụng Backend Bán Hàng Mô Hình Ô Tô & Robot (Spring Boot + PostgreSQL + JWT + OAuth2)

---

## 1. Cấu Trúc Cơ Sở Dữ Liệu (Database Schema)

Cơ sở dữ liệu được cấu hình kết nối tới **PostgreSQL** tại file [application.properties](file:///d:/shop/src/main/resources/application.properties):
- **URL**: `jdbc:postgresql://localhost:5432/shop`
- **Username**: `postgres`
- **Password**: `123456`
- **Port Server**: `6089`

File kịch bản khởi tạo DB đầy đủ với bảng và dữ liệu mẫu đã được tạo tại [db_schema.sql](file:///d:/shop/db_schema.sql).

### Các bảng dữ liệu chính:
1. **`users`**: Quản lý tài khoản (hỗ trợ cả tài khoản nội bộ `LOCAL` và tài khoản mạng xã hội `GOOGLE`, `GITHUB`, phân quyền `ROLE_USER`, `ROLE_ADMIN`).
2. **`categories`**: Quản lý danh mục mô hình (nhóm `CAR_MODEL` như Siêu xe, Xe đua F1, Xe cổ, Diecast 1:64... và nhóm `ROBOT_MODEL` như Gundam Gunpla, Transformers, Mecha Anime...).
3. **`products`**: Quản lý chi tiết sản phẩm mô hình với các trường chuyên biệt:
   - `scale`: Tỉ lệ mô hình (1:18, 1:24, 1:64 đối với ô tô; 1/144 HG, 1/100 MG, 1/60 PG đối với Gundam).
   - `material`: Chất liệu (Hợp kim Diecast, Nhựa ABS, Resin cao cấp...).
   - `brand`: Thương hiệu (AutoArt, Minichamps, Kyosho, Bandai Spirits, Threezero...).
   - `price`, `original_price`, `stock_quantity`, `status`, `featured`, `image_url`.
4. **`product_images`**: Danh sách ảnh chi tiết kèm theo của sản phẩm mô hình.

### Tài khoản mẫu có sẵn (Mật khẩu: `123456`):
- **Admin**: `admin@shop.com` / `123456` (Quyền: `ROLE_ADMIN` - có thể thêm, sửa, xóa sản phẩm và danh mục)
- **User**: `user@shop.com` / `123456` (Quyền: `ROLE_USER`)

---

## 2. API Xác Thực (Authentication & OAuth2)

### 2.1 Đăng ký tài khoản (Register)
- **Endpoint**: `POST /api/v1/auth/register`
- **Quyền**: Public
- **Request Body**:
```json
{
  "email": "khachhang@example.com",
  "password": "password123",
  "fullName": "Nguyễn Văn A",
  "username": "nguyenvana",
  "phoneNumber": "0987654321",
  "address": "Hà Nội, Việt Nam"
}
```
- **Response**: Trả về thông tin User kèm JWT Token.

---

### 2.2 Đăng nhập (Login)
- **Endpoint**: `POST /api/v1/auth/login`
- **Quyền**: Public
- **Request Body**:
```json
{
  "email": "admin@shop.com",
  "password": "123456"
}
```
- **Response**:
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "email": "admin@shop.com",
      "fullName": "Quản Trị Viên",
      "role": "ROLE_ADMIN"
    }
  }
}
```

---

### 2.3 Lấy thông tin tài khoản hiện tại (Current User)
- **Endpoint**: `GET /api/v1/auth/me`
- **Headers**: `Authorization: Bearer <accessToken>`
- **Response**: Trả về chi tiết tài khoản của người dùng đang đăng nhập.

---

### 2.4 Hỗ trợ OAuth2 (Google / GitHub)

Hệ thống hỗ trợ cả 2 cơ chế tích hợp OAuth2 linh hoạt:

1. **OAuth2 Luồng Trình Duyệt (Web Redirect Flow)**:
   - Truy cập: `http://localhost:6089/oauth2/authorize/google` hoặc `github`
   - Người dùng đăng nhập qua Google -> Server tự động tạo/cập nhật tài khoản trong DB -> Sinh JWT Token -> Redirect về Frontend kèm token:
     `http://localhost:3000/oauth2/redirect?token=...`
   - Để kích hoạt, bạn chỉ cần mở file `application.properties` hoặc truyền biến môi trường:
     ```properties
     spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
     spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
     ```

2. **OAuth2 Luồng SPA / Mobile (Direct Token Exchange)**:
   - **Endpoint**: `POST /api/v1/auth/oauth2/token`
   - **Request Body**:
     ```json
     {
       "provider": "google",
       "token": "google_id_token_from_client",
       "email": "user@gmail.com",
       "name": "Tên Người Dùng",
       "avatarUrl": "https://..."
     }
     ```
   - **Response**: Trả về JWT Token của hệ thống.

---

## 3. API Quản Lý Danh Mục (Categories CRUD)

| Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/categories` | Public | Lấy danh sách danh mục (hỗ trợ query param `?group=CAR_MODEL` hoặc `?group=ROBOT_MODEL`) |
| **GET** | `/api/v1/categories/{id}` | Public | Lấy chi tiết danh mục theo ID |
| **GET** | `/api/v1/categories/slug/{slug}` | Public | Lấy chi tiết danh mục theo Slug URL |
| **POST** | `/api/v1/categories` | **ROLE_ADMIN** | Thêm mới danh mục |
| **PUT** | `/api/v1/categories/{id}` | **ROLE_ADMIN** | Cập nhật danh mục |
| **DELETE** | `/api/v1/categories/{id}` | **ROLE_ADMIN** | Xóa danh mục |

### Ví dụ Body tạo mới/cập nhật danh mục (`POST` / `PUT`):
```json
{
  "name": "Mô hình Siêu xe Tỉ lệ 1:18",
  "description": "Các dòng xe Lamborghini, Ferrari, McLaren bản tỉ lệ lớn 1:18",
  "categoryGroup": "CAR_MODEL",
  "imageUrl": "https://example.com/img/cars.jpg",
  "active": true
}
```

---

## 4. API Quản Lý Sản Phẩm Mô Hình (Products CRUD)

| Method | Endpoint | Quyền | Mô tả |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/products` | Public | Danh sách sản phẩm phân trang, tìm kiếm và lọc linh hoạt |
| **GET** | `/api/v1/products/{id}` | Public | Lấy chi tiết sản phẩm theo ID |
| **GET** | `/api/v1/products/slug/{slug}` | Public | Lấy chi tiết sản phẩm theo Slug SEO |
| **GET** | `/api/v1/products/featured` | Public | Lấy danh sách sản phẩm nổi bật |
| **POST** | `/api/v1/products` | **ROLE_ADMIN** | Thêm mới sản phẩm mô hình |
| **PUT** | `/api/v1/products/{id}` | **ROLE_ADMIN** | Cập nhật sản phẩm mô hình |
| **DELETE** | `/api/v1/products/{id}` | **ROLE_ADMIN** | Xóa sản phẩm mô hình |

### Các tham số lọc & phân trang cho `GET /api/v1/products`:
- `keyword`: Tìm theo tên sản phẩm hoặc thương hiệu (vd: `Lamborghini`, `Gundam`, `AutoArt`)
- `categoryId`: Lọc theo ID danh mục
- `scale`: Lọc theo tỉ lệ (vd: `1:18`, `1:24`, `1:64`, `1/144 RG`, `1/100 MG`)
- `brand`: Lọc theo hãng (vd: `AutoArt`, `Bandai Spirits`, `Threezero`, `Hot Wheels`)
- `minPrice` & `maxPrice`: Lọc khoảng giá
- `status`: Lọc trạng thái (`IN_STOCK`, `OUT_OF_STOCK`, `PRE_ORDER`)
- `page`: Trang số (bắt đầu từ 0, mặc định 0)
- `size`: Số sản phẩm mỗi trang (mặc định 12)
- `sort`: Sắp xếp (vd: `price,asc`, `price,desc`, `id,desc`)

### Ví dụ Request Body tạo sản phẩm mô hình ô tô (`POST /api/v1/products`):
```json
{
  "name": "Mô hình Siêu Xe Lamborghini Revuelto 1:18",
  "description": "Mô hình cao cấp đúc kim loại Diecast, vô lăng đánh lái, bánh xe xoay, cửa cắt kéo mở được.",
  "price": 4800000,
  "originalPrice": 5200000,
  "stockQuantity": 5,
  "scale": "1:18",
  "material": "Hợp kim Diecast",
  "brand": "AutoArt",
  "categoryId": 1,
  "status": "IN_STOCK",
  "featured": true,
  "imageUrl": "https://example.com/images/revuelto-front.jpg",
  "images": [
    "https://example.com/images/revuelto-side.jpg",
    "https://example.com/images/revuelto-interior.jpg"
  ]
}
```

### Ví dụ Request Body tạo sản phẩm mô hình robot Gundam (`POST /api/v1/products`):
```json
{
  "name": "Mô hình Gundam PG 1/60 RX-78-2 Unleashed",
  "description": "Đỉnh cao công nghệ Gunpla của Bandai, khung xương đa lớp kim loại, hệ thống đèn led phản quang.",
  "price": 6200000,
  "originalPrice": 6500000,
  "stockQuantity": 3,
  "scale": "1/60 PG",
  "material": "Khung xương kim loại + Nhựa ABS",
  "brand": "Bandai Spirits",
  "categoryId": 4,
  "status": "IN_STOCK",
  "featured": true,
  "imageUrl": "https://example.com/images/pg-unleashed.jpg",
  "images": [
    "https://example.com/images/pg-unleashed-frame.jpg"
  ]
}
```

---

## 5. Định Dạng Chuẩn Phản Hồi API (Standard ApiResponse)

Tất cả các endpoint đều trả về theo chuẩn thống nhất:
```json
{
  "success": true,
  "message": "Thành công",
  "data": { ... },
  "timestamp": "2026-11-20T17:30:00"
}
```
Nếu có lỗi (Validation hoặc Exception), hệ thống sẽ trả về mã HTTP tương ứng (400, 401, 403, 404, 500) và định dạng:
```json
{
  "success": false,
  "message": "Dữ liệu không hợp lệ",
  "data": {
    "price": "Giá sản phẩm không được âm",
    "name": "Tên sản phẩm không được để trống"
  },
  "timestamp": "2026-11-20T17:30:00"
}
```
