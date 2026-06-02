# Tài liệu API (API Documentation)

Tài liệu này mô tả các endpoint REST API hiện có trong hệ thống E-commerce để các ứng dụng khác (ví dụ: Frontend SPA, Mobile App, Postman) có thể kết nối vào.

## Cấu trúc Response chung (ApiResponse)
Tất cả các API trả về JSON đều được bọc trong một định dạng chung (`ApiResponse<T>`) như sau:

```json
{
  "success": true,        // boolean: true nếu thành công, false nếu thất bại
  "message": "...",       // string: Thông báo kết quả (ví dụ: "Thêm thành công")
  "data": { ... }         // object/array/null: Dữ liệu trả về (tùy thuộc vào từng API)
}
```

---

## 1. Giỏ hàng (Cart API)

Các API quản lý giỏ hàng của người dùng hiện tại.
*(Lưu ý: Hệ thống hiện đang hardcode User ID = 5 cho mục đích test, sau này khi có phân quyền sẽ tự động lấy từ Authentication Token)*

### 1.1 Lấy thông tin giỏ hàng
- **URL**: `/carts`
- **Method**: `GET`
- **Response Trả về**: `ApiResponse<CartResponse>`
- **Ví dụ Response Thành công**:
  ```json
  {
    "success": true,
    "message": "Lấy thông tin giỏ hàng thành công",
    "data": {
      "id": 1,
      "items": [
        {
          "id": 10,
          "variantId": 2,
          "quantity": 1,
          "productName": "Giày Nike",
          "price": 500000
        }
      ],
      "totalPrice": 500000
    }
  }
  ```

### 1.2 Thêm sản phẩm vào giỏ
- **URL**: `/carts/items`
- **Method**: `POST`
- **Body** (JSON):
  ```json
  {
    "variantId": 2,     // ID của biến thể sản phẩm (Bắt buộc)
    "quantity": 1       // Số lượng (Bắt buộc)
  }
  ```
- **Response Trả về**: `ApiResponse<Void>`
  ```json
  {
    "success": true,
    "message": "Đã thêm sản phẩm vào giỏ hàng",
    "data": null
  }
  ```

### 1.3 Cập nhật số lượng sản phẩm trong giỏ
- **URL**: `/carts/items/{id}` (với `{id}` là ID của *Cart Item*, không phải Product ID)
- **Method**: `PUT`
- **Body** (JSON):
  ```json
  {
    "quantity": 3       // Số lượng mới (Bắt buộc)
  }
  ```
- **Response Trả về**: `ApiResponse<Void>`
  ```json
  {
    "success": true,
    "message": "Cập nhật số lượng thành công",
    "data": null
  }
  ```

### 1.4 Xóa một sản phẩm khỏi giỏ
- **URL**: `/carts/items/{id}` (với `{id}` là ID của *Cart Item*)
- **Method**: `DELETE`
- **Response Trả về**: `ApiResponse<Void>`
  ```json
  {
    "success": true,
    "message": "Xóa sản phẩm thành công",
    "data": null
  }
  ```

### 1.5 Xóa toàn bộ giỏ hàng
- **URL**: `/carts`
- **Method**: `DELETE`
- **Response Trả về**: `ApiResponse<Void>`
  ```json
  {
    "success": true,
    "message": "Đã làm trống giỏ hàng",
    "data": null
  }
  ```

---

## 2. Đơn hàng (Order API)

### 2.1 Đặt hàng (Checkout)
- **URL**: `/api/orders/checkout`
- **Method**: `POST`
- **Body** (JSON):
  ```json
  {
    "shippingFullName": "Nguyễn Văn A",      // (Bắt buộc)
    "shippingPhone": "0123456789",           // (Bắt buộc)
    "shippingAddressLine": "123 Đường ABC",  // (Bắt buộc)
    "shippingWard": "Phường 1",
    "shippingDistrict": "Quận 1",
    "shippingCity": "Hồ Chí Minh",
    "shippingCountry": "Việt Nam",
    "paymentMethod": "COD",                  // (Bắt buộc) - VD: COD, VNPAY
    "note": "Giao giờ hành chính",
    "selectedCartItemIds": [10, 11]          // (Tùy chọn) Danh sách các Cart Item ID muốn thanh toán
  }
  ```
- **Response Trả về**: `ApiResponse<OrderResponse>`
  ```json
  {
    "success": true,
    "message": "Đặt hàng thành công",
    "data": {
      // Thông tin cơ bản của đơn hàng sau khi tạo (orderId, orderCode, ...)
    }
  }
  ```

### 2.2 Lấy chi tiết đơn hàng
- **URL**: `/api/orders/{orderCode}` (với `{orderCode}` là mã đơn hàng chuỗi, ví dụ: `ORD-12345`)
- **Method**: `GET`
- **Response Trả về**: `ApiResponse<OrderDetailResponse>`
  ```json
  {
    "success": true,
    "message": "Chi tiết đơn hàng",
    "data": {
      "orderCode": "ORD-12345",
      "orderStatus": "PENDING",
      "paymentStatus": "UNPAID",
      "paymentMethod": "COD",
      "totalAmount": 1000000,
      "shippingFullName": "Nguyễn Văn A",
      "shippingPhone": "0123456789",
      "shippingAddress": "123 Đường ABC, Phường 1, Quận 1, Hồ Chí Minh, Việt Nam",
      "placedAt": "2026-05-24T10:00:00"
    }
  }
  ```
