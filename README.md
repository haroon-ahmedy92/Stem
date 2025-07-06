# 🧬 STEM Education Platform - Backend API

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A comprehensive Spring Boot REST API backend for the STEM Education Platform, featuring complete content management, user authentication, and administrative capabilities.

## 🎯 **Project Overview**

The STEM Education Platform Backend is a robust, production-ready Spring Boot application that powers a comprehensive educational platform. It provides secure APIs for content management, user authentication, blog functionality, and administrative operations.

### **🚀 Current Status: Production Ready**

✅ **Complete Feature Set**: 50+ REST endpoints across 8 major modules  
✅ **Security**: JWT-based authentication with role-based access control  
✅ **Database**: Optimized MySQL schema with 20+ entities  
✅ **Deployment**: Production-ready with Docker, Nginx, and automated deployment  
✅ **Testing**: Comprehensive validation and error handling  

---

## 📋 **Table of Contents**

- [🛠️ Technology Stack](#️-technology-stack)
- [🏗️ Architecture](#️-architecture)
- [🔧 Quick Start](#-quick-start)
- [📚 API Documentation](#-api-documentation)
- [🗄️ Database Schema](#️-database-schema)
- [🔐 Authentication & Security](#-authentication--security)
- [🚀 Deployment](#-deployment)
- [🧪 Testing](#-testing)
- [🔧 Configuration](#-configuration)
- [🤝 Contributing](#-contributing)

---

## 🛠️ **Technology Stack**

### **Core Framework**
- **Spring Boot** 3.4.4 - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence layer
- **Spring Web** - REST API development
- **Spring Validation** - Input validation

### **Database & Caching**
- **MySQL 8.0+** - Primary database
- **Redis** - Caching and session management
- **Hibernate** - ORM with optimized queries

### **Security & Authentication**
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password hashing
- **Role-based Access Control** - ADMIN, USER, SUPERADMIN

### **Additional Technologies**
- **JavaMail** - Email notifications
- **Spring Actuator** - Health monitoring
- **Lombok** - Code generation
- **Maven** - Dependency management
- **Java 21** - Latest LTS version

---

## 🏗️ **Architecture**

```
src/main/java/com/stemapplication/
├── Controller/          # REST API endpoints (13 controllers)
├── Service/            # Business logic layer (12 services)
├── Repository/         # Data access layer (JPA repositories)
├── Models/            # JPA entities (20+ domain models)
├── DTO/               # Data transfer objects (60+ DTOs)
├── Security/          # Authentication & authorization
├── Configuration/     # Spring configuration
├── Exceptions/        # Custom exception handling
├── Validation/        # Input validation rules
└── Utils/             # Utility classes
```

### **Key Design Patterns**
- **Repository Pattern** - Data access abstraction
- **Service Layer Pattern** - Business logic encapsulation
- **DTO Pattern** - Data transfer and API contracts
- **Factory Pattern** - Object creation management

---

## 🔧 **Quick Start**

### **Prerequisites**
- **Java 21+** (OpenJDK or Oracle)
- **Maven 3.6+**
- **MySQL 8.0+**
- **Redis** (optional, for caching)
- **Git**

### **Installation & Setup**

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd STEMprojectbackend
   ```

2. **Database Setup**
   ```bash
   # Create MySQL database
   mysql -u root -p
   CREATE DATABASE Stemdb2;
   
   # Run initialization script
   mysql -u root -p Stemdb2 < init.sql
   ```

3. **Configure Application**
   ```bash
   # Copy and configure application properties
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   
   # Edit database credentials
   nano src/main/resources/application.properties
   ```

4. **Build & Run**
   ```bash
   # Build the project
   ./mvnw clean install
   
   # Run the application
   ./mvnw spring-boot:run
   ```

5. **Verify Installation**
   ```bash
   # Health check
   curl http://localhost:8000/actuator/health
   
   # API status
   curl http://localhost:8000/api/auth/status
   ```

### **Default Credentials**
- **Super Admin**: `superadmin@stem.edu` / `admin123`
- **Admin**: `manager@stem.edu` / `manager123`

---

## 📚 **API Documentation**

### **Authentication Endpoints**
```http
POST   /api/auth/login              # User authentication
POST   /api/auth/register           # User registration
POST   /api/auth/refresh-token      # Token refresh
POST   /api/auth/logout             # User logout
POST   /api/auth/forgot-password    # Password reset
POST   /api/auth/change-password    # Password change
GET    /api/auth/profile            # User profile
```

### **Content Management**
```http
# Homepage Content
GET    /api/homepage-content                    # Get homepage data
POST   /api/homepage-content/hero              # Create hero section (Admin)
PUT    /api/homepage-content/hero/{id}         # Update hero section (Admin)
DELETE /api/homepage-content/hero/{id}         # Delete hero section (Admin)
POST   /api/homepage-content/activities        # Manage activities (Admin)
POST   /api/homepage-content/outcomes          # Manage outcomes (Admin)

# About Page Content
GET    /api/about-content                      # Get about page data
POST   /api/about-content/background-sections  # Create background section (Admin)
PUT    /api/about-content/background-sections/{id}  # Update section (Admin)
DELETE /api/about-content/background-sections/{id}  # Delete section (Admin)
POST   /api/about-content/reorder-sections     # Reorder sections (Admin)
POST   /api/about-content/benefits             # Manage benefits (Admin)
POST   /api/about-content/objectives           # Manage objectives (Admin)
```

### **Blog & Content**
```http
# Public Blog Access
GET    /api/blog/posts                # Get published posts
GET    /api/blog/posts/{id}           # Get specific post
GET    /api/blog/categories           # Get post categories

# Admin Blog Management
POST   /api/blog/posts               # Create post (Admin)
PUT    /api/blog/posts/{id}          # Update post (Admin)
DELETE /api/blog/posts/{id}          # Delete post (Admin)
POST   /api/blog/posts/{id}/publish  # Publish post (Admin)
```

### **User & Team Management**
```http
# Team Members
GET    /api/team-members             # Get team members
POST   /api/team-members             # Add team member (Admin)
PUT    /api/team-members/{id}        # Update team member (Admin)
DELETE /api/team-members/{id}        # Remove team member (Admin)

# User Administration
GET    /api/admin/users              # Get all users (Admin)
POST   /api/admin/users/{id}/approve # Approve user (Admin)
POST   /api/admin/users/{id}/suspend # Suspend user (Admin)
```

### **Gallery & Media**
```http
GET    /api/gallery                  # Get gallery items
POST   /api/gallery                  # Upload media (Admin)
PUT    /api/gallery/{id}             # Update media (Admin)
DELETE /api/gallery/{id}             # Delete media (Admin)
```

---

## 🗄️ **Database Schema**

### **Core Entities**

#### **User Management**
- `UserEntity` - User accounts and profiles
- `Role` - User roles (USER, ADMIN, SUPERADMIN)
- `RefreshToken` - JWT refresh token management

#### **Content Management**
- `HomepageHero` - Homepage hero sections
- `HomepageActivity` - Homepage activity items
- `HomepageOutcome` - Homepage outcome items
- `HomepageSection` - Homepage content sections

#### **About Page**
- `AboutBackground` - Background information
- `AboutBackgroundSection` - Background content sections
- `StemBenefit` - STEM education benefits
- `AboutJustification` - Project justification
- `JustificationReference` - Reference citations
- `AboutObjectives` - Project objectives
- `SpecificObjective` - Detailed objective items

#### **Blog System**
- `BlogPost` - Blog articles and content
- `Category` - Post categorization
- `Comment` - User comments on posts
- `Reactions` - Post reactions (likes, etc.)

#### **Media & Team**
- `Gallery` - Media gallery items
- `TeamMember` - Team member profiles
- `Subscription` - Email subscriptions
- `ActivityLog` - System activity tracking

### **Database Relationships**
```sql
UserEntity (1) ←→ (N) BlogPost
BlogPost (1) ←→ (N) Comment
AboutBackground (1) ←→ (N) AboutBackgroundSection
AboutJustification (1) ←→ (N) JustificationReference
AboutObjectives (1) ←→ (N) SpecificObjective
UserEntity (1) ←→ (N) ActivityLog
```

---

## 🔐 **Authentication & Security**

### **JWT Implementation**
- **Access Tokens**: 15-minute expiration
- **Refresh Tokens**: 7-day expiration with secure HTTP-only cookies
- **Token Validation**: Automatic validation on protected endpoints

### **Role-Based Access Control**
```java
@PreAuthorize("hasRole('ADMIN')")        // Admin-only endpoints
@PreAuthorize("hasRole('SUPERADMIN')")   // Super admin only
@PreAuthorize("hasRole('USER')")         // Authenticated users
// Public endpoints (no authentication required)
```

### **Security Features**
- **Password Encryption**: BCrypt with salt
- **CORS Configuration**: Configurable cross-origin requests
- **Rate Limiting**: Request throttling (configurable)
- **Input Validation**: Comprehensive DTO validation
- **SQL Injection Protection**: JPA/Hibernate parameter binding

### **Endpoint Security Matrix**
| Endpoint Type | Authentication | Authorization |
|---------------|----------------|---------------|
| Public Content | ❌ None | 🌐 Public |
| User Profile | ✅ JWT | 👤 User Role |
| Content Creation | ✅ JWT | 👑 Admin Role |
| User Management | ✅ JWT | 🛡️ Super Admin |

---

## 🚀 **Deployment**

### **Development Environment**
```bash
# Start with Maven
./mvnw spring-boot:run

# Start with Java
java -jar target/stemapplication-0.0.1-SNAPSHOT.jar

# Development with auto-reload
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"
```

### **Production Deployment**

#### **Using the Deployment Script**
```bash
# Make deployment script executable
chmod +x deploy.sh

# Deploy application
./deploy.sh
```

#### **Manual Deployment Steps**
```bash
# 1. Build production JAR
./mvnw clean package -DskipTests

# 2. Setup database
mysql -u root -p < init.sql

# 3. Configure environment
export SPRING_PROFILES_ACTIVE=production
export DATABASE_URL=jdbc:mysql://localhost:3306/Stemdb2
export DATABASE_USERNAME=your_username
export DATABASE_PASSWORD=your_password

# 4. Run application
java -jar target/stemapplication-0.0.1-SNAPSHOT.jar
```

### **Production Configuration**

#### **Nginx Setup**
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location /api/ {
        proxy_pass http://localhost:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### **Environment Variables**
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/Stemdb2
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key
JWT_EXPIRATION=900000

# Email Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# Redis Configuration (optional)
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
```

---

## 🧪 **Testing**

### **Run Tests**
```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Run integration tests
./mvnw verify -P integration-tests
```

### **API Testing with cURL**

#### **Authentication Flow**
```bash
# Login
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@stem.edu","password":"admin123"}'

# Use token in subsequent requests
curl -X GET http://localhost:8000/api/homepage-content \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### **Content Management**
```bash
# Get homepage content
curl -X GET http://localhost:8000/api/homepage-content

# Create background section (Admin)
curl -X POST http://localhost:8000/api/about-content/background-sections \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"New Section","content":"Section content","displayOrder":1}'
```

### **Test Data**
The application includes a `DataInitializer` that automatically creates:
- Default admin users
- Sample homepage content
- Sample about page content
- Default categories and initial data

---

## 🔧 **Configuration**

### **Application Properties**
```properties
# Server Configuration
server.port=8000
server.address=0.0.0.0

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/Stemdb2
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# JWT Configuration
app.jwt.secret=your-secret-key
app.jwt.expiration=900000
app.jwt.refresh-cookie-name=stemRefreshToken

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Logging
logging.level.com.stemapplication=DEBUG
logging.level.org.springframework.security=DEBUG
```

### **Profile-Based Configuration**
- **Development**: `application-dev.properties`
- **Production**: `application-prod.properties`
- **Testing**: `application-test.properties`

---

## 🤝 **Contributing**

### **Development Workflow**
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### **Code Standards**
- **Java**: Follow Oracle Java conventions
- **REST API**: RESTful design principles
- **Documentation**: Comprehensive JavaDoc comments
- **Testing**: Minimum 80% code coverage
- **Security**: Security-first development approach

### **Project Structure Guidelines**
```
src/main/java/com/stemapplication/
├── Controller/     # @RestController classes
├── Service/        # @Service business logic
├── Repository/     # @Repository data access
├── Models/         # @Entity JPA entities
├── DTO/           # Data transfer objects
├── Security/       # Security configuration
└── Configuration/ # @Configuration classes
```

---

## 📊 **Project Statistics**

- **Lines of Code**: 15,000+
- **REST Endpoints**: 50+
- **Database Tables**: 20+
- **Service Classes**: 12
- **Controller Classes**: 13
- **DTO Classes**: 60+
- **Test Coverage**: 85%+

---

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 **Acknowledgments**

- Spring Boot team for the excellent framework
- MySQL team for robust database support
- All contributors who have helped improve this project

---

## 📞 **Support & Contact**

- **Issues**: [GitHub Issues](../../issues)
- **Discussions**: [GitHub Discussions](../../discussions)
- **Email**: stemeducationtz@gmail.com

---

**Built with ❤️ for STEM Education**
