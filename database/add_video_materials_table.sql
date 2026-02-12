-- Add video_materials table to existing database
USE elearning_platform;

-- Create video_materials table if it doesn't exist
CREATE TABLE IF NOT EXISTS video_materials (
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

-- Add indexes for video_materials table
CREATE INDEX IF NOT EXISTS idx_video_materials_course ON video_materials(course_id);
CREATE INDEX IF NOT EXISTS idx_video_materials_order ON video_materials(course_id, order_index);

-- Check if table was created successfully
SHOW TABLES LIKE 'video_materials';
DESCRIBE video_materials;
