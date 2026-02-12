# 🏗️ Architecture Logique Front-End React - Pattern MVVM
## Plateforme E-Learning

---

## 📋 Vue d'Ensemble

Cette plateforme e-learning utilise une **architecture MVVM (Model-View-ViewModel)** adaptée à React, avec des patterns modernes pour assurer la séparation des préoccupations, la réutilisabilité et la maintenabilité.

---

## 🎯 Architecture MVVM - Composants Principaux

```
┌─────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    VIEW (Components)                      │  │
│  │  • React Components (.tsx)                               │  │
│  │  • UI Components (Presentational)                        │  │
│  │  • Layout Components                                     │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              ↕                                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                  VIEWMODEL (Hooks/State)                  │  │
│  │  • React Hooks (useState, useEffect)                     │  │
│  │  • Custom Hooks                                          │  │
│  │  • Context API (State Management)                        │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↕
┌─────────────────────────────────────────────────────────────────┐
│                        BUSINESS LAYER                            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    SERVICES (Business Logic)              │  │
│  │  • courseService.ts                                      │  │
│  │  • authService.ts                                        │  │
│  │  • chatService.ts                                        │  │
│  │  • analyticsService.ts                                   │  │
│  │  • etc.                                                  │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↕
┌─────────────────────────────────────────────────────────────────┐
│                          DATA LAYER                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    MODEL (Data Structures)                │  │
│  │  • TypeScript Interfaces                                 │  │
│  │  • Type Definitions                                      │  │
│  │  • API Client (Axios)                                    │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              ↕                                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              BACKEND API (Spring Boot)                    │  │
│  │  • REST API (http://localhost:8080/api)                  │  │
│  │  • WebSocket (Chat, Notifications)                       │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📁 Structure des Dossiers (MVVM)

```
frontend/src/
│
├── 📂 components/              # VIEW LAYER
│   ├── 📂 auth/                # Authentication Views
│   │   ├── Login.tsx
│   │   ├── Register.tsx
│   │   └── VerifyOtp.tsx
│   │
│   ├── 📂 courses/             # Course Management Views
│   │   ├── CourseList.tsx
│   │   ├── CourseDetail.tsx
│   │   └── CourseCard.tsx
│   │
│   ├── 📂 chat/                # Real-time Chat Views
│   │   ├── Chat.tsx
│   │   └── ChatStatic.tsx      # Static Demo
│   │
│   ├── 📂 analytics/           # Analytics & BI Views
│   │   ├── AnalyticsDashboard.tsx
│   │   └── AnalyticsDashboardStatic.tsx  # Static Demo
│   │
│   ├── 📂 admin/               # Admin Panel Views
│   │   └── AdminDashboard.tsx
│   │
│   ├── 📂 instructor/          # Instructor Views
│   │   ├── InstructorDashboard.tsx
│   │   ├── CreateCourse.tsx
│   │   └── CreateQuiz.tsx
│   │
│   ├── 📂 gamification/        # Gamification Views
│   │   ├── GamificationDashboard.tsx
│   │   ├── Leaderboard.tsx
│   │   └── Achievements.tsx
│   │
│   ├── 📂 ai/                  # AI-Powered Features
│   │   ├── RecommendationsPage.tsx
│   │   ├── TrendingPage.tsx
│   │   └── InsightsPage.tsx
│   │
│   ├── 📂 layout/              # Layout Components
│   │   └── Navbar.tsx
│   │
│   ├── 📂 common/              # Reusable UI Components
│   │   └── PrivateRoute.tsx
│   │
│   └── Dashboard.tsx           # Main Dashboard View
│
├── 📂 services/                # BUSINESS LOGIC LAYER (ViewModel Logic)
│   ├── api.ts                  # Axios Instance + Interceptors
│   ├── authService.ts          # Authentication Business Logic
│   ├── courseService.ts        # Course CRUD Operations
│   ├── chatService.ts          # Chat Operations (REST + Mock)
│   ├── analyticsService.ts     # Analytics Data Fetching
│   ├── progressService.ts      # Progress Tracking
│   ├── quizService.ts          # Quiz Management
│   ├── gamificationService.ts  # Gamification Logic
│   ├── paymentService.ts       # Payment Integration (Stripe)
│   ├── adminService.ts         # Admin Operations
│   ├── userService.ts          # User Management
│   ├── socket.ts               # WebSocket Management
│   └── powerBIService.ts       # Power BI Integration
│
├── 📂 contexts/                # STATE MANAGEMENT (ViewModel)
│   └── ThemeContext.tsx        # Global Theme State
│
├── 📂 data/                    # MOCK DATA (for demos)
│   └── mockData.ts             # Static Data for Demos
│
├── 📂 styles/                  # Global Styles
│   └── *.css
│
└── App.tsx                     # Root Component + Routing
```

---

## 🔄 Flux de Données MVVM

### 1️⃣ **VIEW → VIEWMODEL → MODEL**
```typescript
// Exemple: CourseList Component

