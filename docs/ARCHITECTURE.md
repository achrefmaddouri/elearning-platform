# EduSmart – System Architecture Documentation

## 1. Overview

EduSmart is built using a **microservices architecture** with clear separation of concerns, enabling scalability, maintainability, and independent service deployment.

---

## 2. High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                             │
├──────────────────────┬──────────────────┬──────────────────────┤
│  React Admin        │  React User       │  Flutter Mobile       │
│  Dashboard          │  Dashboard        │  Application          │
└──────────────────────┴──────────────────┴──────────────────────┘
                              │
                              ↓
                    ┌─────────────────┐
                    │  API Gateway    │
                    │  (Port: 8080)   │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ↓                    ↓                    ↓
    ┌────────┐           ┌────────┐          ┌────────┐
    │  Auth  │           │ Course │          │ User   │
    │Service │           │Service │          │Service │
    │(8081)  │           │(8082)  │          │(8083)  │
    └────────┘           └────────┘          └────────┘
        │                    │                    │
        │                    ↓                    │
        │              ┌──────────────┐           │
        │              │ AI Engine    │           │
        │              │ (Python)     │           │
        │              └──────────────┘           │
        │                    │                    │
        └────────────────────┼────────────────────┘
                             │
                    ┌────────▼────────┐
                    │   MySQL 8.0+    │
                    │   Database      │
                    └─────────────────┘
```

---

## 3. Service Architecture

### 3.1 Authentication Service (Port: 8081)

**Responsibilities:**
- User registration and login
- JWT token generation and validation
- Password reset and recovery
- Role management

**Key Components:**
```
AuthService/
├── AuthController
├── AuthService
├── JwtTokenProvider
├── SecurityConfig
├── User (Entity)
└── AuthDTO
```

**API Endpoints:**
```
POST   /api/auth/register        - User registration
POST   /api/auth/login           - User login
POST   /api/auth/refresh         - Refresh token
POST   /api/auth/logout          - User logout
POST   /api/auth/password-reset  - Password reset
GET    /api/auth/verify/{token}  - Email verification
```

**Database Tables:**
- `users` - User accounts
- `refresh_tokens` - Token management
- `user_roles` - Role assignments

---

### 3.2 Course Service (Port: 8082)

**Responsibilities:**
- Course CRUD operations
- Enrollment management
- Course materials handling
- Progress tracking

**Key Components:**
```
CourseService/
├── CourseController
├── CourseService
├── EnrollmentService
├── Course (Entity)
├── Enrollment (Entity)
└── CourseDTO
```

**API Endpoints:**
```
GET    /api/courses              - Get all courses
GET    /api/courses/{id}         - Get course details
POST   /api/courses              - Create course
PUT    /api/courses/{id}         - Update course
DELETE /api/courses/{id}         - Delete course
POST   /api/courses/{id}/enroll  - Enroll in course
GET    /api/courses/{id}/students - Get enrolled students
```

**Database Tables:**
- `courses` - Course information
- `enrollments` - Student enrollments
- `course_materials` - Videos, PDFs, etc.
- `course_categories` - Course classification

---

### 3.3 User Service (Port: 8083)

**Responsibilities:**
- User profile management
- Learning history tracking
- User preferences
- Analytics data collection

**Key Components:**
```
UserService/
├── UserController
├── UserService
├── UserProfileService
├── UserProfile (Entity)
├── LearningHistory (Entity)
└── UserDTO
```

**API Endpoints:**
```
GET    /api/users/{id}           - Get user profile
PUT    /api/users/{id}           - Update profile
GET    /api/users/{id}/history   - Get learning history
GET    /api/users/{id}/progress  - Get progress
POST   /api/users/{id}/preferences - Set preferences
```

**Database Tables:**
- `user_profiles` - Extended profile info
- `learning_history` - Course interactions
- `user_preferences` - Learning preferences
- `achievements` - Badges and certificates

---

### 3.4 AI Recommendation Engine (Python)

**Responsibilities:**
- Generate personalized recommendations
- Implement collaborative filtering
- Content-based filtering
- Model training and updates

**Architecture:**
```
AIEngine/
├── models/
│   ├── collaborative_filtering.py
│   ├── content_based.py
│   └── hybrid_model.py
├── data/
│   ├── data_loader.py
│   └── preprocessor.py
├── api/
│   ├── fastapi_server.py
│   └── endpoints.py
└── notebooks/
    └── model_training.ipynb
