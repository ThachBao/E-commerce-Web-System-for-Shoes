# 👟 E-commerce Web System for Shoes

<div align="center">

**Hệ thống Website Thương mại Điện tử Bán Giày**  
Dự án web bán giày được xây dựng bằng **Spring Boot**, **Thymeleaf** và **MySQL**.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-MVC-brightgreen)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![Thymeleaf](https://img.shields.io/badge/View-Thymeleaf-green)
![Maven](https://img.shields.io/badge/Build-Maven-red)

</div>

---

## 📌 Giới thiệu

**E-commerce Web System for Shoes** là hệ thống website thương mại điện tử phục vụ nhu cầu mua bán giày trực tuyến. Dự án hỗ trợ đầy đủ các nghiệp vụ cơ bản của một website bán hàng như đăng ký, đăng nhập, xem sản phẩm, lọc sản phẩm, quản lý giỏ hàng, đặt hàng, thanh toán, theo dõi đơn hàng và quản trị hệ thống.

Ngoài các chức năng thương mại điện tử truyền thống, hệ thống còn được mở rộng thêm chức năng **Chatbot AI hỗ trợ khách hàng**, giúp người dùng hỏi thông tin sản phẩm, tìm kiếm sản phẩm phù hợp và tra cứu thông tin đơn hàng nhanh hơn.

Dự án được tổ chức theo mô hình nhiều tầng:

```text
Controller → Service → Repository → MySQL Database
```

---

## 🎯 Mục tiêu dự án

- Xây dựng website bán giày trực tuyến có đầy đủ chức năng cơ bản.
- Hỗ trợ khách hàng tìm kiếm, xem chi tiết và đặt mua sản phẩm.
- Quản lý sản phẩm, danh mục, thương hiệu, size, màu sắc và tồn kho.
- Quản lý đơn hàng, thanh toán và lịch sử trạng thái đơn hàng.
- Xây dựng trang quản trị riêng cho admin.
- Bảo mật đăng nhập, phân quyền và quản lý tài khoản người dùng.
- Tích hợp gửi email phục vụ chức năng quên mật khẩu.
- Tích hợp Chatbot AI hỗ trợ tư vấn sản phẩm và chăm sóc khách hàng.

---

## ✨ Chức năng chính

### 👤 Người dùng

- Đăng ký tài khoản.
- Đăng nhập, đăng xuất.
- Quên mật khẩu và đặt lại mật khẩu qua email.
- Xem trang chủ và danh sách sản phẩm.
- Xem chi tiết sản phẩm.
- Tìm kiếm sản phẩm.
- Lọc sản phẩm theo danh mục, thương hiệu và thuộc tính sản phẩm.
- Thêm sản phẩm vào giỏ hàng.
- Cập nhật số lượng sản phẩm trong giỏ hàng.
- Xóa sản phẩm khỏi giỏ hàng.
- Đặt hàng và thanh toán.
- Xem lịch sử đơn hàng.
- Xem chi tiết đơn hàng.
- Quản lý thông tin cá nhân.

### 🛒 Giỏ hàng

- Thêm sản phẩm theo biến thể cụ thể.
- Cập nhật số lượng sản phẩm.
- Xóa sản phẩm khỏi giỏ hàng.
- Tự động tính tổng tiền.
- Kiểm tra tồn kho trước khi đặt hàng.

### 📦 Đơn hàng và thanh toán

- Tạo đơn hàng từ giỏ hàng.
- Lưu chi tiết sản phẩm trong đơn hàng.
- Quản lý thông tin thanh toán.
- Theo dõi trạng thái xử lý đơn hàng.
- Lưu lịch sử thay đổi trạng thái đơn hàng.
- Cho phép khách hàng xem lại lịch sử mua hàng.

### 🧑‍💼 Quản trị hệ thống

#### Quản lý sản phẩm

- Thêm sản phẩm mới.
- Cập nhật thông tin sản phẩm.
- Xóa sản phẩm.
- Quản lý hình ảnh sản phẩm.
- Quản lý biến thể sản phẩm theo size, màu sắc và số lượng tồn kho.

#### Quản lý danh mục

- Thêm danh mục.
- Sửa danh mục.
- Xóa danh mục.
- Hiển thị sản phẩm theo danh mục.

#### Quản lý thương hiệu

- Thêm thương hiệu.
- Sửa thương hiệu.
- Xóa thương hiệu.
- Quản lý logo thương hiệu.

#### Quản lý thuộc tính sản phẩm

- Quản lý size.
- Quản lý màu sắc.
- Sử dụng size và màu sắc để tạo biến thể sản phẩm.

#### Quản lý kho

- Theo dõi số lượng tồn kho theo từng biến thể sản phẩm.
- Quản lý giao dịch kho.
- Hỗ trợ kiểm soát số lượng trước khi khách hàng đặt hàng.

#### Quản lý đơn hàng

- Xem danh sách đơn hàng.
- Xem chi tiết đơn hàng.
- Cập nhật trạng thái đơn hàng.
- Theo dõi lịch sử trạng thái xử lý.

#### Quản lý người dùng

- Xem danh sách người dùng.
- Quản lý tài khoản khách hàng.
- Phân quyền người dùng.
- Khóa hoặc mở khóa tài khoản khi cần.

#### Thống kê doanh thu

- Trang dashboard quản trị.
- Theo dõi doanh thu.
- Hỗ trợ admin nắm tình hình hoạt động bán hàng.

### 🤖 Chatbot AI hỗ trợ khách hàng

- Giao diện chatbot ở phía người dùng.
- Nhận câu hỏi từ khách hàng.
- Phân tích ngữ cảnh hội thoại.
- Gợi ý sản phẩm phù hợp.
- Hỗ trợ tra cứu thông tin sản phẩm theo dữ liệu thời gian thực.
- Hỗ trợ tra cứu đơn hàng khi người dùng đã đăng nhập.
- Lưu hội thoại và tin nhắn chatbot.
- Tích hợp Cloudflare AI Search để tăng khả năng tìm kiếm và trả lời theo dữ liệu sản phẩm.

---

## 🏗️ Kiến trúc hệ thống

Dự án được xây dựng theo mô hình MVC kết hợp kiến trúc phân tầng:

```text
Client / Browser
      ↓
Controller
      ↓
Service
      ↓
Repository
      ↓
MySQL Database
```

### Các tầng chính

| Thành phần | Vai trò |
|---|---|
| Controller | Nhận request, điều hướng trang, trả về view hoặc response |
| Service | Xử lý nghiệp vụ chính của hệ thống |
| Repository | Làm việc với cơ sở dữ liệu thông qua Spring Data JPA |
| Entity | Ánh xạ bảng trong MySQL |
| DTO | Truyền dữ liệu giữa các tầng |
| Security | Xác thực, phân quyền và bảo vệ tài nguyên |
| Template | Giao diện Thymeleaf |
| Static Resource | Chứa CSS, JavaScript, hình ảnh |

---

## 🛠️ Công nghệ sử dụng

### Backend

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- JWT Authentication
- Validation
- ModelMapper
- Maven

### Frontend

- HTML5
- CSS3
- JavaScript
- Thymeleaf
- Bootstrap

### Database

- MySQL

### Tích hợp khác

- Gmail SMTP cho chức năng quên mật khẩu.
- Cloudflare AI Search cho chức năng chatbot AI.
- Upload hình ảnh sản phẩm.

---

## 🗄️ Cơ sở dữ liệu

Các entity chính trong hệ thống:

| Entity | Mô tả |
|---|---|
| AppUser | Thông tin tài khoản người dùng |
| Category | Danh mục sản phẩm |
| Brand | Thương hiệu sản phẩm |
| Product | Thông tin sản phẩm |
| ProductImage | Hình ảnh sản phẩm |
| ProductVariant | Biến thể sản phẩm theo size, màu sắc và tồn kho |
| Size | Kích thước giày |
| Color | Màu sắc sản phẩm |
| Cart | Giỏ hàng của người dùng |
| CartItem | Sản phẩm trong giỏ hàng |
| Order | Đơn hàng |
| OrderItem | Chi tiết đơn hàng |
| Payment | Thông tin thanh toán |
| InventoryTransaction | Lịch sử giao dịch kho |
| OrderStatusHistory | Lịch sử cập nhật trạng thái đơn hàng |
| PasswordResetToken | Token đặt lại mật khẩu |
| ChatConversation | Phiên hội thoại chatbot |
| ChatMessage | Tin nhắn trong hội thoại chatbot |
| ChatIntent | Ý định được chatbot nhận diện |

---

## 📁 Cấu trúc thư mục

```text
E-commerce-Web-for-Shoes
├── .mvn/wrapper
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com/CongNgheJave/ecommerce_system
│   │   │       ├── config
│   │   │       ├── controller
│   │   │       │   └── admin
│   │   │       ├── dto
│   │   │       │   ├── ai
│   │   │       │   ├── auth
│   │   │       │   ├── request
│   │   │       │   └── response
│   │   │       ├── entity
│   │   │       ├── exception
│   │   │       ├── repository
│   │   │       ├── security
│   │   │       ├── service
│   │   │       │   ├── ai
│   │   │       │   └── impl
│   │   │       ├── util
│   │   │       └── EcommerceSystemApplication.java
│   │   │
│   │   └── resources
│   │       ├── static
│   │       │   ├── css
│   │       │   ├── images
│   │       │   └── js
│   │       ├── templates
│   │       │   ├── admin
│   │       │   ├── auth
│   │       │   ├── cart
│   │       │   ├── category
│   │       │   ├── customer
│   │       │   ├── error
│   │       │   ├── fragments
│   │       │   ├── home
│   │       │   ├── order
│   │       │   └── product
│   │       └── application.properties
│   │
│   └── test
├── Database.txt
├── pom.xml
├── mvnw
└── mvnw.cmd
```

---

## 🔐 Bảo mật hệ thống

Hệ thống sử dụng Spring Security để bảo vệ các tài nguyên và phân quyền người dùng.

Các thành phần bảo mật chính:

- `SecurityConfig`: cấu hình bảo mật hệ thống.
- `CustomUserDetailsService`: tải thông tin người dùng phục vụ xác thực.
- `JwtService`: xử lý JWT token.
- `JwtAuthenticationFilter`: lọc và xác thực request bằng JWT.
- `CustomAuthenticationEntryPoint`: xử lý request chưa xác thực.
- `CustomAccessDeniedHandler`: xử lý request không đủ quyền truy cập.

Các cơ chế bảo mật:

- Mã hóa mật khẩu bằng BCrypt.
- Phân quyền theo vai trò người dùng.
- Bảo vệ trang quản trị.
- Xử lý đăng nhập, đăng xuất.
- Hỗ trợ quên mật khẩu qua email.

---

## ⚙️ Cấu hình môi trường

Không nên đẩy file `application.properties` thật lên GitHub vì file này thường chứa mật khẩu database, Gmail App Password và Cloudflare API Token.

Nên tạo file mẫu:

```text
src/main/resources/application-example.properties
```

Nội dung mẫu:

```properties
spring.application.name=ecommerce-system

spring.datasource.url=jdbc:mysql://localhost:3306/shoe_store_db?useUnicode=true&characterEncoding=UTF-8&character_set_server=utf8mb4
spring.datasource.username=root
spring.datasource.password=your_database_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.properties.hibernate.connection.characterEncoding=utf-8
spring.jpa.properties.hibernate.connection.useUnicode=true

server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_gmail_app_password
spring.mail.default-encoding=UTF-8
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.mime.charset=UTF-8

cloudflare.account-id=your_cloudflare_account_id
cloudflare.ai-search-instance=your_ai_search_instance
cloudflare.api-token=your_cloudflare_api_token

app.base-url=http://localhost:8080/

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
```

File `.gitignore` nên có:

```gitignore
target/
.idea/
uploads/
src/main/resources/application.properties
```

---

## 🚀 Hướng dẫn cài đặt và chạy dự án

### 1. Clone repository

```bash
git clone https://github.com/ThachBao/E-commerce-Web-System-for-Shoes.git
```

### 2. Di chuyển vào thư mục dự án

```bash
cd E-commerce-Web-System-for-Shoes
```

### 3. Tạo database MySQL

```sql
CREATE DATABASE shoe_store_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. Cấu hình `application.properties`

Tạo file:

```text
src/main/resources/application.properties
```

Có thể copy từ:

```text
src/main/resources/application-example.properties
```

Sau đó sửa các thông tin:

```properties
spring.datasource.username=root
spring.datasource.password=your_database_password
spring.mail.username=your_email@gmail.com
spring.mail.password=your_gmail_app_password
cloudflare.account-id=your_cloudflare_account_id
cloudflare.ai-search-instance=your_ai_search_instance
cloudflare.api-token=your_cloudflare_api_token
```

### 5. Cài đặt dependency

```bash
mvn clean install
```

Hoặc dùng Maven Wrapper:

```bash
./mvnw clean install
```

Trên Windows:

```bash
mvnw.cmd clean install
```

### 6. Chạy ứng dụng

```bash
mvn spring-boot:run
```

Hoặc:

```bash
./mvnw spring-boot:run
```

Trên Windows:

```bash
mvnw.cmd spring-boot:run
```

### 7. Truy cập website

```text
http://localhost:8080/
```

---

## 🌐 Một số trang giao diện chính

| Trang | Chức năng |
|---|---|
| Trang chủ | Hiển thị sản phẩm, danh mục, thương hiệu |
| Đăng nhập | Đăng nhập người dùng |
| Đăng ký | Tạo tài khoản mới |
| Quên mật khẩu | Gửi email đặt lại mật khẩu |
| Danh sách sản phẩm | Xem và lọc sản phẩm |
| Chi tiết sản phẩm | Xem thông tin chi tiết và chọn biến thể |
| Giỏ hàng | Quản lý sản phẩm đã chọn |
| Thanh toán | Đặt hàng và thanh toán |
| Lịch sử đơn hàng | Xem các đơn hàng đã mua |
| Trang quản trị | Quản lý toàn bộ hệ thống |
| Chatbot | Hỗ trợ tư vấn và tìm kiếm sản phẩm |

---

## 🧪 Kiểm thử

Chạy test bằng Maven:

```bash
mvn test
```

Hoặc:

```bash
./mvnw test
```

---

## 📌 Ghi chú khi deploy

Khi deploy lên VPS hoặc server thật, cần kiểm tra:

- Cấu hình MySQL trên server.
- Import dữ liệu ban đầu nếu có.
- Cập nhật `app.base-url` theo domain thật.
- Cấu hình Gmail App Password.
- Cấu hình Cloudflare AI Search nếu sử dụng chatbot.
- Đảm bảo thư mục upload có quyền ghi.
- Không public file chứa mật khẩu hoặc API token.

Ví dụ khi chạy bằng file `.jar`:

```bash
mvn clean package
java -jar target/*.jar
```

---

## 👨‍💻 Tác giả

| Họ tên | Vai trò |
|---|---|
| ThachBao | Developer |

---

## 🔮 Hướng phát triển

- Hoàn thiện giao diện responsive cho mobile.
- Tối ưu trải nghiệm mua hàng.
- Tích hợp thanh toán trực tuyến như VNPay hoặc MoMo.
- Nâng cấp chatbot AI để tư vấn sản phẩm tốt hơn.
- Gợi ý sản phẩm theo hành vi người dùng.
- Thống kê doanh thu nâng cao theo ngày, tháng, năm.
- Tối ưu bảo mật và phân quyền.
- Triển khai hệ thống bằng Docker.

---

## 📜 Giấy phép

Dự án được phát triển phục vụ mục đích học tập, nghiên cứu và thực hành xây dựng hệ thống thương mại điện tử bằng Spring Boot.

---

<div align="center">

### ⭐ E-commerce Web System for Shoes

</div>