// VIEW (Component)
const CourseList: React.FC = () => {
  // VIEWMODEL (State & Logic)
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    loadCourses();  // Appel du service
  }, []);
  
  const loadCourses = async () => {
    try {
      // Appel du SERVICE (Business Logic)
      const data = await courseService.getPublicCourses();
      setCourses(data);  // Mise à jour du state
    } catch (error) {
      toast.error('Failed to load courses');
    } finally {
      setLoading(false);
    }
  };
  
  // VIEW (Rendering)
  return (
    <div>
      {courses.map(course => (
        <CourseCard key={course.id} course={course} />
      ))}
    </div>
  );
};
```

### 2️⃣ **SERVICE → API → BACKEND**
```typescript
// services/courseService.ts

// MODEL (Interface)
export interface Course {
  id: number;
  title: string;
  description: string;
  price: number;
  // ... autres champs
}

// BUSINESS LOGIC
export const courseService = {
  getPublicCourses: async (): Promise<Course[]> => {
    // Appel API via Axios
    const response = await api.get('/courses/public');
    return response.data;  // Retourne le MODEL
  },
  
  getCourseById: async (courseId: number): Promise<Course> => {
    const response = await api.get(`/courses/${courseId}`);
    return response.data;
  },
  
  createCourse: async (courseData: CourseRequest): Promise<Course> => {
    const response = await api.post('/courses', courseData);
    return response.data;
  }
};
```

### 3️⃣ **API CLIENT (Axios Interceptors)**
```typescript
// services/api.ts

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' }
});

// Intercepteur REQUEST (Ajoute le token JWT)
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Intercepteur RESPONSE (Gestion des erreurs)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Redirection vers login si token expiré
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## 🧩 Modules Fonctionnels (Domain-Driven)

### 🔐 **1. Module Authentication**
```
View:           components/auth/Login.tsx
                components/auth/Register.tsx
ViewModel:      useState, useNavigate hooks
Service:        services/authService.ts
Model:          User interface
API:            POST /api/auth/login
                POST /api/auth/register
```

### 📚 **2. Module Courses**
```
View:           components/courses/CourseList.tsx
                components/courses/CourseDetail.tsx
ViewModel:      useState, useEffect, useParams
Service:        services/courseService.ts
Model:          Course, CourseRequest interfaces
API:            GET /api/courses/public
                GET /api/courses/{id}
                POST /api/courses
```

### 💬 **3. Module Chat (Real-time)**
```
View:           components/chat/Chat.tsx
                components/chat/ChatStatic.tsx
ViewModel:      useState, useEffect, useRef
Service:        services/chatService.ts
                services/socket.ts (WebSocket)
Model:          ChatMessage, ChatRoom interfaces
API:            GET /api/chat/rooms
                POST /api/chat/messages
WebSocket:      STOMP over SockJS
```

### 📊 **4. Module Analytics**
```
View:           components/analytics/AnalyticsDashboard.tsx
                components/analytics/AnalyticsDashboardStatic.tsx
ViewModel:      useState, useEffect
Service:        services/analyticsService.ts
Model:          UserAnalytics, CourseAnalytics interfaces
API:            GET /api/analytics/user
                GET /api/analytics/course
Charts:         Chart.js + react-chartjs-2
```

### 🎮 **5. Module Gamification**
```
View:           components/gamification/GamificationDashboard.tsx
                components/gamification/Leaderboard.tsx
                components/achievements/Achievements.tsx
ViewModel:      useState, useEffect
Service:        services/gamificationService.ts
Model:          Achievement, Leaderboard interfaces
API:            GET /api/gamification/achievements
                GET /api/gamification/leaderboard
```

### 🤖 **6. Module AI (Recommendations)**
```
View:           components/ai/RecommendationsPage.tsx
                components/ai/TrendingPage.tsx
                components/ai/InsightsPage.tsx
ViewModel:      useState, useEffect
Service:        services/courseService.ts (AI endpoints)
Model:          Course, Recommendation interfaces
API:            GET /api/courses/recommendations
                GET /api/courses/trending
                GET /api/ai/insights
Backend:        Python ML Models (SVD, NMF, ALS, Deep Learning)
```

