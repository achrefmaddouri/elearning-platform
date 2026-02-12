-- E-Learning Platform Database Schema

CREATE DATABASE IF NOT EXISTS elearning_platform;
USE elearning_platform;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL,
    role ENUM('ADMIN', 'INSTRUCTOR', 'USER') DEFAULT 'USER',
    status ENUM('PENDING', 'ACTIVE', 'BANNED') DEFAULT 'PENDING',
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Courses table
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    instructor_id BIGINT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'DECLINED', 'ARCHIVED') DEFAULT 'PENDING',
    visibility ENUM('PUBLIC', 'PRIVATE') DEFAULT 'PUBLIC',
    price DECIMAL(10, 2) DEFAULT 0.00,
    is_free BOOLEAN DEFAULT TRUE,
    private_url VARCHAR(255) UNIQUE,
    thumbnail_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Course files table
CREATE TABLE course_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type ENUM('VIDEO', 'IMAGE', 'PDF', 'DOCUMENT') NOT NULL,
    file_size BIGINT,
    course_id BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Video materials table
CREATE TABLE video_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    mime_type VARCHAR(100),
    file_size BIGINT,
    duration BIGINT,
    thumbnail_path VARCHAR(500),
    order_index INT DEFAULT 0,
    course_id BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Enrollments table
CREATE TABLE enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_enrollment (user_id, course_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Progress table
CREATE TABLE progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    progress_percentage INT DEFAULT 0,
    is_completed BOOLEAN DEFAULT FALSE,
    certificate_url VARCHAR(255),
    last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    UNIQUE KEY unique_progress (user_id, course_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Quizzes table
CREATE TABLE quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    course_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Quiz questions table
CREATE TABLE quiz_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question TEXT NOT NULL,
    correct_answer INT NOT NULL,
    quiz_id BIGINT NOT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Quiz options table
CREATE TABLE quiz_options (
    question_id BIGINT NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE
);

-- Quiz attempts table
CREATE TABLE quiz_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Quiz answers table
CREATE TABLE quiz_answers (
    attempt_id BIGINT NOT NULL,
    answer_index INT NOT NULL,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE
);

-- Chat rooms table
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_chat_room (course_id, user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Chat messages table
CREATE TABLE chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- OTP codes table
CREATE TABLE otp_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE
);

-- User points and streaks table
CREATE TABLE user_gamification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    total_points INT DEFAULT 0,
    current_login_streak INT DEFAULT 0,
    longest_login_streak INT DEFAULT 0,
    last_login_date DATE,
    current_course_streak INT DEFAULT 0,
    current_quiz_streak INT DEFAULT 0,
    streak_freeze_tokens INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Badge definitions table
CREATE TABLE badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    badge_type ENUM('LEARNING', 'STREAK', 'SUBJECT', 'SOCIAL', 'SPECIAL') NOT NULL,
    category VARCHAR(50),
    points_required INT DEFAULT 0,
    condition_type VARCHAR(50), -- 'COURSE_COMPLETE', 'QUIZ_PASS', 'STREAK', 'POINTS', etc.
    condition_value INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User badges table (many-to-many)
CREATE TABLE user_badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    badge_id BIGINT NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_displayed BOOLEAN DEFAULT TRUE,
    UNIQUE KEY unique_user_badge (user_id, badge_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE CASCADE
);

-- Points transactions table
CREATE TABLE points_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    points INT NOT NULL,
    transaction_type ENUM('EARNED', 'SPENT', 'BONUS', 'PENALTY') NOT NULL,
    source_type ENUM('COURSE_COMPLETE', 'QUIZ_PASS', 'LOGIN_STREAK', 'DAILY_LOGIN', 'BADGE_EARNED', 'PURCHASE', 'ADMIN_ADJUSTMENT') NOT NULL,
    source_id BIGINT, -- References course_id, quiz_id, etc.
    description TEXT,
    multiplier DECIMAL(3,2) DEFAULT 1.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Leaderboards (cached for performance)
CREATE TABLE leaderboards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    leaderboard_type ENUM('GLOBAL', 'COURSE', 'MONTHLY', 'WEEKLY') NOT NULL,
    reference_id BIGINT, -- course_id for course leaderboards, NULL for global
    points INT NOT NULL,
    rank_position INT NOT NULL,
    period_start DATE,
    period_end DATE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Rewards store items
CREATE TABLE rewards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    reward_type ENUM('CERTIFICATE', 'BADGE', 'DISCOUNT', 'ACCESS', 'PHYSICAL') NOT NULL,
    points_cost INT NOT NULL,
    discount_percentage DECIMAL(5,2), -- For discount rewards
    course_id BIGINT, -- For course-specific rewards
    is_active BOOLEAN DEFAULT TRUE,
    stock_quantity INT DEFAULT -1, -- -1 for unlimited
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL
);

-- User reward purchases
CREATE TABLE user_rewards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reward_id BIGINT NOT NULL,
    points_spent INT NOT NULL,
    status ENUM('PENDING', 'DELIVERED', 'EXPIRED') DEFAULT 'PENDING',
    redemption_code VARCHAR(100),
    expires_at TIMESTAMP NULL,
    redeemed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reward_id) REFERENCES rewards(id) ON DELETE CASCADE
);

