# EduSmart – API Documentation

## Overview

Complete REST API documentation for the EduSmart e-learning platform. All endpoints require JWT authentication unless specified otherwise.

---

## Base URL

```
Development: http://localhost:8080/api
Production: https://api.edusmart.com/api
```

## Authentication

All requests (except login/register) must include:

```http
Authorization: Bearer {accessToken}
Content-Type: application/json
```

---

## 1. Authentication Endpoints

### 1.1 User Registration

**Endpoint:** `POST /auth/register`

**Description:** Register a new user account

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "role": "STUDENT"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "STUDENT",
  "message": "Registration successful. Please verify your email."
}
```

**Error (400 Bad Request):**
```json
{
  "error": "Email already registered",
  "code": "EMAIL_EXISTS"
}
```

---

### 1.2 User Login

**Endpoint:** `POST /auth/login`

**Description:** Authenticate user and receive JWT tokens

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STUDENT"
  }
}
```

---

### 1.3 Refresh Token

**Endpoint:** `POST /auth/refresh`

**Description:** Get a new access token using refresh token

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400
}
```

---

### 1.4 Password Reset Request

**Endpoint:** `POST /auth/password-reset`

**Description:** Request password reset email

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Reset email sent successfully"
}
```

---

### 1.5 Reset Password with Token

**Endpoint:** `POST /auth/password-reset/confirm`

**Description:** Complete password reset with token

**Request Body:**
```json
{
  "token": "reset_token_from_email",
  "newPassword": "NewSecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset successfully"
}
```

---

## 2. Course Endpoints

### 2.1 Get All Courses

**Endpoint:** `GET /courses`

**Description:** Retrieve paginated list of all published courses

**Query Parameters:**
```
page=0                    (default: 0)
size=10                   (default: 20)
category=Technology       (optional)
level=BEGINNER            (optional: BEGINNER, INTERMEDIATE, ADVANCED)
sort=title,asc            (optional)
searchTerm=Web Dev        (optional)
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Web Development 101",
      "description": "Learn the basics of web development",
      "category": "Technology",
      "level": "BEGINNER",
      "instructor": {
        "id": 2,
        "name": "Jane Smith"
      },
      "duration": 40,
      "rating": 4.8,
      "reviewCount": 156,
      "enrollmentCount": 450,
      "price": 0,
      "imageUrl": "https://...",
      "createdAt": "2025-01-15T10:00:00Z"
    }
  ],
  "totalElements": 150,
  "totalPages": 15,
  "currentPage": 0,
  "hasNext": true
}
```

---

### 2.2 Get Course Details

**Endpoint:** `GET /courses/{courseId}`

**Description:** Retrieve detailed information about a specific course

**Path Parameters:**
```
courseId: 1 (required)
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Web Development 101",
  "description": "Comprehensive guide to web development",
  "longDescription": "This course covers HTML, CSS, JavaScript...",
  "category": "Technology",
  "level": "BEGINNER",
  "instructor": {
    "id": 2,
    "name": "Jane Smith",
    "bio": "Expert web developer with 10+ years experience",
    "profileImage": "https://..."
  },
  "duration": 40,
  "lessons": 25,
  "rating": 4.8,
  "reviewCount": 156,
  "enrollmentCount": 450,
  "price": 0,
  "currency": "USD",
  "objectives": [
    "Learn HTML5 fundamentals",
    "Master CSS3 styling",
    "Write interactive JavaScript code"
  ],
  "requirements": [
    "Basic computer knowledge",
    "Text editor or IDE"
  ],
  "materials": [
    {
      "id": 1,
      "title": "Lesson 1 - HTML Basics",
      "type": "VIDEO",
      "duration": 25,
      "url": "https://..."
    }
  ],
  "reviews": [
    {
      "id": 1,
      "rating": 5,
      "comment": "Excellent course!",
      "author": "John Doe",
      "createdAt": "2025-02-01T08:00:00Z"
    }
  ],
  "createdAt": "2025-01-15T10:00:00Z"
}
```

---

### 2.3 Create Course (Instructor)

**Endpoint:** `POST /courses`

**Description:** Create a new course (requires INSTRUCTOR role)

**Request Body:**
```json
{
  "title": "Advanced React Patterns",
  "description": "Learn advanced React concepts",
  "longDescription": "Comprehensive course on React hooks, context...",
  "category": "Technology",
  "level": "ADVANCED",
  "duration": 50,
  "price": 49.99,
  "currency": "USD",
  "objectives": [
    "Master React hooks",
    "Implement context API",
    "Optimize performance"
  ],
  "requirements": [
    "Knowledge of React basics",
    "JavaScript ES6+"
  ],
  "imageUrl": "https://...",
  "isPublished": false
}
```