### 👨‍🏫 **7. Module Instructor**
```
View:           components/instructor/InstructorDashboard.tsx
                components/instructor/CreateCourse.tsx
                components/instructor/CreateQuiz.tsx
ViewModel:      useState, useEffect, useNavigate
Service:        services/courseService.ts
                services/quizService.ts
Model:          Course, Quiz interfaces
API:            POST /api/courses
                POST /api/quizzes
```

### 👑 **8. Module Admin**
```
View:           components/admin/AdminDashboard.tsx
ViewModel:      useState, useEffect
Service:        services/adminService.ts
Model:          User, Course, Analytics interfaces
API:            GET /api/admin/users
                GET /api/admin/courses
                PUT /api/admin/users/{id}/role
```

### 💳 **9. Module Payment (Stripe)**
```
View:           components/courses/CourseDetail.tsx (Checkout)
ViewModel:      useState, useStripe, useElements
Service:        services/paymentService.ts
Model:          PaymentIntent interface
API:            POST /api/payments/create-intent
Integration:    Stripe.js
```

---

## 🎨 Patterns de Design Utilisés

### 1️⃣ **Container/Presentation Pattern**
```typescript
// Container Component (Logic)
const CourseListContainer: React.FC = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  
  useEffect(() => {
    loadCourses();
  }, []);
  
  const loadCourses = async () => {
    const data = await courseService.getPublicCourses();
    setCourses(data);
  };
  
  return <CourseListView courses={courses} />;
};

// Presentation Component (UI)
const CourseListView: React.FC<{courses: Course[]}> = ({ courses }) => {
  return (
    <div className="grid grid-cols-3 gap-4">
      {courses.map(course => <CourseCard course={course} />)}
    </div>
  );
};
```

### 2️⃣ **Custom Hooks Pattern**
```typescript
// Custom Hook pour réutiliser la logique
const useCourses = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    const loadCourses = async () => {
      try {
        const data = await courseService.getPublicCourses();
        setCourses(data);
      } finally {
        setLoading(false);
      }
    };
    loadCourses();
  }, []);
  
  return { courses, loading };
};

// Usage dans un component
const CourseList: React.FC = () => {
  const { courses, loading } = useCourses();
  
  if (loading) return <Spinner />;
  return <CourseListView courses={courses} />;
};
```

### 3️⃣ **Service Layer Pattern**
Tous les appels API sont centralisés dans les services:
- ✅ Séparation des préoccupations
- ✅ Réutilisabilité du code
- ✅ Testabilité facilitée
- ✅ Gestion centralisée des erreurs

### 4️⃣ **Private Route Pattern**
```typescript
// components/common/PrivateRoute.tsx
const PrivateRoute: React.FC<Props> = ({ children, requiredRole }) => {
  const isAuthenticated = authService.isAuthenticated();
  const currentUser = authService.getCurrentUser();
  
  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }
  
  if (requiredRole && currentUser?.role !== requiredRole) {
    return <Navigate to="/dashboard" />;
  }
  
  return <>{children}</>;
};
```

### 5️⃣ **Mock Data Pattern (Static Demos)**
```typescript
// data/mockData.ts
export const mockCourses = [...];
export const mockUsers = [...];
export const mockKPIs = {...};

// services/chatService.ts (Mode Static)
export const chatService = {
  getRooms: async () => {
    await simulateNetworkDelay(500);
    return Promise.resolve(mockChatRooms);
  }
};
```

---

## 🔌 Intégrations Externes

### 1️⃣ **Backend REST API**
```
Base URL: http://localhost:8080/api
Auth: JWT Bearer Token
Format: JSON
```

### 2️⃣ **WebSocket (STOMP over SockJS)**
```typescript
// services/socket.ts
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const socket = new SockJS('http://localhost:8080/ws');
const stompClient = new Client({
  webSocketFactory: () => socket,
  // ...
});
```

### 3️⃣ **Stripe Payment**
```typescript
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';

const stripePromise = loadStripe('pk_test_...');
```

### 4️⃣ **Chart.js (Analytics)**
```typescript
import { Chart } from 'chart.js';
import { Line, Bar, Pie, Doughnut } from 'react-chartjs-2';
```