-- Course categories for subject badges
CREATE TABLE course_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add category to courses table
ALTER TABLE courses ADD COLUMN category_id BIGINT NULL,
ADD FOREIGN KEY (category_id) REFERENCES course_categories(id) ON DELETE SET NULL;

-- Insert default admin user
INSERT INTO users (first_name, last_name, email, password, role, status, email_verified) 
VALUES ('Admin', 'User', 'admin@elearning.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', 'ACTIVE', TRUE);
-- Password: password (hashed with BCrypt)

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_courses_instructor ON courses(instructor_id);
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_courses_visibility ON courses(visibility);
CREATE INDEX idx_courses_category ON courses(category_id);
CREATE INDEX idx_course_files_course ON course_files(course_id);
CREATE INDEX idx_video_materials_course ON video_materials(course_id);
CREATE INDEX idx_video_materials_order ON video_materials(course_id, order_index);
CREATE INDEX idx_enrollments_user ON enrollments(user_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_progress_user ON progress(user_id);
CREATE INDEX idx_progress_course ON progress(course_id);
CREATE INDEX idx_chat_messages_room ON chat_messages(chat_room_id);
CREATE INDEX idx_chat_messages_sender ON chat_messages(sender_id);
CREATE INDEX idx_quiz_questions_quiz ON quiz_questions(quiz_id);
CREATE INDEX idx_quiz_attempts_user ON quiz_attempts(user_id);
CREATE INDEX idx_quiz_attempts_quiz ON quiz_attempts(quiz_id);

-- Gamification indexes
CREATE INDEX idx_user_gamification_user ON user_gamification(user_id);
CREATE INDEX idx_user_badges_user ON user_badges(user_id);
CREATE INDEX idx_user_badges_badge ON user_badges(badge_id);
CREATE INDEX idx_points_transactions_user ON points_transactions(user_id);
CREATE INDEX idx_points_transactions_type ON points_transactions(source_type);
CREATE INDEX idx_leaderboards_type ON leaderboards(leaderboard_type);
CREATE INDEX idx_leaderboards_user ON leaderboards(user_id);
CREATE INDEX idx_leaderboards_rank ON leaderboards(leaderboard_type, rank_position);
CREATE INDEX idx_user_rewards_user ON user_rewards(user_id);
CREATE INDEX idx_user_rewards_status ON user_rewards(status);

-- Insert default course categories
INSERT INTO course_categories (name, description) VALUES
('Programming', 'Programming and software development courses'),
('Design', 'Graphic design, UI/UX, and creative courses'),
('Business', 'Business, marketing, and entrepreneurship'),
('Languages', 'Foreign language learning courses'),
('Science', 'Science, mathematics, and engineering'),
('Arts', 'Music, art, and creative expression'),
('Health', 'Health, fitness, and wellness courses'),
('Technology', 'Technology, AI, and digital skills'),
('Personal Development', 'Self-improvement and life skills'),
('Academic', 'Academic subjects and test preparation');

-- Insert default badges
INSERT INTO badges (name, description, badge_type, condition_type, condition_value, icon_url) VALUES
-- Learning Badges
('First Steps', 'Complete your first course', 'LEARNING', 'COURSE_COMPLETE', 1, '/badges/first-course.png'),
('Knowledge Seeker', 'Complete 5 courses', 'LEARNING', 'COURSE_COMPLETE', 5, '/badges/knowledge-seeker.png'),
('Learning Machine', 'Complete 10 courses', 'LEARNING', 'COURSE_COMPLETE', 10, '/badges/learning-machine.png'),
('Quiz Master', 'Pass 10 quizzes', 'LEARNING', 'QUIZ_PASS', 10, '/badges/quiz-master.png'),
('Perfect Score', 'Get 100% on any quiz', 'LEARNING', 'QUIZ_PERFECT', 1, '/badges/perfect-score.png'),
('Speed Learner', 'Complete a course in under 3 days', 'LEARNING', 'COURSE_SPEED', 3, '/badges/speed-learner.png'),

-- Streak Badges
('Consistent Learner', '7-day login streak', 'STREAK', 'LOGIN_STREAK', 7, '/badges/7-day-streak.png'),
('Dedicated Student', '15-day login streak', 'STREAK', 'LOGIN_STREAK', 15, '/badges/15-day-streak.png'),
('Learning Habit', '30-day login streak', 'STREAK', 'LOGIN_STREAK', 30, '/badges/30-day-streak.png'),
('Unstoppable', '60-day login streak', 'STREAK', 'LOGIN_STREAK', 60, '/badges/60-day-streak.png'),
('Quiz Streak', '5 quizzes in a row', 'STREAK', 'QUIZ_STREAK', 5, '/badges/quiz-streak.png'),

-- Subject Badges
('Code Warrior', 'Complete 3 programming courses', 'SUBJECT', 'CATEGORY_COMPLETE', 3, '/badges/code-warrior.png'),
('Design Guru', 'Complete 3 design courses', 'SUBJECT', 'CATEGORY_COMPLETE', 3, '/badges/design-guru.png'),
('Business Mind', 'Complete 3 business courses', 'SUBJECT', 'CATEGORY_COMPLETE', 3, '/badges/business-mind.png'),
('Polyglot', 'Complete 2 language courses', 'SUBJECT', 'CATEGORY_COMPLETE', 2, '/badges/polyglot.png'),

-- Social Badges
('Helpful Peer', 'Send 50 helpful chat messages', 'SOCIAL', 'CHAT_MESSAGES', 50, '/badges/helpful-peer.png'),
('Active Chatter', 'Participate in 10 course chats', 'SOCIAL', 'CHAT_PARTICIPATION', 10, '/badges/active-chatter.png'),

-- Point-based Badges
('Point Collector', 'Earn 1000 points', 'LEARNING', 'POINTS_EARNED', 1000, '/badges/point-collector.png'),
('Point Master', 'Earn 5000 points', 'LEARNING', 'POINTS_EARNED', 5000, '/badges/point-master.png'),
('Point Legend', 'Earn 10000 points', 'LEARNING', 'POINTS_EARNED', 10000, '/badges/point-legend.png');

-- Insert default rewards
INSERT INTO rewards (name, description, reward_type, points_cost, icon_url) VALUES
('Bronze Certificate Template', 'Unlock a special bronze certificate design', 'CERTIFICATE', 500, '/rewards/bronze-cert.png'),
('Silver Certificate Template', 'Unlock a premium silver certificate design', 'CERTIFICATE', 1000, '/rewards/silver-cert.png'),
('Gold Certificate Template', 'Unlock an exclusive gold certificate design', 'CERTIFICATE', 2000, '/rewards/gold-cert.png'),
('10% Course Discount', 'Get 10% off your next course purchase', 'DISCOUNT', 300, '/rewards/discount-10.png'),
('25% Course Discount', 'Get 25% off your next course purchase', 'DISCOUNT', 750, '/rewards/discount-25.png'),
('50% Course Discount', 'Get 50% off your next course purchase', 'DISCOUNT', 1500, '/rewards/discount-50.png'),
('Streak Freeze Token', 'Protect your login streak for one day', 'ACCESS', 200, '/rewards/streak-freeze.png'),
('Premium Badge Slot', 'Display one additional badge on your profile', 'ACCESS', 800, '/rewards/badge-slot.png'),
('Profile Theme', 'Unlock a premium profile theme', 'ACCESS', 600, '/rewards/profile-theme.png'),
('Early Access Pass', 'Get early access to new courses', 'ACCESS', 1200, '/rewards/early-access.png');
