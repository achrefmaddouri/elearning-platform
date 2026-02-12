# 🎓 EduSmart – AI-Powered E-Learning Platform

[![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue?logo=react)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-4.9-blue?logo=typescript)](https://www.typescriptlang.org/)
[![Python](https://img.shields.io/badge/Python-3.9%2B-blue?logo=python)](https://www.python.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> A production-grade, full-stack AI-powered e-learning platform built with Spring Boot microservices, React frontend, and Python ML models for intelligent course recommendations.

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [Database Configuration](#database-configuration)
- [API Endpoints](#api-endpoints)
- [Docker Setup](#docker-setup)
- [Contributing](#contributing)
- [License](#license)
- [Author](#author)

---

## ⭐ Features

### Core Functionality
- 🔐 **JWT Authentication**: Secure token-based authentication with OTP email verification
- 📚 **Course Management**: Full CRUD operations for courses with rich content
- 🧪 **Quiz System**: AI-powered quiz generation using Google Gemini API
- 📁 **File Management**: Upload and manage videos, images, PDFs, and documents
- 📊 **Progress Tracking**: Real-time student progress monitoring with completion metrics
- 💬 **Real-time Chat**: WebSocket-based instructor-student communication
- 💳 **Stripe Payments**: Secure payment integration for paid courses
- 👑 **Role-Based Access**: Three tiers - Student, Instructor, Admin
- 🎮 **Gamification**: Badges, rewards, and leaderboards for engagement
- 📈 **Analytics**: PowerBI integration and comprehensive dashboards
- 🤖 **AI Recommendations**: Machine learning-based personalized course suggestions

### User Roles
| Role | Capabilities |
|------|--------------|
| **Student** | Enroll courses, take quizzes, track progress, participate in chat, earn badges |
| **Instructor** | Create/manage courses, design quizzes, upload materials, view analytics |
| **Admin** | Manage users/courses, approve content, view system statistics, retrain AI models |

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL 8.0+ with JPA/Hibernate
- **Security**: Spring Security 6 + JWT (JJWT 0.11.5)
- **Real-time**: Spring WebSocket + Socket.IO
- **Payments**: Stripe Java SDK 24.16.0
- **AI Integration**: Google Cloud AI Platform
- **Build**: Maven 3.6+

### Frontend
- **Framework**: React 19 with TypeScript 4.9
- **Routing**: React Router v7
- **Styling**: Tailwind CSS + custom CSS
- **HTTP**: Axios + Socket.IO client
- **Charts**: Chart.js + react-chartjs-2
- **UI Components**: Heroicons
- **Payments**: Stripe React SDK
- **Analytics**: PowerBI Client
- **Notifications**: React Toastify

### AI & ML
- **Platform**: Python 3.9+ (Jupyter Notebooks)
- **ML Models**: scikit-learn, TensorFlow
- **Data Processing**: Pandas, NumPy
- **Recommendation**: Collaborative & Content-based filtering

### Database
- **DBMS**: MySQL 8.0
- **Analytics Views**: PowerBI-compatible SQL views
- **Seeding**: Mock data for testing

---

## 📁 Project Structure

```
edusmart/
├── backend/                          # Spring Boot Microservices
│   ├── src/main/java/com/elearning/
│   │   ├── controller/               # REST API endpoints
│   │   │   ├── AuthController
│   │   │   ├── CourseController
│   │   │   ├── UserController
│   │   │   ├── AdminController
│   │   │   ├── InstructorController
│   │   │   ├── AIController
│   │   │   ├── QuizController
│   │   │   ├── ChatController
│   │   │   ├── PaymentController
│   │   │   ├── ProgressController
│   │   │   ├── GamificationController
│   │   │   └── AnalyticsController
│   │   ├── service/                  # Business logic
│   │   ├── repository/               # Data access layer
│   │   ├── model/                    # JPA entities
│   │   ├── security/                 # JWT & Security config
│   │   ├── dto/                      # Data transfer objects
│   │   └── config/                   # Configuration classes
│   ├── src/main/resources/
│   │   ├── application.properties    # DB & server config
│   │   ├── powerbi-reports-config.json
│   │   └── ai-models/                # Embedded ML models
│   ├── src/test/java/                # Unit tests
│   ├── pom.xml                       # Maven dependencies
│   └── Dockerfile
│
├── frontend/                         # React Admin + User Dashboard
│   ├── src/
│   │   ├── components/
│   │   │   ├── auth/                 # Login, Register, OTP
│   │   │   ├── courses/              # Course List, Details
│   │   │   ├── admin/                # Admin Dashboard
│   │   │   ├── instructor/           # Instructor tools
│   │   │   ├── chat/                 # Real-time chat
│   │   │   ├── quiz/                 # Quiz interface
│   │   │   ├── gamification/         # Badges, Leaderboard
│   │   │   ├── analytics/            # Analytics Dashboard
│   │   │   ├── powerbi/              # PowerBI reports
│   │   │   ├── videos/               # Video player
│   │   │   ├── profile/              # User profile
│   │   │   ├── ai/                   # AI Insights
│   │   │   └── common/               # Navbar, PrivateRoute
│   │   ├── services/
│   │   │   ├── authService.ts
│   │   │   ├── courseService.ts
│   │   │   ├── adminService.ts
│   │   │   ├── chatService.ts
│   │   │   ├── paymentService.ts
│   │   │   ├── analyticsService.ts
│   │   │   └── socket.ts
│   │   ├── styles/                   # CSS + Tailwind
│   │   ├── App.tsx
│   │   └── index.tsx
│   ├── public/
│   ├── package.json
│   ├── tsconfig.json
│   ├── tailwind.config.js
│   └── Dockerfile
│
├── ai-model/                         # Python ML Models
│   ├── E_Learning_AI_Recommendation_System.ipynb
│   ├── E_Learning_AI_Recommendation_System_Colab.ipynb
│   ├── models/                       # Trained models
│   ├── data/                         # Training datasets
│   ├── outputs/                      # Model outputs
│   ├── requirements.txt
│   ├── generate_dataset.py
│   └── setup_environment.ps1
│
├── database/                         # SQL Scripts
│   ├── schema.sql                    # Tables & constraints
│   ├── add_analytics_tables.sql      # Analytics tables
│   ├── add_video_materials_table.sql
│   ├── powerbi_analytics_views.sql   # PowerBI views
│   └── mock_data/
│
├── docs/                             # Documentation
│   ├── ARCHITECTURE.md
│   ├── API_DOCUMENTATION.md
│   ├── SETUP_GUIDE.md
│   └── DEPLOYMENT.md
│
├── .gitignore                        # Git configuration
├── docker-compose.yml                # Multi-container orchestration
├── README.md                         # This file
├── CONTRIBUTING.md
└── LICENSE
```

---

## 🚀 Quick Start

### Prerequisites
- **Java**: 17+ with Maven 3.6+
- **Node.js**: 16+ with npm/yarn
- **Python**: 3.9+ (for AI models)
- **MySQL**: 8.0+
- **Docker**: Optional but recommended

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/edusmart.git
cd edusmart
```

### 2. Backend Setup
```bash
cd backend

# Configure database connection
# Edit: src/main/resources/application.properties

# Build project
mvn clean install

# Run development server
mvn spring-boot:run
```
Backend runs on: `http://localhost:8080`

### 3. Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```
Frontend runs on: `http://localhost:3000`

### 4. Database Setup
```bash
# Create database
mysql -u root -p < ../database/schema.sql

# Optional: Add analytics views
mysql -u root -p edusmart_db < ../database/add_analytics_tables.sql
```

---

## 🔧 Backend Setup (Detailed)

### Step 1: Install Java 17+
```bash
java --version  # Verify installation
```

### Step 2: Configure Application Properties
```properties
# src/main/resources/application.properties

# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/edusmart_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
jwt.secret=your_very_secure_jwt_secret_key_minimum_32_characters
jwt.expiration=86400000
jwt.refreshExpiration=604800000

# Email (OTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

# Stripe
stripe.api.key=sk_test_your_stripe_secret_key

# Google AI (Gemini)
google.ai.api.key=your_gemini_api_key

# Logging
logging.level.root=INFO
logging.level.com.elearning=DEBUG
```

### Step 3: Build & Run
```bash
# Build with dependencies
mvn clean install

# Run with Maven plugin
mvn spring-boot:run

# Or run JAR directly
java -jar target/elearning-platform-0.0.1-SNAPSHOT.jar
```

### Step 4: Verify Backend
```bash
# Test health check
curl http://localhost:8080/api/auth/health
```

---

## 🎨 Frontend Setup (Detailed)

### Step 1: Install Dependencies
```bash
cd frontend
npm install
```

### Step 2: Environment Configuration
```bash
# Create .env file
echo "REACT_APP_API_URL=http://localhost:8080/api" > .env
echo "REACT_APP_STRIPE_PUBLISHABLE_KEY=pk_test_your_key" >> .env
echo "REACT_APP_ENVIRONMENT=development" >> .env
```

### Step 3: Development Server
```bash
# Start development server with hot reload
npm start

# Or use yarn
yarn start
```

### Step 4: Build for Production
```bash
# Create optimized production build
npm run build

# Serve build locally
npm install -g serve
serve -s build -l 3000
```

---

## 🗄️ Database Configuration

### Step 1: Create Database
```bash
mysql -u root -p

# In MySQL shell
CREATE DATABASE edusmart_db;
CREATE USER 'edusmart_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON edusmart_db.* TO 'edusmart_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Step 2: Import Schema
```bash
mysql -u edusmart_user -p edusmart_db < database/schema.sql
```

### Step 3: Add Analytics Views (Optional)
```bash
mysql -u edusmart_user -p edusmart_db < database/powerbi_analytics_views.sql
```

### Step 4: Seed Mock Data (Optional)
```bash
# Import sample data
mysql -u edusmart_user -p edusmart_db < database/mock_data/seed_data.sql
```

---

## 📡 API Endpoints

### Authentication (`/api/auth`)
```
POST   /register              Register new user
POST   /verify-otp            Verify OTP
POST   /login                 User login
POST   /refresh               Refresh JWT token
POST   /logout                User logout
POST   /password-reset        Request password reset
POST   /password-reset/confirm Confirm reset with token
```

### Courses (`/api/courses`)
```
GET    /public                Get all published courses
GET    /{id}                  Get course details
POST   /                      Create course (Instructor)
PUT    /{id}                  Update course
DELETE /{id}                  Delete course
POST   /{id}/enroll           Enroll in course
GET    /{id}/files            Get course materials
```

### Users (`/api/users`)
```
GET    /profile               Get current user profile
PUT    /profile               Update profile
PUT    /change-password       Change password
GET    /enrolled-courses      Get enrolled courses
```

### Instructor (`/api/instructor`)
```
GET    /courses               Get instructor's courses
POST   /courses               Create course
PUT    /courses/{id}          Update course
DELETE /courses/{id}          Delete course
```

### Admin (`/api/admin`)
```
GET    /users                 Get all users
GET    /statistics            Get system statistics
PUT    /users/{id}/ban        Ban user
PUT    /users/{id}/role       Change user role
GET    /courses/pending       Get pending courses
PUT    /courses/{id}/approve  Approve course
```

### AI/Recommendations (`/api/ai`)
```
GET    /recommendations       Get personalized recommendations
GET    /insights              Get AI insights dashboard
POST   /retrain-models        Retrain ML models (Admin)
GET    /model-metrics         Get model performance (Admin)
```

### Chat (`/api/chat`)
```
GET    /rooms                 Get user's chat rooms
GET    /course/{id}/room      Get course chat room
POST   /message               Send message
```

### Analytics (`/api/analytics`)
```
GET    /dashboard             Get analytics dashboard
GET    /user-activity         Get user activity stats
POST   /create-sample-data    Generate sample data
```

### Payments (`/api/payments`)
```
POST   /create-intent         Create Stripe payment intent
POST   /confirm               Confirm payment
GET    /history               Get payment history
```

### Gamification (`/api/gamification`)
```
GET    /badges                Get user badges
GET    /leaderboard           Get leaderboard
GET    /rewards               Get available rewards
```

---

## 🐳 Docker Setup

### Quick Start with Docker Compose
```bash
# From root directory
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Access Services
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **MySQL**: localhost:3306

---

## 🧪 Testing

### Backend Unit Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

---

## 📚 Additional Documentation

- [**Architecture Guide**](docs/ARCHITECTURE.md) - System design, microservices, database schema
- [**API Documentation**](docs/API_DOCUMENTATION.md) - Complete endpoint reference
- [**Setup Guide**](docs/SETUP_GUIDE.md) - Detailed installation steps
- [**Deployment Guide**](docs/DEPLOYMENT.md) - Production deployment
- [**Contributing**](CONTRIBUTING.md) - Development guidelines

---

## 🤝 Contributing

We welcome contributions! Please follow our [CONTRIBUTING.md](CONTRIBUTING.md) guide for:
- Code standards
- Commit conventions
- Pull request process
- Testing requirements

**Commit Convention:**
```
feat(scope): description
fix(scope): description
docs(scope): description
style(scope): description
refactor(scope): description
test(scope): description
chore(scope): description
```

---

## 📊 Statistics

- **Backend**: 15+ controllers, 50+ REST endpoints
- **Database**: 20+ normalized tables with analytics views
- **Frontend**: 20+ React components with TypeScript
- **Test Coverage**: Unit tests for all major services
- **Code Lines**: ~15,000+ lines of production code

---

## 🔒 Security Features

- ✅ JWT token-based authentication with expiration
- ✅ BCrypt password hashing
- ✅ Role-based access control (RBAC)
- ✅ CORS configuration
- ✅ SQL injection prevention (parameterized queries)
- ✅ HTTPS/SSL ready
- ✅ Input validation and sanitization

---

## 📄 License

This project is licensed under the **MIT License** - see [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Achref Maddouri** – Software Engineer

Open to **CDI opportunities in France** 🇫🇷

- **GitHub**: [[@yourusername](https://github.com/yourusername)](https://github.com/achrefmaddouri)
- **LinkedIn**: [[Achref Maddouri](https://linkedin.com/in/achref-maddouri)](https://www.linkedin.com/in/achref-maddouri-a1381821a/)
- **Email**: achref.maddouri@esprit.tn

---

## 🙋 Support

- **Issues**: Report bugs via [GitHub Issues](https://github.com/yourusername/edusmart/issues)
- **Discussions**: Ask questions in [GitHub Discussions](https://github.com/yourusername/edusmart/discussions)
- **Documentation**: Check [docs/](docs/) folder for detailed guides

---

**Built with ❤️ | EduSmart Platform**

Last Updated: February 12, 2026