### 5️⃣ **Power BI Embedded**
```typescript
import { models, Report } from 'powerbi-client';
```

---

## 🔒 Sécurité & Authentification

### JWT Token Flow
```
1. Login → POST /api/auth/login
2. Backend retourne JWT token
3. Token stocké dans localStorage
4. Axios interceptor ajoute "Authorization: Bearer {token}"
5. Backend valide le token à chaque requête
6. Si token expiré → 401 → Redirection /login
```

### Protection des Routes
```typescript
<Route path="/dashboard" element={
  <PrivateRoute>
    <Dashboard />
  </PrivateRoute>
} />

<Route path="/admin/*" element={
  <PrivateRoute requiredRole="ADMIN">
    <AdminDashboard />
  </PrivateRoute>
} />
```

---

## 📊 Gestion d'État

### 1️⃣ **Local State (useState)**
Pour les états spécifiques aux composants
```typescript
const [courses, setCourses] = useState<Course[]>([]);
const [loading, setLoading] = useState(true);
```

### 2️⃣ **Context API (Global State)**
Pour les états partagés (Thème, User)
```typescript
// contexts/ThemeContext.tsx
const ThemeContext = React.createContext<ThemeContextType>({});

export const ThemeProvider: React.FC = ({ children }) => {
  const [theme, setTheme] = useState('light');
  return (
    <ThemeContext.Provider value={{ theme, setTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};
```

### 3️⃣ **LocalStorage (Persistence)**
Pour la persistance des données
```typescript
localStorage.setItem('token', token);
localStorage.setItem('user', JSON.stringify(user));
```

---

## 🚀 Composants Statiques (Démo Mode)

### Dashboard BI Statique
```
Route: /dashboard-bi
Component: AnalyticsDashboardStatic.tsx
Service: mockData.ts (pas d'API)
Features:
  - 4 KPI Cards
  - Line Chart (Enrollment Trends)
  - Bar Chart (Courses by Category)
  - Pie Chart (Users by Role)
  - Donut Chart (Progress Status)
  - Recent Activities
```

### Chat Statique
```
Route: /chat-demo
Component: ChatStatic.tsx
Service: mockData.ts + auto-reply simulation
Features:
  - 3 Mock Chat Rooms
  - Message Sending
  - Auto-Reply (1-2s delay)
  - Typing Indicator
  - Emoji Picker
  - Unread Count
```

---

## 📦 Dépendances Principales

```json
{
  "dependencies": {
    "react": "^19.1.1",
    "react-dom": "^19.1.1",
    "react-router-dom": "^7.8.2",
    "axios": "^1.11.0",
    "socket.io-client": "^4.8.1",
    "@stomp/stompjs": "^7.0.0",
    "sockjs-client": "1.6.1",
    "chart.js": "^4.5.0",
    "react-chartjs-2": "^5.3.0",
    "@stripe/stripe-js": "^7.9.0",
    "@stripe/react-stripe-js": "^4.0.0",
    "powerbi-client": "^2.23.1",
    "react-toastify": "^11.0.5",
    "@heroicons/react": "^2.2.0",
    "tailwindcss": "^3.4.17"
  }
}
```

---

## 🎯 Avantages de cette Architecture

✅ **Séparation des préoccupations** - Vue, Logique, Données séparées  
✅ **Réutilisabilité** - Services et composants réutilisables  
✅ **Testabilité** - Facile à tester unitairement  
✅ **Maintenabilité** - Code organisé et facile à maintenir  
✅ **Scalabilité** - Facile d'ajouter de nouveaux modules  
✅ **Type Safety** - TypeScript pour éviter les erreurs  
✅ **Performance** - Optimisation avec React hooks  
✅ **Démo Mode** - Composants statiques sans backend  

---

## 📝 Résumé de l'Architecture

```
┌─────────────────────────────────────────────────────────┐
│  React Components (VIEW)                                 │
│  ↓ useState, useEffect                                   │
│  Services (BUSINESS LOGIC)                               │
│  ↓ Axios HTTP Client                                     │
│  REST API (Spring Boot Backend)                          │
│  ↓ JPA/Hibernate                                         │
│  MySQL Database                                          │
└─────────────────────────────────────────────────────────┘

+ WebSocket (Real-time: Chat, Notifications)
+ Stripe (Payments)
+ Power BI (Analytics)
+ AI Models (Python: Recommendations)
```

---

**🎓 Cette architecture suit les meilleures pratiques React et garantit une application maintenable, scalable et performante!**