**Response (201 Created):**
```json
{
  "id": 5,
  "title": "Advanced React Patterns",
  "status": "DRAFT",
  "message": "Course created successfully"
}
```

---

### 2.4 Update Course

**Endpoint:** `PUT /courses/{courseId}`

**Description:** Update course details (only instructor who created it)

**Path Parameters:**
```
courseId: 1 (required)
```

**Request Body:**
```json
{
  "title": "Advanced React Patterns - Updated",
  "description": "Updated description",
  "price": 59.99
}
```

**Response (200 OK):**
```json
{
  "id": 5,
  "message": "Course updated successfully"
}
```

---

### 2.5 Delete Course

**Endpoint:** `DELETE /courses/{courseId}`

**Description:** Delete course (only instructor or admin)

**Path Parameters:**
```
courseId: 1 (required)
```

**Response (204 No Content)**

---

### 2.6 Enroll in Course

**Endpoint:** `POST /courses/{courseId}/enroll`

**Description:** Enroll the current user in a course

**Path Parameters:**
```
courseId: 1 (required)
```

**Request Body (optional for free courses):**
```json
{
  "paymentMethodId": "pm_123456"  // Required for paid courses
}
```

**Response (200 OK):**
```json
{
  "enrollmentId": 10,
  "courseId": 1,
  "userId": 3,
  "enrollmentDate": "2025-02-12T10:30:00Z",
  "status": "ACTIVE",
  "progressPercentage": 0,
  "message": "Successfully enrolled in course"
}
```

---

### 2.7 Get Course Materials

**Endpoint:** `GET /courses/{courseId}/materials`

**Description:** Get all course materials and lessons

**Path Parameters:**
```
courseId: 1 (required)
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Lesson 1 - HTML Basics",
    "type": "VIDEO",
    "duration": 25,
    "url": "https://...",
    "order": 1,
    "completed": true
  },
  {
    "id": 2,
    "title": "HTML Quiz",
    "type": "QUIZ",
    "duration": 15,
    "quizId": 1,
    "order": 2,
    "completed": false
  }
]
```

---

## 3. User Endpoints

### 3.1 Get User Profile

**Endpoint:** `GET /users/me`

**Description:** Get current logged-in user's profile

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "STUDENT",
  "bio": "I'm passionate about learning",
  "profileImage": "https://...",
  "joinedAt": "2025-01-10T08:00:00Z",
  "enrolledCoursesCount": 5,
  "completedCoursesCount": 2,
  "learningStreak": 15,
  "totalLearningHours": 120
}
```

---

### 3.2 Update User Profile

**Endpoint:** `PUT /users/me`

**Description:** Update current user's profile

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "bio": "Updated bio",
  "profileImage": "https://..."
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "message": "Profile updated successfully"
}
```

---

### 3.3 Get Learning History

**Endpoint:** `GET /users/me/history`

**Description:** Get user's learning history and interactions

**Query Parameters:**
```
page=0
size=20
courseId=1 (optional)
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "courseId": 1,
    "courseName": "Web Development 101",
    "action": "VIEW",
    "materialId": 1,
    "materialName": "Lesson 1",
    "timestamp": "2025-02-12T10:00:00Z",
    "duration": 25
  }
]
```

---

### 3.4 Get User Progress

**Endpoint:** `GET /users/me/progress`

**Description:** Get progress for all enrolled courses

**Response (200 OK):**
```json
{
  "enrollments": [
    {
      "courseId": 1,
      "courseTitle": "Web Development 101",
      "progressPercentage": 45,
      "completedLessons": 11,
      "totalLessons": 25,
      "enrollmentDate": "2025-01-15T08:00:00Z",
      "estimatedCompletionDate": "2025-03-15T08:00:00Z"
    }
  ],
  "totalProgressPercentage": 40,
  "totalEnrolledCourses": 5,
  "totalCompletedCourses": 2
}
```

---

### 3.5 Get User Achievements

**Endpoint:** `GET /users/me/achievements`

**Description:** Get user's badges and certificates

**Response (200 OK):**
```json
{
  "badges": [
    {
      "id": 1,
      "name": "First Course",
      "description": "Complete your first course",
      "icon": "https://...",
      "unlockedAt": "2025-02-01T10:00:00Z"
    }
  ],
  "certificates": [
    {
      "id": 1,
      "courseId": 1,
      "courseName": "Web Development 101",
      "certificateUrl": "https://...",
      "issuedAt": "2025-02-01T10:00:00Z"
    }
  ]
}
```

---

## 4. Recommendation Endpoints

### 4.1 Get Personalized Recommendations

