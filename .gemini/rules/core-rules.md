# Quy Tắc Cốt Lõi (Core Rules)
*Đây là các quy tắc kinh doanh và kỹ thuật bắt buộc tuân thủ khi viết code cho module này:*

## 1. Cart Logic
- Mỗi User chỉ có duy nhất một Cart (Giỏ hàng) tại một thời điểm (One-to-One hoặc được định danh rõ ràng theo User ID).
- Cart chứa nhiều CartItem (Mỗi item là một sản phẩm/biến thể cụ thể cùng với số lượng).
- **Xử lý cộng dồn:** Khi thêm một sản phẩm đã tồn tại trong giỏ hàng (cùng Product_Variant), bắt buộc phải cộng dồn số lượng (`quantity`) thay vì tạo một dòng mới.

## 2. Order Logic
- **Snapshot Giá:** Khi User chốt đơn (Checkout), bắt buộc phải copy/lưu trữ giá hiện tại từ `Product_Variant` sang bảng `Order_Item`. Điều này đảm bảo giá đơn hàng không bị thay đổi nếu sau này giá sản phẩm trên hệ thống thay đổi.

## 3. Database Conventions
- Bảng Đơn hàng bắt buộc đặt tên là `Orders` (có chữ 's') hoặc `orders` để tránh xung đột với từ khóa `ORDER` trong SQL.
