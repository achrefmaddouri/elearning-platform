-- Advanced Analytics & Insights Database Schema

-- Learning Analytics Tables
CREATE TABLE learning_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT,
    module_id BIGINT,
    session_start TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    session_end TIMESTAMP NULL,
    duration_minutes INT DEFAULT 0,
    activity_type ENUM('COURSE_VIEW', 'VIDEO_WATCH', 'QUIZ_ATTEMPT', 'MATERIAL_DOWNLOAD', 'CHAT_PARTICIPATION') NOT NULL,
    engagement_score DECIMAL(3,2) DEFAULT 0.00, -- 0.00 to 1.00
    device_type VARCHAR(50),
    browser VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE TABLE learning_patterns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    peak_hour INT, -- 0-23 hour of day
    preferred_duration INT, -- average session duration in minutes
    learning_velocity DECIMAL(5,2), -- courses per week
    consistency_score DECIMAL(3,2), -- 0.00 to 1.00
    difficulty_preference ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'MIXED'),
    last_analyzed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_pattern (user_id)
);

CREATE TABLE course_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    total_enrollments INT DEFAULT 0,
    active_learners INT DEFAULT 0,
    completion_rate DECIMAL(5,2) DEFAULT 0.00,
    average_completion_time DECIMAL(8,2) DEFAULT 0.00, -- in hours
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    difficulty_rating DECIMAL(3,2) DEFAULT 0.00, -- calculated from quiz performance
    engagement_score DECIMAL(3,2) DEFAULT 0.00,
    revenue DECIMAL(10,2) DEFAULT 0.00,
    refund_rate DECIMAL(5,2) DEFAULT 0.00,
    last_calculated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY unique_course_analytics (course_id)
);

CREATE TABLE quiz_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    total_attempts INT DEFAULT 0,
    average_score DECIMAL(5,2) DEFAULT 0.00,
    pass_rate DECIMAL(5,2) DEFAULT 0.00,
    average_duration DECIMAL(8,2) DEFAULT 0.00, -- in minutes
    difficulty_score DECIMAL(3,2) DEFAULT 0.00, -- calculated from performance
    question_effectiveness TEXT, -- JSON array of question performance
    common_mistakes TEXT, -- JSON array of common wrong answers
    improvement_suggestions TEXT,
    last_calculated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_quiz_analytics (quiz_id)
);

CREATE TABLE user_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recommendation_type ENUM('COURSE', 'LEARNING_PATH', 'QUIZ_DIFFICULTY', 'STUDY_TIME', 'SKILL_GAP') NOT NULL,
    target_id BIGINT, -- course_id, path_id, etc.
    title VARCHAR(255) NOT NULL,
    description TEXT,
    reasoning TEXT, -- AI explanation for recommendation
    confidence_score DECIMAL(3,2) DEFAULT 0.00, -- 0.00 to 1.00
    priority_score DECIMAL(3,2) DEFAULT 0.00, -- 0.00 to 1.00
    is_viewed BOOLEAN DEFAULT FALSE,
    is_acted_upon BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE learning_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    difficulty_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED'),
    estimated_duration_hours DECIMAL(6,2),
    course_sequence TEXT, -- JSON array of course IDs in order
    prerequisites TEXT, -- JSON array of required skills/courses
    learning_objectives TEXT, -- JSON array of objectives
    is_personalized BOOLEAN DEFAULT FALSE,
    created_by_ai BOOLEAN DEFAULT FALSE,
    completion_rate DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE instructor_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instructor_id BIGINT NOT NULL,
    total_courses INT DEFAULT 0,
    total_students INT DEFAULT 0,
    total_revenue DECIMAL(12,2) DEFAULT 0.00,
    average_course_rating DECIMAL(3,2) DEFAULT 0.00,
    student_completion_rate DECIMAL(5,2) DEFAULT 0.00,
    engagement_score DECIMAL(3,2) DEFAULT 0.00,
    response_rate DECIMAL(5,2) DEFAULT 0.00, -- chat/support response rate
    average_response_time DECIMAL(8,2) DEFAULT 0.00, -- in hours
    top_performing_course_id BIGINT,
    monthly_earnings DECIMAL(10,2) DEFAULT 0.00,
    last_calculated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (top_performing_course_id) REFERENCES courses(id) ON DELETE SET NULL,
    UNIQUE KEY unique_instructor_analytics (instructor_id)
);

-- Indexes for performance
CREATE INDEX idx_learning_sessions_user_time ON learning_sessions(user_id, session_start);
CREATE INDEX idx_learning_sessions_course ON learning_sessions(course_id);
CREATE INDEX idx_learning_sessions_activity ON learning_sessions(activity_type);
CREATE INDEX idx_user_recommendations_user ON user_recommendations(user_id);
CREATE INDEX idx_user_recommendations_type ON user_recommendations(recommendation_type);
CREATE INDEX idx_learning_patterns_user ON learning_patterns(user_id);
CREATE INDEX idx_course_analytics_course ON course_analytics(course_id);
CREATE INDEX idx_quiz_analytics_quiz ON quiz_analytics(quiz_id);
CREATE INDEX idx_instructor_analytics_instructor ON instructor_analytics(instructor_id);

-- Sample learning patterns data
INSERT INTO learning_patterns (user_id, peak_hour, preferred_duration, learning_velocity, consistency_score, difficulty_preference) VALUES
(1, 14, 45, 2.5, 0.85, 'INTERMEDIATE'),
(2, 9, 30, 1.8, 0.72, 'BEGINNER'),
(3, 20, 60, 3.2, 0.91, 'ADVANCED');

-- Sample recommendations
INSERT INTO user_recommendations (user_id, recommendation_type, target_id, title, description, reasoning, confidence_score, priority_score) VALUES
(1, 'COURSE', 1, 'Advanced JavaScript Concepts', 'Based on your progress in basic JavaScript, this course will help you master advanced topics.', 'User completed JavaScript Basics with 95% score and shows strong engagement with programming content.', 0.92, 0.88),
(1, 'STUDY_TIME', NULL, 'Optimize Study Schedule', 'Your peak learning hours are 2-4 PM. Consider scheduling study sessions during this time.', 'Analysis of your learning sessions shows 40% better retention during afternoon hours.', 0.87, 0.75),
(2, 'LEARNING_PATH', NULL, 'Web Development Fundamentals', 'A curated path from HTML basics to full-stack development.', 'Your learning pattern suggests you prefer structured, sequential learning with shorter sessions.', 0.81, 0.82);

-- Sample learning paths
INSERT INTO learning_paths (user_id, title, description, difficulty_level, estimated_duration_hours, course_sequence, learning_objectives, is_personalized, created_by_ai) VALUES
(1, 'Full-Stack JavaScript Developer', 'Complete path from JavaScript basics to advanced full-stack development', 'INTERMEDIATE', 120.5, '[1, 2, 3, 5, 8]', '["Master JavaScript ES6+", "Build REST APIs", "Create React applications", "Deploy to cloud"]', TRUE, TRUE),
(2, 'Beginner Web Designer', 'Design fundamentals to professional web design', 'BEGINNER', 80.0, '[6, 7, 9]', '["Learn design principles", "Master CSS layouts", "Create responsive designs"]', TRUE, FALSE);
