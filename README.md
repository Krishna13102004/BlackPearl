# ⚓ Black Pearl – Shipyard Management System

**Maritime Excellence | Shipbuilding | Repair | Inventory Optimization**

![Java](https://img.shields.io/badge/Java-17-ED8936?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![JWT](https://img.shields.io/badge/JWT-Security-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)

---

## 🌊 Project Overview

**Black Pearl** is an enterprise-grade, full-stack maritime operations management platform designed to streamline shipyard workflows — from vessel construction and repair to inventory control, payment processing, and tender lifecycle management.

### ✨ Key Highlights
- **Enterprise Architecture**: Service-Oriented Design with Spring Boot 3, Java 17
- **Role-Based Access Control (RBAC)**: Granular permissions for Users & Admins
- **Secure Authentication**: JWT-based token authentication with Spring Security
- **Real-time Inventory Management**: Automatic stock deduction during exports
- **Payment Tracking**: Full payment lifecycle management
- **Notification System**: Real-time alerts and broadcasts
- **Responsive Design**: Modern glassmorphism UI with vanilla JavaScript
- **Production-Ready**: Proper error handling, validation, and logging

---

## 📚 Table of Contents

* [Project Overview](#-project-overview)
* [System Features](#-system-features)
* [Architecture & Design](#-architecture--design)
* [Technology Stack](#-technology-stack)
* [Database Schema](#-database-schema)
* [Project Structure](#-project-structure)
* [API Endpoints](#-api-endpoints)
* [Code Analysis & Module Verification](#-code-analysis--module-verification-feb-2026)
* [Frontend Modules](#-frontend-modules)
* [Installation & Setup](#-installation--setup)
* [Demo Credentials](#-demo-credentials)
* [Running the Application](#-running-the-application)
* [How It Works](#-how-it-works)
* [Use Cases](#-use-cases)
* [Module Documentation](#-module-documentation)
* [Troubleshooting](#-troubleshooting)
* [Learning Outcomes](#-learning-outcomes)
* [Future Enhancements](#-future-enhancements)
* [License](#-license)

---


---

## ✨ System Features

### 👤 User Module
- ✅ Register with role selection (USER/ADMIN)
- ✅ Secure login with JWT authentication
- ✅ Personal dashboard with activity metrics
- ✅ View and bid on open tenders
- ✅ Submit vessel construction orders
- ✅ Log repair issues with priority levels
- ✅ Track stock exports and approvals
- ✅ View payment history
- ✅ Receive real-time notifications

### 🛡️ Admin Module
- ✅ Full user management (activate/deactivate accounts)
- ✅ Complete inventory CRUD operations
- ✅ Smart stock export processing with automatic deduction
- ✅ Tender creation and lifecycle management (OPEN → CLOSED → AWARDED)
- ✅ Ship order approval/rejection workflow
- ✅ Repair request status updates
- ✅ Payment verification and status updates
- ✅ System-wide analytics dashboard
- ✅ Notification broadcasting to users

### 🔐 Security & RBAC Features
- ✅ **Strict RBAC**: Enforced role-based access for all modules (ADMIN, ENGINEERING, OPERATIONS, etc.)
- ✅ **Role Hierarchy**: ADMIN role inherits all lower-level permissions automatically
- ✅ **JWT Claims**: Enhanced tokens containing `userId`, `role`, and `department` for stateless authorization
- ✅ **Ownership Validation**: Backend logic ensures users only access their own data unless they are ADMIN
- ✅ **Dynamic UI Rendering**: Sidebar and dashboard modules filtered based on user's department
- ✅ **Polling & Real-time Sync**: 30s background data polling for stats and notifications
- ✅ **Auto-Logout on 403**: Seamless redirection to login on unauthorized access
- ✅ **BCrypt Hashing**: All passwords securely hashed using BCrypt
- ✅ **Method Security**: Controllers protected with `@PreAuthorize` annotation
- ✅ **Audit Logging**: Tracking of administrative approvals (`approved_by`, `approved_at`)

---

## 🏗️ Architecture & Design

### Architectural Pattern: Service-Oriented Architecture (SOA)

```
┌─────────────────────────┐
│   Frontend (Browser)    │  HTML5, CSS3, Vanilla JS
│  (13 Pages + API Layer) │
└────────────┬────────────┘
             │ HTTP/REST (JSON)
             │ Bearer Token (JWT)
             ↓
┌─────────────────────────────────────────┐
│      Spring Boot REST Layer             │  11 Controllers
│      (@RestController, @RequestMapping) │
└────────────┬────────────────────────────┘
             │ Request Processing
             ↓
┌─────────────────────────────────────────┐
│      Service Layer (Business Logic)     │  10 Services
│      (@Service, @Transactional)         │  Validation & Rules
└────────────┬────────────────────────────┘
             │ Data Access
             ↓
┌─────────────────────────────────────────┐
│      Repository Layer (Data Access)     │  8 Repositories
│      (@Repository, Spring Data JPA)     │  ORM Mapping
└────────────┬────────────────────────────┘
             │ SQL Queries
             ↓
┌─────────────────────────────────────────┐
│      MySQL Database                     │  8 Tables
│      (Relational Data Storage)          │  Proper Constraints
└─────────────────────────────────────────┘
```

### Security Architecture

```
HTTP Request
    ↓
CORS Filter (configured for frontend)
    ↓
Spring Security Filter Chain
    ↓
JWT Authentication Filter (JwtAuthenticationFilter)
    ↓
Token Validation (JwtUtil)
    ↓
UserDetailsService (Custom implementation)
    ↓
Authentication Manager
    ↓
Authorization (@PreAuthorize, @RolesAllowed)
    ↓
Secured Endpoint
```

---

## 💻 Technology Stack

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Language** | Java | 17 LTS | Core programming |
| **Framework** | Spring Boot | 3.2.3 | REST API framework |
| **Security** | Spring Security | 6.x | Authentication & Authorization |
| **Authentication** | jjwt | 0.11.5 | JWT token generation/validation |
| **ORM** | Spring Data JPA | - | Database abstraction |
| **Database** | MySQL | 8.0+ | Relational data storage |
| **Password** | BCrypt | - | Secure password hashing |
| **Utilities** | Lombok | - | Boilerplate code reduction |
| **Frontend** | HTML5/CSS3/JS | ES6+ | Presentation layer |
| **HTTP Client** | Fetch API | - | Frontend-backend communication |

---

## 🗄️ Database Schema

### Database: `blackpearl_db`

#### 1. **users** Table
```sql
Stores user accounts with role-based distinction
- id: Primary Key (Auto-increment)
- first_name, last_name: User identity
- email: Unique identifier for login
- password: BCrypt hashed
- role: ENUM('USER', 'ADMIN')
- department: VARCHAR (nullable)
- active: Boolean flag for account status
- created_at, updated_at: Timestamps
```

#### 2. **tenders** Table
```sql
Manages shipyard service tenders and bids
- id: Primary Key
- tender_no: Unique reference number
- title, description: Tender details
- value: DECIMAL(15,2) - Amount
- published_date, closing_date: Timeline
- status: ENUM('OPEN', 'CLOSING_SOON', 'CLOSED', 'AWARDED')
- category: VARCHAR (materials, services, equipment)
```

#### 3. **ship_orders** Table
```sql
Stores vessel construction requests
- id: Primary Key
- user_id: FOREIGN KEY → users(id)
- ship_type, tonnage, material: Specifications
- expected_delivery: DATE
- status: ENUM('PENDING', 'APPROVED', 'IN_PROGRESS', 'COMPLETED', 'REJECTED')
- admin_notes: TEXT (rejection reasons)
```

#### 4. **ship_repairs** Table
```sql
Maintenance and repair request tracking
- id: Primary Key
- user_id: FOREIGN KEY → users(id)
- vessel_name: Identifier for vessel
- issue_type, description: Problem details
- priority: ENUM('LOW', 'MEDIUM', 'HIGH', 'EMERGENCY')
- status: ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')
- technician_notes: TEXT
```

#### 5. **inventory** Table
```sql
Stock management with real-time quantity tracking
- id: Primary Key
- item_code: Unique SKU
- name, category: Item classification
- quantity: INT (real-time count)
- unit_price: DECIMAL(15,2)
- status: ENUM('AVAILABLE', 'LOW_STOCK', 'OUT_OF_STOCK')
  Auto-calculated based on quantity
```

#### 6. **stock_exports** Table
```sql
Tracks material requisitions with approval workflow
- id: Primary Key
- user_id: FOREIGN KEY → users(id)
- inventory_id: FOREIGN KEY → inventory(id) [nullable]
- quantity, unit: Export specifics
- purpose, delivery_address: Context
- status: ENUM('PENDING', 'APPROVED', 'PROCESSING', 'DISPATCHED', 'REJECTED')
- Triggers automatic inventory deduction when APPROVED
```

#### 7. **payments** Table
```sql
Financial transaction tracking
- id: Primary Key
- user_id: FOREIGN KEY → users(id)
- amount: DECIMAL(15,2)
- payment_method: VARCHAR
- status: ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')
- description: Transaction details
```

#### 8. **notifications** Table
```sql
System alerts and user notifications
- id: Primary Key
- user_id: FOREIGN KEY → users(id) [nullable for broadcasts]
- title, message: Notification content
- type: ENUM('INFO', 'SUCCESS', 'WARNING', 'ALERT')
- is_read: Boolean read status
- created_at: Timestamp
```

### Database Relationships (Foreign Keys)
- ship_orders → users (ON DELETE CASCADE)
- ship_repairs → users (ON DELETE CASCADE)
- stock_exports → users (ON DELETE CASCADE)
- stock_exports → inventory (ON DELETE SET NULL)
- payments → users (ON DELETE CASCADE)
- notifications → users (ON DELETE CASCADE)

---

## 📁 Project Structure

```
blackpearl/
│
├── 📄 README.md                     # This file - comprehensive documentation
├── 📄 LICENSE                       # MIT License
├── 📁 database/
│   └── schema.sql                   # Complete database schema with seed data
│
├── 🎨 frontend/                     # Presentation Layer (13 Pages)
│   ├── index.html                   # Home page (public)
│   ├── login.html                   # Authentication page
│   ├── register.html                # User registration
│   ├── services.html                # Services overview (public)
│   ├── about.html                   # Company information (public)
│   ├── contact.html                 # Contact form (public)
│   ├── teams.html                   # Team information (public)
│   ├── user-dashboard.html          # User portal (authenticated)
│   ├── admin-dashboard.html         # Admin panel (admin-only)
│   ├── inventory.html               # Inventory management (admin)
│   ├── tender.html                  # Tender browser (user)
│   ├── stock-export.html            # Stock export requests
│   ├── vigilance.html               # System monitoring page
│   │
│   ├── 🎨 css/
│   │   ├── style.css                # Main stylesheet (glassmorphism design)
│   │   └── dashboard.css            # Dashboard-specific styling
│   │
│   ├── 📱 js/
│   │   ├── api.js                   # API service layer (fetch wrapper)
│   │   ├── auth.js                  # Authentication logic
│   │   ├── user.js                  # User dashboard scripts
│   │   └── admin.js                 # Admin dashboard scripts
│   │
│   └── 📁 assets/
│       └── [Images and static resources]
│
├── ⚙️  backend/                      # REST API Layer (Spring Boot)
│   ├── pom.xml                      # Maven dependencies & build config
│   │
│   └── src/main/java/com/blackpearl/
│       │
│       ├── 🚀 BlackPearlApplication.java  # Spring Boot entry point
│       │
│       ├── 🔗 controller/            # REST Endpoints (11 Controllers)
│       │   ├── AuthController.java       # /auth/login, /auth/register
│       │   ├── UserController.java       # /users (CRUD)
│       │   ├── TenderController.java     # /tenders (tender management)
│       │   ├── ShipOrderController.java  # /ship-orders (order requests)
│       │   ├── ShipRepairController.java # /ship-repairs (repair logs)
│       │   ├── InventoryController.java  # /inventory (stock management)
│       │   ├── StockExportController.java# /stock-exports (export tracking)
│       │   ├── PaymentController.java    # /payments (payment tracking)
│       │   ├── NotificationController.java# /notifications (alerts)
│       │   ├── DashboardController.java  # /dashboard (analytics)
│       │   └── PublicController.java     # /public (unauthenticated endpoints)
│       │
│       ├── 🧠 service/              # Business Logic (10 Services)
│       │   ├── UserService.java          # User CRUD & profile management
│       │   ├── TenderService.java        # Tender lifecycle operations
│       │   ├── ShipOrderService.java     # Order processing logic
│       │   ├── ShipRepairService.java    # Repair request handling
│       │   ├── InventoryService.java     # Stock management & calculations
│       │   ├── StockExportService.java   # Export approval & inventory deduction
│       │   ├── PaymentService.java       # Payment processing
│       │   ├── NotificationService.java  # Alert generation & delivery
│       │   ├── DashboardService.java     # Analytics aggregation
│       │   └── UserDetailsServiceImpl.java# Custom Spring Security service
│       │
│       ├── 💾 repository/          # Data Access (8 Repositories - Spring Data JPA)
│       │   ├── UserRepository.java       # User entity queries
│       │   ├── TenderRepository.java     # Tender queries
│       │   ├── ShipOrderRepository.java  # Order queries
│       │   ├── ShipRepairRepository.java # Repair queries
│       │   ├── InventoryRepository.java  # Stock queries
│       │   ├── StockExportRepository.java# Export queries
│       │   ├── PaymentRepository.java    # Payment queries
│       │   └── NotificationRepository.java# Notification queries
│       │
│       ├── 🗂️  dto/                 # Data Transfer Objects (8 DTOs)
│       │   ├── UserDto.java
│       │   ├── TenderDto.java
│       │   ├── ShipOrderDto.java
│       │   ├── ShipRepairDto.java
│       │   ├── InventoryDto.java
│       │   ├── StockExportDto.java
│       │   ├── PaymentDto.java
│       │   └── NotificationDto.java
│       │
│       ├── 🗄️  model/               # Entity Models (8 JPA Entities)
│       │   ├── User.java               # With Role enum, Department enum
│       │   ├── Tender.java             # With Status enum
│       │   ├── ShipOrder.java          # With Status enum
│       │   ├── ShipRepair.java         # With Status & Priority enums
│       │   ├── Inventory.java          # With Status enum
│       │   ├── StockExport.java        # With Status enum
│       │   ├── Payment.java            # With Status enum
│       │   └── Notification.java       # With Type enum
│       │
│       ├── 🔐 security/             # Security & Auth
│       │   ├── JwtUtil.java            # JWT token generation & validation
│       │   └── JwtAuthenticationFilter.java # Security filter
│       │
│       ├── ⚙️  config/              # Configuration Classes
│       │   ├── SecurityConfig.java     # Spring Security setup
│       │   ├── JpaConfig.java          # JPA configuration
│       │   └── DataInitializer.java    # Database seed data loader
│       │
│       ├── ⚠️  exception/           # Error Handling
│       │   ├── ResourceNotFoundException.java
│       │   └── GlobalExceptionHandler.java  # @ControllerAdvice
│       │
│       └── 📋 resources/
│           ├── application.properties # Configuration (DB, JWT, CORS)
│           └── schema.sql            # Database schema
│
└── 📦 target/                       # Build output (auto-generated)
    ├── classes/                     # Compiled Java classes
    ├── test-classes/                # Test classes
    ├── blackpearl-1.0.0.jar         # Compiled JAR application
    └── maven-status/                # Build metadata
```

---

## 🔌 API Endpoints

### Authentication Endpoints (`/auth`)
```
POST   /auth/login              - User login (returns JWT token)
POST   /auth/register           - User registration
POST   /auth/logout             - User logout
GET    /auth/me                 - Get current user profile
```

### User Management (`/users`) [ADMIN ONLY]
```
GET    /users                   - Get all users
GET    /users/{id}              - Get user by ID
PUT    /users/{id}              - Update user details
DELETE /users/{id}              - Delete user account
PATCH  /users/{id}/activate     - Activate user account
PATCH  /users/{id}/deactivate   - Deactivate user account
```

### Tender Management (`/tenders`)
```
GET    /tenders                 - Get all tenders (AUTHENTICATED)
GET    /tenders/open            - Get open tenders (PUBLIC)
GET    /tenders/{id}            - Get tender details (PUBLIC/AUTHENTICATED)
POST   /tenders                 - Create tender (ADMIN ONLY)
PATCH  /tenders/{id}/close      - Close tender (ADMIN ONLY)
DELETE /tenders/{id}            - Delete tender (ADMIN ONLY)
```

### Ship Orders (`/ship-orders`)
```
GET    /ship-orders             - Get all orders (AUTHENTICATED)
GET    /ship-orders/my          - Get user's orders
GET    /ship-orders/{id}        - Get order details
POST   /ship-orders             - Create new order
PATCH  /ship-orders/{id}/approve - Approve order (ADMIN)
PATCH  /ship-orders/{id}/reject  - Reject order (ADMIN)
DELETE /ship-orders/{id}        - Delete order
```

### Ship Repairs (`/ship-repairs`)
```
GET    /ship-repairs            - Get all repairs (AUTHENTICATED)
GET    /ship-repairs/my         - Get user's repair requests
POST   /ship-repairs            - Create repair request
PATCH  /ship-repairs/{id}/status - Update repair status (ADMIN)
```

### Inventory Management (`/inventory`)
```
GET    /inventory               - Get all items (AUTHENTICATED)
GET    /inventory/available     - Get available items only
GET    /inventory/{id}          - Get item details
POST   /inventory               - Create item (ADMIN)
PUT    /inventory/{id}          - Update item (ADMIN)
PATCH  /inventory/{id}/restock  - Restock item (ADMIN)
DELETE /inventory/{id}          - Delete item (ADMIN)
```

### Stock Exports (`/stock-exports`)
```
GET    /stock-exports           - Get all exports (ADMIN)
GET    /stock-exports/my        - Get user's export requests
POST   /stock-exports           - Submit new export request
PATCH  /stock-exports/{id}/approve - Approve export (ADMIN) [TRIGGERS INVENTORY DEDUCTION]
PATCH  /stock-exports/{id}/reject  - Reject export (ADMIN)
```

### Payments (`/payments`)
```
GET    /payments                - Get all payments (ADMIN)
GET    /payments/my             - Get user's payments
GET    /payments/{id}           - Get payment details
POST   /payments                - Create payment record
PATCH  /payments/{id}/status    - Update payment status (ADMIN)
GET    /payments/revenue/monthly - Get monthly revenue (ADMIN)
```

### Notifications (`/notifications`)
```
GET    /notifications           - Get user's notifications
GET    /notifications/unread    - Get unread count
PATCH  /notifications/{id}/read - Mark as read
DELETE /notifications/{id}      - Delete notification
POST   /notifications/broadcast - Send broadcast (ADMIN)
```

### Dashboard (`/dashboard`)
```
GET    /dashboard/user/stats    - Get user statistics
GET    /dashboard/admin/stats   - Get admin analytics (ADMIN ONLY)
```

### Public Endpoints (`/public`) – No Authentication Required
```
GET    /public/tenders          - Get open tenders (for landing page)
GET    /public/inventory        - Get available inventory items
```

---

## ✅ Code Analysis & Module Verification (Feb 2026)

A full codebase analysis was performed to ensure all modules and pages work correctly. The following fixes were applied:

| Issue | Location | Fix |
|-------|----------|-----|
| **Missing API.public** | `api.js` | Added `public` module with `getOpenTenders()` and `getAvailableInventory()` – required by `index.html` polling |
| **Broken renderUsers** | `admin-dashboard.html` | Removed invalid `renderUsers(users)` call; users load via `AdminModule.onSectionShown('users')` |
| **Export CSV crash** | `admin-dashboard.html` | Fixed `exportCSV()` to use cached data from AdminModule (`window.__adminOrders`, etc.) with proper DTO field mapping |
| **Department nav filter** | `user-dashboard.html` | Corrected `data-section` values from `ENGINEERING`/`OPERATIONS`/`PROCUREMENT` to `ship-orders`/`repairs`/`tenders`/`stock-exports` to match `auth.js` DEPT_ACCESS |
| **Notification form** | `admin-dashboard.html` | Added global `sendNotification()` wrapper and form `name` attributes (`notifTitle`, `notifMsg`, `notifType`) for API integration |

### Page & Script Loading Summary

| Page | Scripts Loaded | Status |
|------|----------------|--------|
| index.html | api.js, Leaflet | ✅ Public polling works |
| login.html | api.js, auth.js | ✅ Login/register |
| register.html | api.js, auth.js | ✅ Registration |
| user-dashboard.html | api.js, auth.js, user.js | ✅ Dashboard, sections, API |
| admin-dashboard.html | api.js, auth.js, admin.js | ✅ Full admin panel |
| tender.html | (inline) | ✅ Static demo data |
| stock-export.html | (inline) | ✅ Static demo data |
| inventory.html | (inline) | ✅ Static demo data |
| vigilance.html | (inline) | ✅ Static form |
| about.html, services.html, teams.html, contact.html | (public) | ✅ Static content |

---

## 🖥️ Frontend Modules

### Public Pages (No Authentication Required)
1. **index.html** - Landing page with services overview
2. **about.html** - Company background and mission
3. **services.html** - List of shipyard services
4. **teams.html** - Team member profiles
5. **contact.html** - Contact & support information

### Authentication Pages
6. **login.html** - User login form (JWT token generation)
7. **register.html** - User registration with role selection

### User Portal (Authentication Required - USER Role)
8. **user-dashboard.html** - Personal activity dashboard
9. **tender.html** - Browse and view tenders
10. **stock-export.html** - Submit and track exports

### Admin Portal (Authentication Required - ADMIN Role)
11. **admin-dashboard.html** - System analytics & monitoring
12. **inventory.html** - Full inventory management interface
13. **vigilance.html** - System monitoring and alerts

### Frontend Technology
- **HTML5**: Semantic markup
- **CSS3**: Glassmorphism design pattern for modern UI
- **Vanilla JavaScript (ES6+)**: Event handling, DOM manipulation
- **Fetch API**: HTTP communication with backend
- **LocalStorage**: Client-side JWT storage
- **Leaflet.js**: Interactive mapping (optional feature)

---

## ⚙️ Installation & Setup

### Prerequisites
- **Java 17+** (JDK installed and in PATH)
- **MySQL 8.0+** (Server running)
- **Maven 3.6+** (for building Spring Boot)
- **Node.js** (optional - for frontend tooling)
- **Git** (for version control)

### Step 1: Database Setup

1. Open MySQL Command Line or MySQL Workbench
2. Create database and execute schema:
   ```sql
   mysql> source database/schema.sql;
   ```
   Or manually:
   ```sql
   CREATE DATABASE blackpearl_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE blackpearl_db;
   -- Execute all SQL from database/schema.sql
   ```

3. Verify database creation:
   ```sql
   SHOW DATABASES;
   SHOW TABLES IN blackpearl_db;
   ```

### Step 2: Backend Setup

1. Open Terminal/Command Prompt
2. Navigate to backend directory:
   ```bash
   cd backend
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Verify `pom.xml` contains:
   - Spring Boot 3.2.3
   - Java 17 target version
   - MySQL connector
   - JWT dependencies (jjwt)
   - Spring Security

### Step 3: Configure Application

Edit `backend/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/blackpearl_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=12345

# JWT Configuration
jwt.secret=BlackPearlShipyardSecretKey2025VeryLongSecureKeyForJWT
jwt.expiration=86400000

# CORS Configuration
cors.allowed-origins=http://localhost:3000,http://127.0.0.1:5500

# Server Port
server.port=8080
server.servlet.context-path=/api
```

### Step 4: Frontend Setup

1. Place `frontend` folder in a web-accessible location
2. Use VS Code Live Server extension:
   - Right-click `index.html`
   - Select "Open with Live Server"
   - OR use Python: `python -m http.server 5500`
   - OR use Node.js: `npx http-server`

---

## 🔐 Demo Credentials

Use these accounts to test the system:

### Regular User
```
Email:    user@blackpearl.com
Password: user123
Role:     USER
Department: Engineering
```

### Administrator
```
Email:    admin@blackpearl.com
Password: admin123
Role:     ADMIN
Department: Management
```

---

## 🚀 Running the Application

### Terminal 1 - Start Backend Server

```bash
cd backend

# Option 1: Using Maven Spring Boot plugin
mvn spring-boot:run

# Option 2: Run compiled JAR
java -jar target/blackpearl-shipyard-1.0.0.jar

# Option 3: Using IDE (IntelliJ/Eclipse)
# Right-click BlackPearlApplication.java → Run
```

**Expected Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 
Started BlackPearlApplication in X.XXX seconds
Tomcat started on port(s): 8080 with context path: '/api'
```

Backend is now running at: **http://localhost:8080/api**

### Terminal 2 - Start Frontend Server

```bash
# Option 1: VS Code Live Server extension
# (Right-click frontend/index.html → Open with Live Server)

# Option 2: Python HTTP Server
cd frontend
python -m http.server 5500

# Option 3: Node.js http-server
npx http-server frontend -p 5500

# Option 4: Using Ruby (if installed)
cd frontend
ruby -run -ehttpd . -p5500
```

Frontend is now accessible at: **http://localhost:5500**

### Step-by-Step Usage

1. **Home Page** - Open http://localhost:5500
   - View landing page
   - Browse services, team, about sections
   - Click "Login" button

2. **Login** - Navigate to login.html
   - Enter credentials (User or Admin)
   - Click "Login"
   - JWT token stored in LocalStorage

3. **User Dashboard** - After successful login
   - View personal metrics
   - Browse open tenders
   - Submit orders/repairs
   - Track exports and payments

4. **Admin Dashboard** - If logged in as admin
   - View system-wide analytics
   - Manage inventory
   - Approve/reject requests
   - Send notifications

---

## 🔄 How It Works

### Authentication Flow

```
1. User enters email & password on login.html
   ↓
2. JavaScript captures form submission
   ↓
3. API.js sends POST /auth/login {email, password}
   ↓
4. AuthController receives request
   ↓
5. AuthenticationManager validates credentials
   ↓
6. JwtUtil.generateToken() creates JWT
   ↓
7. Token returned to frontend
   ↓
8. Token stored in localStorage via auth.js
   ↓
9. All future requests include Authorization header
   ↓
10. JwtAuthenticationFilter validates token
```

### Request Processing

```
Frontend (JavaScript)
  ↓
Fetch API with JWT in Authorization header
  ↓
CORS Filter (validates allowed origins)
  ↓
Spring Security Filter Chain
  ↓
JwtAuthenticationFilter (extracts & validates token)
  ↓
@RestController (matches request to handler)
  ↓
@PreAuthorize (checks roles)
  ↓
Service Layer (business logic & validation)
  ↓
Transactional operations (@Transactional)
  ↓
Repository (JPA/Hibernate → SQL)
  ↓
MySQL Database
  ↓
Response back to Frontend
```

### Inventory Deduction Workflow

When stock export is approved:

```
1. Admin clicks "Approve" on export request in admin-dashboard.html
   ↓
2. API sends PATCH /stock-exports/{id}/approve
   ↓
3. StockExportController receives request
   ↓
4. StockExportService.updateStatus() called
   ↓
5. Within @Transactional method:
   - Stock export status → APPROVED
   - Get inventory item ID
   - Fetch current inventory quantity
   - Subtract export quantity: new_qty = current_qty - export_qty
   - Update inventory.quantity in database
   - Auto-calculate status (AVAILABLE/LOW_STOCK/OUT_OF_STOCK)
   - Save both entities in single transaction
   ↓
6. If error occurs → Entire transaction rolls back
   ↓
7. Response sent: {message: "Approved and inventory deducted"}
   ↓
8. Frontend updates dashboard automatically
```

---

## 🎯 Use Cases

### Shipyard Operations Manager
- Monitor all ongoing projects (orders, repairs, tenders)
- Manage inventory levels
- Process export requests
- Track vendor payments
- Generate progress reports

### Maritime Procurement Officer
- Browse available materials in tenders
- Submit vessel specifications
- Track delivery timelines
- Manage shipyard relationships

### Inventory Controller
- Real-time stock visibility
- Automated deduction on exports
- Low-stock alerts
- Reorder recommendations

### System Administrator
- User account management
- Access control configuration
- System health monitoring
- Backup and recovery procedures

---

## 📖 Module Documentation

### Controller Layer
Each controller is REST-idempotent and returns proper HTTP status codes:
- `200 OK` - Successful GET/PUT
- `201 CREATED` - Successful POST
- `204 NO CONTENT` - Successful DELETE
- `400 BAD REQUEST` - Invalid input
- `401 UNAUTHORIZED` - Missing/invalid token
- `403 FORBIDDEN` - Insufficient permissions
- `404 NOT FOUND` - Resource not found
- `500 INTERNAL SERVER ERROR` - Server error

### Service Layer
Each service class:
- Implements business logic
- Handles validation
- Manages transactions (@Transactional)
- Converts entities to DTOs
- Throws `ResourceNotFoundException` for missing data
- Logs important operations

### Repository Layer
Uses Spring Data JPA for queries:
- `findAll()`, `findById()` provided by JpaRepository
- Custom queries defined in repository interfaces
- Automatic pagination support
- Query methods named conventionally

### Security Implementation
- `SecurityFilterChain` bean configures authorization rules
- `JwtAuthenticationFilter` validates tokens pre-request
- `UserDetailsServiceImpl` loads user authorities
- `@PreAuthorize` enforces method-level security
- `PasswordEncoder` hashes passwords with BCrypt

---

## ⚠️ Troubleshooting

### Backend Won't Start
```
❌ Error: java.net.ConnectException: Connection refused
✅ Solution: Ensure MySQL is running
   - Windows: MySQL service in Services manager
   - Mac: brew services start mysql
   - Linux: sudo systemctl start mysql

❌ Error: "Java version 17 not found"
✅ Solution: Set JAVA_HOME environment variable
   set JAVA_HOME=C:\Program Files\Java\jdk-17

❌ Error: "port 8080 already in use"
✅ Solution: Kill existing process or use different port
   # Windows: netstat -ano | findstr :8080
   # Mac/Linux: lsof -i :8080
   # Or change server.port in application.properties
```

### Database Connection Issues
```
❌ Error: "Access denied for user 'root'@'localhost'"
✅ Solution: Check credentials in application.properties
   spring.datasource.username=root
   spring.datasource.password=12345

❌ Error: "Unknown database 'blackpearl_db'"
✅ Solution: Run database/schema.sql in MySQL
   mysql> source database/schema.sql;
```

### Frontend Login Fails
```
❌ Error: 401 Unauthorized
✅ Solution:
   1. Verify backend is running (http://localhost:8080/api/public/tenders)
   2. Check email/password credentials
   3. Ensure CORS is configured correctly
   4. Check browser console for detailed error

❌ Error: "CORS error"
✅ Solution: Verify cors.allowed-origins in application.properties
   cors.allowed-origins=http://localhost:5500,http://127.0.0.1:5500

❌ Token not storing
✅ Solution: Check localStorage in browser DevTools
   - F12 → Application → LocalStorage → http://localhost:5500
   - Verify token is being saved after login
```

### API Endpoints Return 404
```
❌ Error: 404 Not Found
✅ Solution:
   1. Verify endpoint URL includes /api prefix
   2. Check REST controller @RequestMapping path
   3. Ensure proper HTTP method (GET/POST/PUT/DELETE)
   4. Verify authentication token if endpoint requires it

Example:
   ❌ http://localhost:8080/users  (missing /api)
   ✅ http://localhost:8080/api/users
```

### Database Seed Data Missing
```
❌ Error: No demo users or tenders shown
✅ Solution: Schema.sql includes INSERT IGNORE statements
   - Ensure schema.sql was fully executed
   - Check that INSERT statements ran without errors
   - Verify database.initial-mode=never in properties
```

---

## 🎓 Learning Outcomes

By studying and working with Black Pearl, you understand:

### Backend Architecture
- ✅ Spring Boot REST API development
- ✅ Layered architecture patterns
- ✅ Service-oriented design
- ✅ Transaction management (@Transactional)
- ✅ JPA/Hibernate ORM concepts
- ✅ Repository pattern implementation

### Security & Authentication
- ✅ JWT token-based authentication
- ✅ Password hashing with BCrypt
- ✅ Role-based access control (RBAC)
- ✅ Spring Security configuration
- ✅ CORS policy management
- ✅ Stateless session management principle

### Database Design
- ✅ Entity-relationship modeling
- ✅ Foreign key constraints
- ✅ Data integrity enforcement
- ✅ Query optimization basics
- ✅ Enum datatypes for status fields
- ✅ Cascading deletes and updates

### Frontend Development
- ✅ Vanilla JavaScript (ES6+) fundamentals
- ✅ Fetch API for HTTP communication
- ✅ DOM manipulation and events
- ✅ LocalStorage for client-side data
- ✅ Modern UI design (glassmorphism)
- ✅ Form validation and submission

### Production Best Practices
- ✅ Error handling and logging
- ✅ Input validation at multiple layers
- ✅ Separation of concerns
- ✅ Code organization and naming conventions
- ✅ Configuration management
- ✅ Documentation standards

---

## 🚀 Future Enhancements

### Phase 2 - Advanced Features
- [ ] Real-time notifications using WebSockets
- [ ] Advanced search and filtering
- [ ] PDF invoice generation
- [ ] Email notifications
- [ ] Payment gateway integration (Stripe/PayPal)
- [ ] Two-factor authentication (2FA)
- [ ] Audit logging for all transactions

### Phase 3 - Analytics & Reporting
- [ ] Advanced analytics dashboard with charts
- [ ] Export reports as PDF/Excel
- [ ] Predictive inventory forecasting
- [ ] Revenue analytics by tender
- [ ] Performance metrics and KPIs

### Phase 4 - Mobile & Cloud
- [ ] Mobile responsive Progressive Web App (PWA)
- [ ] Native iOS/Android mobile apps
- [ ] Cloud deployment (AWS/Azure/GCP)
- [ ] Containerization (Docker)
- [ ] Kubernetes orchestration
- [ ] CI/CD pipeline automation

### Phase 5 - Advanced Integration
- [ ] Third-party vendor portal
- [ ] API documentation (Swagger/OpenAPI)
- [ ] GraphQL API layer
- [ ] Message queue integration (RabbitMQ/Kafka)
- [ ] Real-time vessel tracking
- [ ] Blockchain for supply chain transparency

---

## 🏆 Project Accomplishments

✅ **11 REST Controllers** with proper routing and error handling  
✅ **10 Service Classes** implementing business logic and validation  
✅ **8 Repository Interfaces** with Spring Data JPA  
✅ **8 Entity Models** with proper ORM annotations  
✅ **8 DTOs** for clean data transfer  
✅ **13 Frontend Pages** with responsive design  
✅ **MySQL Database** with 8 interconnected tables  
✅ **JWT Authentication** with role-based authorization  
✅ **Transaction Management** for data consistency  
✅ **Error Handling** with custom exceptions  
✅ **CORS Configuration** for frontend integration  
✅ **Production-Ready Code** with logging and validation  

---

## 📜 License

MIT License © 2025 Black Pearl Shipyard Management System

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software.

---

## 👨‍💻 About This Project

**Black Pearl** demonstrates enterprise-grade full-stack development with:
- Production-ready architecture
- Security best practices
- Clean code principles
- Comprehensive documentation
- Real-world business logic

**Perfect for:**
- Resume/Portfolio presentation
- Internship applications
- Technical interview preparation
- Learning full-stack development
- Understanding enterprise patterns

---

## 📞 Support & Contributions

For issues, feature requests, or contributions:

1. Check [Troubleshooting](#⚠️-troubleshooting) section
2. Review code comments and documentation
3. Test with provided demo credentials
4. Use browser DevTools for frontend debugging
5. Check MySQL and backend logs for server issues

---

## 🌟 Built with Maritime Innovation & Engineering Excellence

**"Not all who wander in shipyards are lost. Some are optimizing inventories." ⚓**

---

*Last Updated: February 22, 2026*  
*Status: ✅ Production Ready*
#   B l a c k P e a r l  
 