```

**Key Features:**
- Collaborative filtering using user-item matrices
- Content-based filtering using course features
- Hybrid approach combining both methods
- Real-time and batch recommendations

**API Endpoints:**
```
POST   /api/recommend/user/{userId}     - Get recommendations
POST   /api/recommend/similar/{courseId} - Get similar courses
POST   /api/model/train                 - Trigger model training
GET    /api/model/metrics               - Get model performance
```

---

### 3.5 Analytics Service

**Responsibilities:**
- User engagement tracking
- Performance metrics
- Dashboard data aggregation
- Real-time analytics

**Database Views:**
```sql
-- Daily active users
SELECT DATE(created_at) as date, COUNT(DISTINCT user_id) 
FROM user_activities GROUP BY DATE(created_at);

-- Course engagement metrics
SELECT course_id, AVG(completion_rate), COUNT(DISTINCT user_id)
FROM enrollments GROUP BY course_id;

-- Recommendation accuracy
SELECT AVG(click_rate), AVG(enrollment_rate)
FROM recommendation_tracking;
```

---

## 4. Database Schema

### 4.1 Core Entities

```
users
├── id (PK)
├── email (UNIQUE)
├── password_hash
├── first_name
├── last_name
├── role (enum: STUDENT, INSTRUCTOR, ADMIN)
├── is_active
├── created_at
└── updated_at

courses
├── id (PK)
├── title
├── description
├── instructor_id (FK: users.id)
├── category
├── level (BEGINNER, INTERMEDIATE, ADVANCED)
├── duration (hours)
├── price
├── is_published
├── created_at
└── updated_at

enrollments
├── id (PK)
├── user_id (FK: users.id)
├── course_id (FK: courses.id)
├── enrollment_date
├── completion_date
├── progress_percentage
└── is_completed

user_interactions
├── id (PK)
├── user_id (FK: users.id)
├── course_id (FK: courses.id)
├── action_type (VIEW, CLICK, ENROLL, COMPLETE)
├── interaction_date
└── duration

