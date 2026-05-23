# GEMINI CONTEXT: E-COMMERCE SHOES SYSTEM

Để giữ file gọn gàng, các quy tắc và thông tin dự án đã được chia nhỏ thành các module riêng biệt trong thư mục `.gemini/rules/`. Hãy tham khảo các file dưới đây để biết thêm chi tiết:

- **[Thông Tin Dự Án & Tech Stack](.gemini/rules/project-info.md)**: Chứa thông tin tổng quan dự án, số lượng thành viên, và các công nghệ sử dụng.
- **[Vai Trò Cá Nhân](.gemini/rules/my-role.md)**: Ghi rõ thông tin cá nhân và các module phụ trách (Cart & Order).
- **[Quy Tắc Cốt Lõi (Core Rules)](.gemini/rules/core-rules.md)**: Các ràng buộc về logic Giỏ hàng (Cộng dồn số lượng), logic Đơn hàng (Snapshot giá) và Quy ước Database.
- **[Trạng Thái Hiện Tại](.gemini/rules/current-status.md)**: Tiến độ hiện tại và các tác vụ đang cần tập trung giải quyết.
- **[Database Schema](.gemini/rules/database.md)**: Chứa cấu trúc các bảng và dữ liệu mẫu cho module Giỏ hàng và Đơn hàng.


## Guidelines
- Luôn kiểm tra file `.gemini/rules/` trước khi thực hiện task.
- Mọi API phải tuân thủ chuẩn RESTful.
- Code phải có comment tiếng Việt rõ ràng cho các logic phức tạp.
- Ưu tiên soft delete (`isActive = false`) thay vì xóa vật lý.
- Mọi danh sách phải hỗ trợ phân trang (`Page<T>`, `Pageable`).
- Validation messages, flash messages, labels viết bằng tiếng Việt.
- Đừng code quá phức tạp, code trong phạm vi là một đồ án đại học về những thứ cơ bản JAVA Spring Boot, không sử dụng những thư viện quá nâng cao.
- Tối ưu code, dễ đọc, dễ bảo trì, tuân theo nguyên tắc SOLID.
- Luôn dùng JPA & Spring Data JPA (Repository Pattern), không dùng JDBC thuần.
- Tuân thủ chặt chẽ Database Schema đã định nghĩa, không tự ý thay đổi cấu trúc bảng.
- Luôn sử dụng các Service và Repository có sẵn, không tự ý tạo Entity hoặc DTO mới nếu không cần thiết.