# Customer Service (Microservice)

Dự án Microservice quản lý khách hàng (Customer Service) được xây dựng theo kiến trúc nhiều tầng (Layered Architecture).

## Công nghệ sử dụng
- **Java**: JDK 17
- **Framework**: Spring Boot 4.0.8
- **Build Tool**: Gradle 9.7.1
- **Database**: PostgreSQL (chạy trên port 5432, database `customer_db`)
- **ORM**: Spring Data JPA / Hibernate
- **Security / Password Hashing**: Spring Security Crypto (`BCryptPasswordEncoder`)
- **Port**: `8081`

---

## Cấu trúc thư mục (Packages)
```
src/main/java/com/example/customerservice/
├── config/
│   └── SecurityConfig.java            # Cấu hình Bean PasswordEncoder (BCrypt)
├── controller/
│   └── CustomerController.java        # REST Controller (CRUD, Register, Login)
├── dto/
│   ├── CustomerRequestDTO.java        # DTO nhận dữ liệu đăng ký/cập nhật
│   ├── CustomerResponseDTO.java       # DTO trả về thông tin (không gồm password)
│   └── LoginRequestDTO.java           # DTO nhận email & password đăng nhập
├── entity/
│   └── Customer.java                  # JPA Entity ánh xạ bảng 'customers'
├── exception/
│   ├── ApiResponseError.java          # Cấu trúc lỗi chuẩn (timestamp, status, error, message)
│   ├── GlobalExceptionHandler.java    # Bắt exception tập trung (@RestControllerAdvice)
│   ├── InvalidCredentialsException.java
│   └── ResourceNotFoundException.java
├── repository/
│   └── CustomerRepository.java        # Kế thừa JpaRepository<Customer, Long>
├── service/
│   ├── CustomerService.java           # Interface định nghĩa nghiệp vụ
│   └── impl/
│       └── CustomerServiceImpl.java   # Triển khai nghiệp vụ CRUD, mã hóa password
└── CustomerServiceApplication.java    # Main Spring Boot Application
```

---

## Cấu hình Cơ sở dữ liệu (`application.properties`)
```properties
spring.application.name=customer-service
server.port=8081

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/customer_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

> **Lưu ý**: Trước khi chạy ứng dụng trên máy thật với PostgreSQL, hãy tạo database `customer_db`:
> ```sql
> CREATE DATABASE customer_db;
> ```

---

## Danh sách API & Hướng dẫn kiểm thử Postman

### 1. Đăng ký khách hàng (Mã hóa mật khẩu)
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/customers/register`
- **Headers**: `Content-Type: application/json`
- **Body (raw JSON)**:
  ```json
  {
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@gmail.com",
    "password": "Password123@"
  }
  ```
- **Response (201 Created)**:
  ```json
  {
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@gmail.com"
  }
  ```

### 2. Tìm kiếm khách hàng theo ID
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/customers/1`
- **Response (200 OK)**:
  ```json
  {
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@gmail.com"
  }
  ```
- **Trường hợp ID không tồn tại (VD: ID 99)**:
  - **URL**: `http://localhost:8081/api/v1/customers/99`
  - **Response (404 Not Found)**:
    ```json
    {
      "timestamp": "2026-10-11T20:15:30",
      "status": 404,
      "error": "Not Found",
      "message": "Khách hàng với ID 99 không tồn tại!"
    }
    ```

### 3. Đăng nhập
- **Method**: `PUT`
- **URL**: `http://localhost:8081/api/v1/customers/login`
- **Headers**: `Content-Type: application/json`
- **Body đúng**:
  ```json
  {
    "email": "nguyenvana@gmail.com",
    "password": "Password123@"
  }
  ```
- **Response thành công (200 OK)**:
  ```json
  {
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@gmail.com"
  }
  ```
- **Body sai (hoặc sai email/password)**:
  ```json
  {
    "email": "nguyenvana@gmail.com",
    "password": "sai_mat_khau"
  }
  ```
- **Response thất bại (400 Bad Request)**:
  ```json
  {
    "timestamp": "2026-10-11T20:15:30",
    "status": 400,
    "error": "Bad Request",
    "message": "email or password incorrect"
  }
  ```

### 4. Lấy tất cả khách hàng (CRUD)
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/customers`

### 5. Cập nhật thông tin khách hàng (CRUD)
- **Method**: `PUT`
- **URL**: `http://localhost:8081/api/v1/customers/1`

### 6. Xóa khách hàng (CRUD)
- **Method**: `DELETE`
- **URL**: `http://localhost:8081/api/v1/customers/1`

---

## Lệnh khởi chạy và kiểm thử

- **Chạy tests**:
  ```bash
  .\gradlew test
  ```
- **Khởi chạy ứng dụng**:
  ```bash
  .\gradlew bootRun
  ```