recommendations
├── id (PK)
├── user_id (FK: users.id)
├── course_id (FK: courses.id)
├── score (0.0 - 1.0)
├── algorithm (COLLABORATIVE, CONTENT_BASED)
├── created_at
└── clicked (boolean)
```

### 4.2 Relationships

```
User (1) ──→ (M) Course [instructor relationship]
User (M) ──→ (M) Course [enrollment relationship]
User (1) ──→ (M) UserInteraction
User (1) ──→ (M) Recommendation
Course (1) ──→ (M) CourseMaterial
Course (1) ──→ (M) Quiz
```

---

## 5. API Gateway Pattern

The API Gateway provides:
- **Request Routing**: Routes requests to appropriate services
- **Authentication**: Validates JWT tokens
- **Rate Limiting**: Protects against abuse
- **Request/Response Logging**: Audit trails
- **CORS Configuration**: Cross-origin request handling

```yaml
# Gateway Configuration
routes:
  - path: /api/auth/**
    service: auth-service:8081
  - path: /api/courses/**
    service: course-service:8082
  - path: /api/users/**
    service: user-service:8083
  - path: /api/recommendations/**
    service: ai-engine:5000
```

---

## 6. Authentication & Security

### 6.1 JWT Token Structure

```
Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "sub": "user_id",
  "email": "user@example.com",
  "role": "STUDENT",
  "iat": 1706582400,
  "exp": 1706668800
}

Signature: HMAC-SHA256
```

### 6.2 Security Measures

- **Password Hashing**: BCrypt with salt
- **Token Expiration**: 24 hours for access token, 7 days for refresh token
- **CORS**: Restricted to trusted domains
- **SQL Injection Prevention**: Parameterized queries
- **HTTPS**: SSL/TLS encryption
- **CSRF Protection**: SameSite cookies

---

## 7. Data Flow Diagrams

### 7.1 User Registration Flow

```
User (Frontend)
    │
    ├─→ POST /api/auth/register
    │   {email, password, firstName, lastName, role}
    │
    ├─→ Auth Service
    │   ├─ Validate input
    │   ├─ Hash password (BCrypt)
    │   ├─ Store in database
    │   └─ Send verification email
    │
    └─→ Response: {userId, message: "Verification email sent"}
```

### 7.2 Course Recommendation Flow

```
Frontend (User Dashboard)
    │
    ├─→ GET /api/recommendations/user/{userId}
    │
    ├─→ User Service
    │   ├─ Fetch user preferences
    │   ├─ Fetch enrollment history
    │   └─ Get user interactions
    │
    ├─→ AI Engine (Python)
    │   ├─ Analyze user behavior
    │   ├─ Apply ML models
    │   ├─ Generate scores
    │   └─ Return top-N recommendations
    │
    └─→ Response: [{courseId, title, score, reason}]
```

### 7.3 Course Enrollment Flow

```
Frontend (Course Page)
    │
    ├─→ POST /api/courses/{courseId}/enroll
    │
    ├─→ Course Service
    │   ├─ Validate user authorization
    │   ├─ Check prerequisite courses
    │   ├─ Create enrollment record
    │   └─ Update progress tracking
    │
    ├─→ Analytics Service
    │   └─ Log enrollment event
    │
    └─→ Response: {enrollmentId, status: "enrolled"}
```

---

## 8. Deployment Architecture

### 8.1 Docker Containerization

```dockerfile
# Backend Service
FROM openjdk:17-jdk-slim
COPY target/app.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

# Frontend
FROM node:16-alpine
COPY build/ /app/
EXPOSE 3000
CMD ["serve", "-s", "app"]

# AI Engine
FROM python:3.9
COPY requirements.txt .
RUN pip install -r requirements.txt
EXPOSE 5000
CMD ["python", "app.py"]
```

### 8.2 Docker Compose Orchestration

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: edusmart_db

  auth-service:
    build: ./backend
    ports:
      - "8081:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/edusmart_db

  course-service:
    build: ./backend
    ports:
      - "8082:8080"
    depends_on:
      - mysql

  ai-engine:
    build: ./ai-model
    ports:
      - "5000:5000"
    depends_on:
      - mysql

  frontend:
    build: ./frontend
    ports:
      - "3000:3000"
    depends_on:
      - auth-service
```

---

## 9. Scalability Considerations

### 9.1 Horizontal Scaling

- **Load Balancing**: Nginx or HAProxy for distributing traffic
- **Stateless Services**: Each service instance is independent
- **Database Replication**: Master-slave MySQL setup
- **Cache Layer**: Redis for session and recommendation caching

### 9.2 Performance Optimization

- **Database Indexing**: Indexes on frequently queried columns
- **Query Optimization**: Avoid N+1 queries using JOIN operations
- **Caching Strategy**:
  - Cache popular courses (1-hour TTL)
  - Cache user profiles (30-minute TTL)
  - Cache recommendations (real-time)
- **Lazy Loading**: Frontend pagination for large datasets

---

## 10. Monitoring & Logging

### 10.1 Logging Strategy

```java
// Service-level logging
@Slf4j
public class CourseService {
    public Course getCourseById(Long id) {
        log.info("Fetching course with ID: {}", id);
        try {
            return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        } catch (Exception e) {
            log.error("Error fetching course", e);
            throw e;
        }
    }
}
```

### 10.2 Monitoring Metrics

- **Request Latency**: Average response time per endpoint
- **Error Rate**: Percentage of failed requests
- **Database Performance**: Query execution time
- **Resource Utilization**: CPU, memory, disk usage
- **Recommendation Accuracy**: Click-through rate, conversion rate

---

## 11. Testing Strategy

### 11.1 Unit Tests
```java
@SpringBootTest
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;
    
    @InjectMocks
    private CourseService courseService;
    
    @Test
    public void testGetCourseById() {
        // Arrange
        Course course = new Course();
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        
        // Act
        Course result = courseService.getCourseById(1L);
        
        // Assert
        assertEquals(1L, result.getId());
    }
}
```

### 11.2 Integration Tests
- Test inter-service communication
- Test API endpoints end-to-end
- Test database operations

### 11.3 Performance Tests
- Load testing with JMeter
- Stress testing for peak loads
- Memory leak detection

---

## 12. Disaster Recovery

### 12.1 Backup Strategy
- **Daily Database Backups**: Automated MySQL backups
- **Version Control**: Git for code versioning
- **Infrastructure as Code**: Terraform/CloudFormation

### 12.2 Recovery Plan
- **RTO (Recovery Time Objective)**: 4 hours
- **RPO (Recovery Point Objective)**: 1 hour
- **Failover Mechanism**: Automated service restart

---

## 13. Future Enhancements

- **API Gateway**: Implement Spring Cloud Gateway
- **Service Mesh**: Consider Istio for advanced traffic management
- **Message Queue**: RabbitMQ/Kafka for asynchronous processing
- **GraphQL**: GraphQL layer for flexible queries
- **Microservices Patterns**: Circuit breaker, retry logic, timeouts

---

**Last Updated**: February 12, 2026
