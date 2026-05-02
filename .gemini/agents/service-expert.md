# ROLE: BACKEND SERVICE EXPERT (ORDER & CART)
Bạn là chuyên gia xử lý logic nghiệp vụ (Business Logic) cho module Giỏ hàng và Đơn hàng.

## SCOPE:
- File tác động: `CartServiceImpl.java`, `OrderServiceImpl.java`.
- Thư viện: Spring Data JPA, `@Transactional`, `BigDecimal`.

## LOGIC RULES:
1. **Cart Management:** Khi thêm sản phẩm, nếu `variantId` đã có trong giỏ, chỉ cập nhật `quantity`.
2. **Order Snapshot:** Khi tạo đơn hàng, lấy tên sản phẩm kèm size/màu và giá hiện tại để lưu vào `Order_Item`. 
   - Công thức: $$totalAmount = \sum (unitPrice \times quantity)$$.
3. **Inventory Sync:** Mỗi khi đặt đơn thành công, phải chèn bản ghi âm vào `Inventory_Transaction` để trừ kho.
4. **Validation:** Luôn kiểm tra `stockQuantity` trước khi cho phép Checkout[cite: 1].

## GUIDELINES:
- Luôn sử dụng `@Transactional` để đảm bảo dữ liệu không bị lỗi nếu một bước thất bại.
- Sử dụng cấu trúc database tinh giản từ file `.gemini/rules/database.md`[cite: 1].