**Endpoint:** `GET /recommendations`

**Description:** Get AI-powered course recommendations for current user

**Query Parameters:**
```
limit=10          (default: 5)
algorithm=HYBRID  (optional: COLLABORATIVE, CONTENT_BASED, HYBRID)
```

**Response (200 OK):**
```json
{
  "recommendations": [
    {
      "courseId": 5,
      "title": "Advanced React Patterns",
      "category": "Technology",
      "level": "ADVANCED",
      "reason": "Based on your interest in JavaScript and React",
      "confidenceScore": 0.92,
      "instructorName": "Jane Smith",
      "enrollmentCount": 250,
      "rating": 4.9
    }
  ],
  "generatedAt": "2025-02-12T10:30:00Z"
}
```

---

### 4.2 Get Similar Courses

**Endpoint:** `GET /recommendations/similar/{courseId}`

**Description:** Get courses similar to a specific course

**Path Parameters:**
```
courseId: 1 (required)
```

**Query Parameters:**
```
limit=5 (default)
```

**Response (200 OK):**
```json
{
  "baseCourse": {
    "id": 1,
    "title": "Web Development 101"
  },
  "similarCourses": [
    {
      "courseId": 2,
      "title": "Advanced HTML & CSS",
      "similarity": 0.85
    }
  ]
}
```

---

## 5. Admin Endpoints

### 5.1 Get All Users (Admin)

**Endpoint:** `GET /admin/users`

**Description:** Get list of all users (admin only)

**Query Parameters:**
```
page=0
size=20
role=STUDENT      (optional: STUDENT, INSTRUCTOR, ADMIN)
status=ACTIVE     (optional: ACTIVE, BANNED, PENDING)
searchTerm=john   (optional)
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "role": "STUDENT",
      "status": "ACTIVE",
      "joinedAt": "2025-01-10T08:00:00Z",
      "enrolledCourses": 5
    }
  ],
  "totalElements": 500,
  "totalPages": 25
}
```

---

### 5.2 Ban User (Admin)

**Endpoint:** `POST /admin/users/{userId}/ban`

**Description:** Ban a user from the platform

**Path Parameters:**
```
userId: 1 (required)
```

**Request Body:**
```json
{
  "reason": "Inappropriate behavior"
}
```

**Response (200 OK):**
```json
{
  "message": "User banned successfully"
}
```

---

### 5.3 Get Analytics Dashboard

**Endpoint:** `GET /admin/analytics/dashboard`

**Description:** Get system-wide analytics data

**Response (200 OK):**
```json
{
  "totalUsers": 5000,
  "activeUsers": 1200,
  "totalCourses": 150,
  "totalEnrollments": 15000,
  "averageRating": 4.5,
  "revenueThisMonth": 50000,
  "dailyActiveUsers": [
    {
      "date": "2025-02-12",
      "count": 450
    }
  ],
  "topCourses": [
    {
      "courseId": 1,
      "title": "Web Development 101",
      "enrollments": 1200
    }
  ]
}
```

---

## 6. Error Responses

All error responses follow this format:

```json
{
  "error": "Error message",
  "code": "ERROR_CODE",
  "status": 400,
  "timestamp": "2025-02-12T10:30:00Z",
  "details": {
    "field": "value"
  }
}
```

### Common Error Codes

| Code | Status | Description |
|------|--------|-------------|
| `INVALID_CREDENTIALS` | 401 | Email or password incorrect |
| `UNAUTHORIZED` | 401 | Missing or invalid JWT token |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `EMAIL_EXISTS` | 400 | Email already registered |
| `INVALID_INPUT` | 400 | Invalid request parameters |
| `COURSE_FULL` | 400 | Course enrollment limit reached |
| `ALREADY_ENROLLED` | 400 | User already enrolled in course |
| `INTERNAL_ERROR` | 500 | Server error |

---

## 7. Rate Limiting

API implements rate limiting to prevent abuse:

```
Rate Limit: 1000 requests per hour per API key
```

Rate limit headers:
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1707744000
```

---

## 8. Pagination

All list endpoints support pagination with:

```
page: 0-indexed page number
size: Items per page (max 100)
```

Response structure:
```json
{
  "content": [...],
  "totalElements": 500,
  "totalPages": 25,
  "currentPage": 0,
  "hasNext": true,
  "hasPrevious": false
}
```

---

## 9. Filtering & Sorting

Supported sorting:
```
sort=field,direction
sort=title,asc
sort=createdAt,desc
sort=rating,desc
```

Multiple sorts:
```
sort=rating,desc&sort=enrollmentCount,desc
```

---

**Last Updated**: February 12, 2026
**API Version**: 1.0
