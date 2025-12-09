# University Social - JWT Authentication System

A complete JWT-based authentication system built with Spring Boot, featuring user registration, OTP email verification, login, and logout functionality.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [Configuration](#configuration)
- [Security Configuration](#security-configuration)
- [API Endpoints](#api-endpoints)
- [How It Works](#how-it-works)
- [Testing the API](#testing-the-api)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

This project implements a production-ready JWT (JSON Web Token) authentication system from scratch. It includes:

- User registration with email, password, and username
- OTP (One-Time Password) email verification
- JWT-based login and authentication
- Token blacklisting for secure logout
- Protected and public endpoints configuration
- Swagger API documentation

## ✨ Features

### Authentication Features
- ✅ User registration with validation
- ✅ Email verification via OTP (6-digit code, 10-minute expiration)
- ✅ Secure login with JWT token generation
- ✅ Logout with token blacklisting
- ✅ Password encryption using BCrypt
- ✅ JWT token validation and expiration

### Security Features
- ✅ Spring Security integration
- ✅ JWT filter for request authentication
- ✅ Token blacklist for logout
- ✅ Protected endpoints configuration
- ✅ Public endpoints for authentication
- ✅ CORS and CSRF configuration

## 🛠 Technology Stack

- **Java 17**
- **Spring Boot 4.0.0**
- **Spring Security 7.0.0**
- **Spring Data JPA**
- **PostgreSQL** (Database)
- **JWT (jjwt 0.11.5)** - Token generation and validation
- **Spring Mail** - Email sending for OTP
- **Swagger/OpenAPI 3** - API documentation
- **Lombok** - Boilerplate code reduction
- **Maven** - Dependency management

## 📁 Project Structure

```
src/main/java/com/example/universitysocial/
├── config/
│   ├── ApplicationConfiguration.java      # UserDetailsService bean
│   ├── JwtAuthenticationFilter.java       # JWT filter for request validation
│   ├── SecurityConfiguration.java         # Spring Security configuration
│   └── SwaggerConfig.java                 # Swagger/OpenAPI configuration
├── controller/
│   └── AuthController.java                # Authentication REST endpoints
├── dto/
│   ├── ApiResponse.java                   # Generic API response wrapper
│   ├── AuthenticationResponse.java        # Login response with JWT
│   ├── LoginRequest.java                  # Login request DTO
│   ├── RegisterRequest.java               # Registration request DTO
│   └── VerifyOtpRequest.java              # OTP verification DTO
├── entity/
│   ├── TokenBlacklist.java                # Blacklisted tokens entity
│   └── User.java                          # User entity
├── exception/
│   └── GlobalExceptionHandler.java        # Global exception handler
├── repository/
│   ├── TokenBlacklistRepository.java      # Token blacklist data access
│   └── UserRepository.java                # User data access
└── service/
    ├── AuthenticationService.java         # Main authentication logic
    ├── EmailService.java                  # Email sending service
    ├── JWTService.java                    # JWT token operations
    └── OTPService.java                    # OTP generation and validation
```

## 🚀 Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database
- Gmail account (for email sending)

### Step 1: Clone and Navigate

```bash
cd university-social
```

### Step 2: Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE university_social;
```

2. Update `application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/university_social
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Step 3: Generate JWT Secret Key

Generate a secure 256-bit base64-encoded key:

```bash
openssl rand -base64 32
```

This will output something like: `UyYfJQmrTIOPRPbmprjIaAa8Vw07DaaglOXvJEZs3Ww=`

### Step 4: Configure Environment Variables

Create a `.env` file in the project root:

```properties
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/university_social
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# JWT Secret Key (use the key generated in Step 3)
JWT_SECRET_KEY=UyYfJQmrTIOPRPbmprjIaAa8Vw07DaaglOXvJEZs3Ww=

# Email Configuration (Gmail)
SUPPORT_EMAIL=your-email@gmail.com
APP_PASSWORD=your-gmail-app-password
```

**Note:** For Gmail, you need to:
1. Enable 2-Factor Authentication
2. Generate an App Password: https://myaccount.google.com/apppasswords
3. Use the app password (not your regular password)

### Step 5: Update application.properties

The `application.properties` file should reference these environment variables:

```properties
# JWT Configuration
security.jwt.secret-key=${JWT_SECRET_KEY:default-key-here}
security.jwt.expiration-time=3600000  # 1 hour in milliseconds

# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${SUPPORT_EMAIL}
spring.mail.password=${APP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Step 6: Run the Application

```bash
./mvnw spring-boot:run
```

Or using Maven:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## ⚙️ Configuration

### JWT Configuration

**Location:** `application.properties`

```properties
# JWT secret key (256-bit base64 encoded)
security.jwt.secret-key=${JWT_SECRET_KEY:your-key-here}

# Token expiration time in milliseconds (default: 1 hour)
security.jwt.expiration-time=3600000
```

**Key Requirements:**
- Must be base64-encoded
- Must be at least 256 bits (32 bytes) after decoding
- Generate using: `openssl rand -base64 32`

### Email Configuration

**Location:** `application.properties`

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${SUPPORT_EMAIL}
spring.mail.password=${APP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Database Configuration

**Location:** `application.properties`

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 🔒 Security Configuration

### SecurityConfiguration.java

This is the main security configuration file that defines:

1. **Public Endpoints** (No authentication required)
2. **Protected Endpoints** (JWT token required)
3. **JWT Filter** integration
4. **Session Management** (Stateless)
5. **CSRF** (Disabled for stateless API)

### Public Endpoints

The following endpoints are **publicly accessible** (no JWT required):

```java
.requestMatchers(
    "/api/v1/auth/**",           // All authentication endpoints
    "/swagger-ui/**",            // Swagger UI
    "/v3/api-docs/**",           // OpenAPI documentation
    "/swagger-ui.html",
    "/swagger-resources/**",
    "/webjars/**"
).permitAll()
```

### Protected Endpoints

All other endpoints require a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

### How to Add/Remove Public Endpoints

Edit `SecurityConfiguration.java`:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // Add public endpoints here
            .requestMatchers(
                "/api/v1/auth/**",
                "/api/v1/public/**",        // Example: Add public API
                "/swagger-ui/**"
            ).permitAll()
            
            // All other endpoints require authentication
            .anyRequest().authenticated()
        )
        // ... rest of configuration
}
```

### JWT Filter Flow

1. **Request arrives** → `JwtAuthenticationFilter`
2. **Check Authorization header** → Extract Bearer token
3. **Validate token** → Check if blacklisted, expired, or invalid
4. **Load user** → Get user details from database
5. **Set authentication** → Add to SecurityContext
6. **Continue filter chain** → Process request

### Token Blacklist

When a user logs out, the token is added to the `token_blacklist` table. The JWT filter checks this table before validating tokens, ensuring logged-out tokens cannot be reused.

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api/v1/auth
```

### 1. Register User

**Endpoint:** `POST /api/v1/auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "Password123",
  "username": "johndoe"
}
```

**Validation Rules:**
- Email: Must be valid email format
- Password: 
  - Minimum 8 characters
  - At least one uppercase letter
  - At least one lowercase letter
  - At least one number
- Username: 
  - 3-50 characters
  - Only letters, numbers, and underscores

**Response:**
```json
{
  "message": "Registration successful. Please check your email for OTP verification code.",
  "data": null,
  "success": true
}
```

### 2. Verify OTP

**Endpoint:** `POST /api/v1/auth/verify-otp`

**Request Body:**
```json
{
  "email": "user@example.com",
  "otpCode": "123456"
}
```

**Response:**
```json
{
  "message": "Email verified successfully. You can now login.",
  "data": null,
  "success": true
}
```

**Note:** OTP expires after 10 minutes.

### 3. Login

**Endpoint:** `POST /api/v1/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "Password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "message": "Login successful"
}
```

### 4. Logout

**Endpoint:** `POST /api/v1/auth/logout`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "message": "Logout successful",
  "data": null,
  "success": true
}
```

**Note:** After logout, the token is blacklisted and cannot be used again.

## 🔄 How It Works

### Registration Flow

```
1. User submits registration form
   ↓
2. System validates input (email, password, username)
   ↓
3. Check if email/username already exists
   ↓
4. Generate 6-digit OTP
   ↓
5. Hash password with BCrypt
   ↓
6. Save user to database (enabled = false)
   ↓
7. Send OTP via email
   ↓
8. Return success message
```

### OTP Verification Flow

```
1. User submits email and OTP code
   ↓
2. Find user by email
   ↓
3. Validate OTP (check code and expiration)
   ↓
4. Set user.enabled = true
   ↓
5. Clear OTP fields
   ↓
6. Return success message
```

### Login Flow

```
1. User submits email and password
   ↓
2. Check if user exists and is enabled
   ↓
3. Authenticate using Spring Security
   ↓
4. Generate JWT token (contains user email as subject)
   ↓
5. Return token to client
```

### Request Authentication Flow

```
1. Client sends request with Authorization header
   ↓
2. JwtAuthenticationFilter intercepts request
   ↓
3. Extract token from "Bearer <token>"
   ↓
4. Check if token is blacklisted
   ↓
5. Extract username (email) from token
   ↓
6. Load user from database
   ↓
7. Validate token (signature, expiration)
   ↓
8. Set authentication in SecurityContext
   ↓
9. Continue to controller
```

### Logout Flow

```
1. Client sends logout request with token
   ↓
2. Extract token from Authorization header
   ↓
3. Get token expiration date
   ↓
4. Save token to blacklist table
   ↓
5. Return success message
```

## 🧪 Testing the API

### Using Swagger UI

1. Start the application
2. Navigate to: `http://localhost:8080/swagger-ui.html`
3. Use the interactive API documentation to test endpoints

### Using cURL

#### 1. Register
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123",
    "username": "testuser"
  }'
```

#### 2. Verify OTP
```bash
curl -X POST http://localhost:8080/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otpCode": "123456"
  }'
```

#### 3. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123"
  }'
```

Save the token from the response.

#### 4. Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/v1/user/profile \
  -H "Authorization: Bearer <your-jwt-token>"
```

#### 5. Logout
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Using Postman

1. **Register:** POST to `/api/v1/auth/register` with JSON body
2. **Verify OTP:** POST to `/api/v1/auth/verify-otp` with JSON body
3. **Login:** POST to `/api/v1/auth/login` with JSON body
4. **Use Token:** Add to Headers: `Authorization: Bearer <token>`
5. **Logout:** POST to `/api/v1/auth/logout` with Authorization header

## 🐛 Troubleshooting

### Common Issues

#### 1. JWT Secret Key Error

**Error:** `Invalid JWT secret key` or `Illegal base64 character`

**Solution:**
- Ensure JWT secret key is base64-encoded
- Generate new key: `openssl rand -base64 32`
- Update `.env` file: `JWT_SECRET_KEY=your-new-key`
- Restart application

#### 2. Email Not Sending

**Error:** Email not received or SMTP error

**Solution:**
- Verify Gmail credentials in `.env`
- Use App Password (not regular password)
- Check Gmail 2FA is enabled
- Verify SMTP settings in `application.properties`

#### 3. Circular Dependency Error

**Error:** `The dependencies of some of the beans in the application context form a cycle`

**Solution:**
- Already fixed in `JwtAuthenticationFilter` - uses `TokenBlacklistRepository` directly
- If you see this, ensure you're using the latest code

#### 4. Database Connection Error

**Error:** `Unable to determine Dialect without JDBC metadata`

**Solution:**
- Check database is running
- Verify connection URL, username, password in `.env`
- Ensure database exists: `CREATE DATABASE university_social;`

#### 5. Token Expired

**Error:** `JWT expired` or `Token expired`

**Solution:**
- Token expiration is set to 1 hour (3600000 ms)
- User needs to login again to get new token
- To change expiration, update `security.jwt.expiration-time` in `application.properties`

#### 6. User Not Enabled

**Error:** `Please verify your email first`

**Solution:**
- User must verify email with OTP before login
- Check email for OTP code
- OTP expires after 10 minutes

## 📚 Key Components Explained

### JWTService

Handles all JWT operations:
- Token generation
- Token validation
- Token expiration checking
- Username extraction from token

**Key Methods:**
- `generateToken(UserDetails)` - Create JWT token
- `isTokenValid(String, UserDetails)` - Validate token
- `extractUsernameFromToken(String)` - Get email from token
- `isTokenExpired(String)` - Check if token expired

### AuthenticationService

Main business logic for authentication:
- User registration
- OTP verification
- Login
- Logout (token blacklisting)

### JwtAuthenticationFilter

Spring Security filter that:
- Intercepts all requests
- Extracts JWT from Authorization header
- Validates token
- Sets authentication in SecurityContext

### SecurityConfiguration

Configures Spring Security:
- Defines public/protected endpoints
- Sets up JWT filter
- Configures authentication provider
- Disables CSRF for stateless API

## 🔐 Security Best Practices

1. **Never commit secrets** - Use `.env` file and add to `.gitignore`
2. **Use strong JWT keys** - Minimum 256 bits
3. **Set appropriate expiration** - 1 hour is recommended
4. **Use HTTPS in production** - Never send tokens over HTTP
5. **Validate all inputs** - Use `@Valid` annotations
6. **Hash passwords** - Always use BCrypt
7. **Blacklist tokens on logout** - Prevent token reuse
8. **Rate limiting** - Consider adding rate limiting for login/register

## 📝 Environment Variables Summary

Required environment variables (in `.env` file):

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/university_social
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# JWT
JWT_SECRET_KEY=your-base64-encoded-256-bit-key

# Email
SUPPORT_EMAIL=your-email@gmail.com
APP_PASSWORD=your-gmail-app-password
```

## 🎓 Learning Resources

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [JWT.io](https://jwt.io/) - JWT debugger and information
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [BCrypt Password Hashing](https://en.wikipedia.org/wiki/Bcrypt)

## 📄 License

This project is for educational purposes.

## 👥 Contributing

Feel free to submit issues and enhancement requests!

---

**Built with ❤️ using Spring Boot and JWT**

