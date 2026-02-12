# EduSmart – Setup & Installation Guide

Complete step-by-step guide for setting up EduSmart locally and in production.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Docker Setup](#docker-setup)
4. [Production Deployment](#production-deployment)
5. [Configuration](#configuration)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### System Requirements

- **RAM**: Minimum 4GB (8GB recommended)
- **Storage**: 10GB free space
- **OS**: Windows, macOS, or Linux

### Software Requirements

| Software | Version | Purpose |
|----------|---------|---------|
| Java JDK | 17+ | Backend runtime |
| Maven | 3.6+ | Java build tool |
| Node.js | 16+ | Frontend runtime |
| npm/yarn | Latest | Node package manager |
| MySQL | 8.0+ | Database |
| Python | 3.9+ | AI models |
| Docker | 20.10+ | Containerization |
| Git | 2.30+ | Version control |

---

## Local Development Setup

### Step 1: Clone Repository

```bash
# Clone the repository
git clone https://github.com/yourusername/edusmart.git
cd edusmart

# Or clone with SSH
git clone git@github.com:yourusername/edusmart.git
cd edusmart
```

### Step 2: Backend Setup

#### 2.1 Install Java 17

**Windows:**
```bash
# Using Chocolatey
choco install openjdk17

# Or download from Oracle: https://www.oracle.com/java/technologies/javase-jdk17-downloads.html
```

**macOS:**
```bash
# Using Homebrew
brew install openjdk@17

# Add to PATH
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

**Linux:**
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install openjdk-17-jdk

# Verify
java -version
```

#### 2.2 Install Maven

**Windows:**
```bash
# Using Chocolatey
choco install maven

# Or download: https://maven.apache.org/download.cgi
```

**macOS:**
```bash
brew install maven

# Verify
mvn -version
```

**Linux:**
```bash
sudo apt-get install maven

# Verify
mvn -version
```

#### 2.3 Setup MySQL Database

**Windows:**
```bash
# Using Chocolatey
choco install mysql

# Or download: https://dev.mysql.com/downloads/mysql/
```

**macOS:**
```bash
# Using Homebrew
brew install mysql

# Start MySQL service
brew services start mysql

# Verify
mysql --version
```

**Linux:**
```bash
# Ubuntu/Debian
sudo apt-get install mysql-server

# Start service
sudo systemctl start mysql

# Verify
mysql --version
```

**Create Database:**
```bash
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE edusmart_db;
CREATE USER 'edusmart_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON edusmart_db.* TO 'edusmart_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# Import schema
mysql -u edusmart_user -p edusmart_db < database/schema.sql
```

#### 2.4 Configure Backend

```bash
cd backend

# Edit application.properties
# Windows: Open with notepad
notepad src/main/resources/application.properties

# macOS/Linux: Use nano or vim
nano src/main/resources/application.properties
```

**Configure these properties:**
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/edusmart_db
spring.datasource.username=edusmart_user
spring.datasource.password=password123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=your_very_secure_jwt_secret_key_minimum_32_characters_here
jwt.expiration=86400000
jwt.refreshExpiration=604800000

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Logging
logging.level.root=INFO
logging.level.com.elearning=DEBUG
```

#### 2.5 Build and Run Backend

```bash
# Build project
mvn clean install

# Run development server
mvn spring-boot:run

# Or run JAR directly after build
java -jar target/elearning-platform-0.0.1-SNAPSHOT.jar
```

Backend will start on: `http://localhost:8080/api`

---

### Step 3: Frontend Setup

#### 3.1 Install Node.js and npm

**Windows/macOS/Linux:**
Download from https://nodejs.org/

```bash
# Verify installation
node --version
npm --version
```

#### 3.2 Configure Frontend

```bash
cd frontend

# Create .env file
# Windows
type nul > .env

# macOS/Linux
touch .env
```

**Add these variables to `.env`:**
```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENVIRONMENT=development
REACT_APP_LOG_LEVEL=debug
```

#### 3.3 Install Dependencies and Run

```bash
# Install dependencies
npm install

# Start development server
npm start

# Or use yarn
yarn install
yarn start
```

Frontend will start on: `http://localhost:3000`

---

### Step 4: AI Model Setup (Optional)

```bash
cd ai-model

# Create virtual environment
python -m venv venv

# Activate virtual environment
# Windows
venv\Scripts\activate

# macOS/Linux
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Launch Jupyter Notebook
jupyter notebook

# Open: E_Learning_AI_Recommendation_System.ipynb
```

---

## Docker Setup

### Quick Start with Docker Compose

#### Prerequisites
- Docker Desktop installed and running

#### 1. Build and Run

```bash
# From root directory
docker-compose up -d

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

#### 2. Access Services

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **MySQL**: localhost:3306

#### 3. Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (careful!)
docker-compose down -v
```

### Manual Docker Build

#### Build Individual Services

**Backend:**
```bash
cd backend

# Build Docker image
docker build -t edusmart-backend:latest .

# Run container
docker run \
  -p 8080:8080 \
  --env-file ../.env \
  --name edusmart-backend \
  edusmart-backend:latest
```

**Frontend:**
```bash
cd frontend

# Build Docker image
docker build -t edusmart-frontend:latest .

# Run container
docker run \
  -p 3000:3000 \
  --name edusmart-frontend \
  edusmart-frontend:latest
```

**MySQL:**
```bash
# Run MySQL container
docker run \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=edusmart_db \
  --name edusmart-mysql \
  -d mysql:8.0
```

---

## Production Deployment

### Prerequisites

- Linux server (Ubuntu 20.04+ recommended)
- Docker and Docker Compose installed
- SSL/TLS certificate (Let's Encrypt recommended)
- Domain name

### Step 1: Server Setup

```bash
# Update system
sudo apt-get update
sudo apt-get upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

### Step 2: Deploy Application

```bash
# Clone repository
git clone https://github.com/yourusername/edusmart.git /opt/edusmart
cd /opt/edusmart

# Create production .env
sudo nano .env.production

# Deploy with production compose file
docker-compose -f docker-compose.prod.yml up -d
```

**Production .env** (`.env.production`):
```env
# Database
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DATABASE=edusmart_db
MYSQL_USER=edusmart_user
MYSQL_PASSWORD=secure_password_here

# Backend
SPRING_PROFILES_ACTIVE=production
JWT_SECRET=your_very_secure_jwt_secret_for_production
JWT_EXPIRATION=86400000

# Frontend
REACT_APP_API_URL=https://api.yourdomain.com
REACT_APP_ENVIRONMENT=production

# Email (if applicable)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=app_password
```

### Step 3: SSL/TLS Setup

Using Let's Encrypt with Certbot:

```bash
# Install Certbot
sudo apt-get install certbot python3-certbot-nginx

# Generate certificate
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# Configure auto-renewal
sudo systemctl enable certbot.timer
sudo systemctl start certbot.timer
```

### Step 4: Nginx Reverse Proxy

```bash
# Create Nginx config
sudo nano /etc/nginx/sites-available/edusmart
```

**Nginx Configuration:**
```nginx
upstream backend {
    server localhost:8080;
}

upstream frontend {
    server localhost:3000;
}

server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    # Frontend
    location / {
        proxy_pass http://frontend;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }

    # API
    location /api {
        proxy_pass http://backend;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/edusmart /etc/nginx/sites-enabled/

# Test Nginx config
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx
```

---

## Configuration

### Environment Variables

#### Backend (application.properties or .env)

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/edusmart_db
spring.datasource.username=edusmart_user
spring.datasource.password=password

# JWT
jwt.secret=your_secret_key
jwt.expiration=86400000

# Server
server.port=8080
server.servlet.context-path=/api

# Logging
logging.level.root=INFO
```

#### Frontend (.env)

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENVIRONMENT=development
```

#### Python (.env or environment)

```bash
FLASK_ENV=development
DATABASE_URL=mysql://user:pass@localhost:3306/edusmart_db
```

---

## Troubleshooting

### Issue: MySQL Connection Error

```
ERROR: Connection refused (localhost:3306)
```

**Solution:**
```bash
# Check if MySQL is running
sudo systemctl status mysql

# Start MySQL if not running
sudo systemctl start mysql

# Verify connection
mysql -u root -p -e "SELECT 1"
```

### Issue: Port Already in Use

```
ERROR: Address already in use :8080
```

**Solution:**
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>

# Or change port in application.properties
server.port=8081
```

### Issue: CORS Errors

**Solution:**
```java
// Configure CORS in Spring Boot
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true);
    }
}
```

### Issue: Node Modules Issues

```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and lock file
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

### Issue: Database Migration Failure

```bash
# Check migration history
mysql -u root -p edusmart_db -e "SELECT * FROM schema_version;"

# Reset database (development only)
mysql -u root -p edusmart_db < database/schema.sql
```

---

## Monitoring & Logs

### View Application Logs

**Docker:**
```bash
# Backend logs
docker-compose logs backend

# Frontend logs
docker-compose logs frontend

# Follow logs
docker-compose logs -f
```

**Local Development:**
```bash
# Backend: Check console output
# Frontend: Check browser console (F12)
# Python: Check terminal output
```

### Database Logs

```bash
# MySQL error log
sudo tail -f /var/log/mysql/error.log
```

---

**Last Updated**: February 12, 2026
