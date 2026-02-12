#!/usr/bin/env python3
"""
E-Learning Platform AI Dataset Generator
Generates synthetic data for training a course recommendation system
"""

import pandas as pd
import numpy as np
import random
from datetime import datetime, timedelta
from faker import Faker
import json

# Initialize Faker
fake = Faker()

# Configuration
NUM_USERS = 2000
NUM_INSTRUCTORS = 150
NUM_COURSES = 800
NUM_CATEGORIES = 10
NUM_ENROLLMENTS = 12000
NUM_QUIZ_ATTEMPTS = 25000
NUM_CHAT_MESSAGES = 15000

# Set random seeds for reproducibility
random.seed(42)
np.random.seed(42)
Faker.seed(42)

def generate_course_categories():
    """Generate course categories"""
    categories = [
        {'id': 1, 'name': 'Programming', 'description': 'Programming and software development courses'},
        {'id': 2, 'name': 'Design', 'description': 'Graphic design, UI/UX, and creative courses'},
        {'id': 3, 'name': 'Business', 'description': 'Business, marketing, and entrepreneurship'},
        {'id': 4, 'name': 'Languages', 'description': 'Foreign language learning courses'},
        {'id': 5, 'name': 'Science', 'description': 'Science, mathematics, and engineering'},
        {'id': 6, 'name': 'Arts', 'description': 'Music, art, and creative expression'},
        {'id': 7, 'name': 'Health', 'description': 'Health, fitness, and wellness courses'},
        {'id': 8, 'name': 'Technology', 'description': 'Technology, AI, and digital skills'},
        {'id': 9, 'name': 'Personal Development', 'description': 'Self-improvement and life skills'},
        {'id': 10, 'name': 'Academic', 'description': 'Academic subjects and test preparation'},
    ]
    return pd.DataFrame(categories)

def generate_users():
    """Generate users data"""
    users = []
    roles = ['USER', 'INSTRUCTOR', 'ADMIN']
    role_weights = [0.85, 0.14, 0.01]  # 85% users, 14% instructors, 1% admins
    
    for i in range(1, NUM_USERS + NUM_INSTRUCTORS + 1):
        role = np.random.choice(roles, p=role_weights)
        created_date = fake.date_time_between(start_date='-2y', end_date='now')
        
        user = {
            'id': i,
            'first_name': fake.first_name(),
            'last_name': fake.last_name(),
            'email': fake.unique.email(),
            'role': role,
            'status': np.random.choice(['ACTIVE', 'PENDING', 'BANNED'], p=[0.92, 0.07, 0.01]),
            'email_verified': np.random.choice([True, False], p=[0.85, 0.15]),
            'created_at': created_date,
            'updated_at': created_date + timedelta(days=random.randint(0, 30))
        }
        users.append(user)
    
    return pd.DataFrame(users)

def generate_courses(users_df, categories_df):
    """Generate courses data"""
    courses = []
    instructors = users_df[users_df['role'] == 'INSTRUCTOR']['id'].tolist()
    
    # Course title templates by category
    course_templates = {
        'Programming': [
            'Introduction to {}', 'Advanced {}', '{} for Beginners', 'Master {}', 
            '{} Bootcamp', 'Complete {} Guide', '{} Fundamentals', 'Professional {}'
        ],
        'Design': [
            '{} Design Principles', 'Creative {} Workshop', '{} Masterclass', 
            'Modern {} Techniques', '{} for Professionals', 'Digital {} Course'
        ],
        'Business': [
            '{} Strategy', 'Introduction to {}', '{} Management', 'Advanced {}',
            '{} for Entrepreneurs', 'Professional {} Skills'
        ],
        'Languages': [
            'Learn {} - Beginner', '{} Conversation', 'Advanced {}', 
            'Business {}', '{} Grammar Mastery', '{} for Travelers'
        ],
        'Science': [
            'Introduction to {}', 'Advanced {}', '{} Fundamentals', 
            'Applied {}', '{} Laboratory', 'Theoretical {}'
        ],
        'Arts': [
            '{} Basics', 'Creative {} Workshop', '{} Masterclass',
            'Modern {} Techniques', 'Classical {}', '{} History'
        ],
        'Health': [
            '{} Essentials', 'Complete {} Guide', '{} for Beginners',
            'Advanced {}', 'Professional {}', '{} Certification'
        ],
        'Technology': [
            'Introduction to {}', '{} Fundamentals', 'Advanced {}',
            '{} Implementation', 'Professional {}', '{} Best Practices'
        ],
        'Personal Development': [
            '{} Mastery', 'Effective {}', '{} Skills', 
            'Advanced {}', 'Professional {}', '{} Workshop'
        ],
        'Academic': [
            '{} Fundamentals', 'Advanced {}', '{} Preparation',
            'Complete {} Course', '{} Mastery', 'Professional {}'
        ]
    }
    
    # Subject matter by category
    subjects = {
        'Programming': ['Python', 'JavaScript', 'Java', 'C++', 'React', 'Node.js', 'SQL', 'Machine Learning'],
        'Design': ['UI/UX', 'Graphic Design', 'Web Design', 'Logo Design', 'Branding', 'Typography'],
        'Business': ['Marketing', 'Finance', 'Leadership', 'Project Management', 'Sales', 'Analytics'],
        'Languages': ['Spanish', 'French', 'German', 'Chinese', 'Japanese', 'Italian'],
        'Science': ['Physics', 'Chemistry', 'Biology', 'Mathematics', 'Statistics', 'Data Science'],
        'Arts': ['Photography', 'Music Theory', 'Drawing', 'Painting', 'Creative Writing', 'Film'],
        'Health': ['Nutrition', 'Fitness', 'Yoga', 'Mental Health', 'First Aid', 'Wellness'],
        'Technology': ['Cloud Computing', 'Cybersecurity', 'AI', 'Blockchain', 'IoT', 'DevOps'],
        'Personal Development': ['Time Management', 'Communication', 'Productivity', 'Goal Setting', 'Mindfulness', 'Career'],
        'Academic': ['Test Prep', 'Study Skills', 'Research Methods', 'Writing', 'Critical Thinking', 'Logic']
    }
    
    for i in range(1, NUM_COURSES + 1):
        category = categories_df.sample(1).iloc[0]
        category_name = category['name']
        
        # Select template and subject
        template = random.choice(course_templates[category_name])
        subject = random.choice(subjects[category_name])
        title = template.format(subject)
        
        # Generate description
        descriptions = [
            f"Comprehensive {subject.lower()} course covering fundamental concepts and practical applications.",
            f"Learn {subject.lower()} from scratch with hands-on projects and real-world examples.",
            f"Master {subject.lower()} with this complete guide designed for all skill levels.",
            f"Professional {subject.lower()} training with industry best practices and expert insights.",
            f"In-depth {subject.lower()} course with practical exercises and certification."
        ]
        
        created_date = fake.date_time_between(start_date='-18mo', end_date='now')
        
        course = {
            'id': i,
            'title': title,
            'description': random.choice(descriptions),
            'instructor_id': random.choice(instructors),
            'category_id': category['id'],
            'status': np.random.choice(['ACCEPTED', 'PENDING', 'DECLINED'], p=[0.85, 0.1, 0.05]),
            'visibility': np.random.choice(['PUBLIC', 'PRIVATE'], p=[0.9, 0.1]),
            'price': round(random.uniform(0, 299.99), 2) if random.random() > 0.4 else 0.0,
            'is_free': random.random() > 0.4,
            'thumbnail_url': f'/thumbnails/course_{i}.jpg',
            'created_at': created_date,
            'updated_at': created_date + timedelta(days=random.randint(0, 60))
        }
        courses.append(course)
    
    return pd.DataFrame(courses)

def generate_enrollments(users_df, courses_df):
    """Generate enrollments data"""
    enrollments = []
    students = users_df[users_df['role'] == 'USER']['id'].tolist()
    available_courses = courses_df[courses_df['status'] == 'ACCEPTED']['id'].tolist()
    
    for i in range(1, NUM_ENROLLMENTS + 1):
        user_id = random.choice(students)
        course_id = random.choice(available_courses)
        
        # Avoid duplicate enrollments (simplified approach)
        if (user_id, course_id) in [(e['user_id'], e['course_id']) for e in enrollments]:
            continue
            
        enrollment_date = fake.date_time_between(start_date='-12mo', end_date='now')
        
        enrollment = {
            'id': i,
            'user_id': user_id,
            'course_id': course_id,
            'enrolled_at': enrollment_date,
            'is_paid': random.random() > 0.6  # 40% paid courses
        }
        enrollments.append(enrollment)
    
    return pd.DataFrame(enrollments)

def generate_progress(enrollments_df):
    """Generate progress data based on enrollments"""
    progress_data = []
    
    for _, enrollment in enrollments_df.iterrows():
        # Simulate realistic progress patterns
        if random.random() < 0.15:  # 15% haven't started
            progress_percentage = 0
            is_completed = False
            completed_at = None
        elif random.random() < 0.25:  # 25% dropped out early
            progress_percentage = random.randint(5, 30)
            is_completed = False
            completed_at = None
        elif random.random() < 0.35:  # 35% in progress
            progress_percentage = random.randint(31, 99)
            is_completed = False
            completed_at = None
        else:  # 40% completed
            progress_percentage = 100
            is_completed = True
            completed_at = enrollment['enrolled_at'] + timedelta(days=random.randint(7, 90))
        
        last_accessed = enrollment['enrolled_at'] + timedelta(days=random.randint(0, 30))
        
        progress = {
            'id': enrollment['id'],  # Using same ID as enrollment for simplicity
            'user_id': enrollment['user_id'],
            'course_id': enrollment['course_id'],
            'progress_percentage': progress_percentage,
            'is_completed': is_completed,
            'certificate_url': f'/certificates/user_{enrollment["user_id"]}_course_{enrollment["course_id"]}.pdf' if is_completed else None,
            'last_accessed': last_accessed,
            'completed_at': completed_at
        }
        progress_data.append(progress)
    
    return pd.DataFrame(progress_data)

def generate_quizzes(courses_df):
    """Generate quizzes for courses"""
    quizzes = []
    
    # Each course has 1-5 quizzes
    for _, course in courses_df.iterrows():
        if course['status'] != 'ACCEPTED':
            continue
            
        num_quizzes = random.randint(1, 5)
        for quiz_num in range(1, num_quizzes + 1):
            quiz = {
                'id': len(quizzes) + 1,
                'title': f'{course["title"]} - Quiz {quiz_num}',
                'description': f'Assessment quiz for {course["title"]}',
                'course_id': course['id'],
                'created_by': course['instructor_id'],
                'created_at': course['created_at'] + timedelta(days=random.randint(1, 30))
            }
            quizzes.append(quiz)
    
    return pd.DataFrame(quizzes)

def generate_quiz_attempts(quizzes_df, enrollments_df):
    """Generate quiz attempts"""
    attempts = []
    
    for i in range(1, NUM_QUIZ_ATTEMPTS + 1):
        # Select random quiz and check if user is enrolled
        quiz = quizzes_df.sample(1).iloc[0]
        enrolled_users = enrollments_df[enrollments_df['course_id'] == quiz['course_id']]['user_id'].tolist()
        
        if not enrolled_users:
            continue
            
        user_id = random.choice(enrolled_users)
        
        # Generate realistic score distribution
        score = np.random.normal(75, 15)  # Mean 75%, std dev 15%
        score = max(0, min(100, int(score)))  # Clamp to 0-100
        
        total_questions = random.randint(5, 20)
        correct_answers = int((score / 100) * total_questions)
        
        attempted_at = quiz['created_at'] + timedelta(days=random.randint(0, 180))
        
        attempt = {
            'id': i,
            'quiz_id': quiz['id'],
            'user_id': user_id,
            'score': correct_answers,
            'total_questions': total_questions,
            'percentage': score,
            'attempted_at': attempted_at
        }
        attempts.append(attempt)
    
    return pd.DataFrame(attempts)

def generate_user_interactions(users_df, courses_df, enrollments_df):
    """Generate user interaction data for ML features"""
    interactions = []
    
    # Generate implicit feedback data
    for _, enrollment in enrollments_df.iterrows():
        user_id = enrollment['user_id']
        course_id = enrollment['course_id']
        
        # Time spent on course (minutes)
        time_spent = np.random.exponential(120)  # Average 2 hours, exponential distribution
        
        # Number of sessions
        num_sessions = max(1, int(np.random.poisson(8)))  # Average 8 sessions
        
        # Rating (implicit based on completion and time spent)
        progress = random.randint(0, 100)  # Will be replaced with actual progress
        if progress == 100:
            rating = np.random.choice([4, 5], p=[0.3, 0.7])
        elif progress > 70:
            rating = np.random.choice([3, 4, 5], p=[0.2, 0.5, 0.3])
        elif progress > 30:
            rating = np.random.choice([2, 3, 4], p=[0.3, 0.5, 0.2])
        else:
            rating = np.random.choice([1, 2, 3], p=[0.5, 0.3, 0.2])
        
        interaction = {
            'user_id': user_id,
            'course_id': course_id,
            'time_spent_minutes': int(time_spent),
            'num_sessions': num_sessions,
            'implicit_rating': rating,
            'last_interaction': enrollment['enrolled_at'] + timedelta(days=random.randint(0, 60))
        }
        interactions.append(interaction)
    
    return pd.DataFrame(interactions)

def generate_gamification_data(users_df, progress_df):
    """Generate gamification data"""
    gamification_data = []
    students = users_df[users_df['role'] == 'USER']
    
    for _, user in students.iterrows():
        user_progress = progress_df[progress_df['user_id'] == user['id']]
        
        # Calculate points based on progress
        completed_courses = len(user_progress[user_progress['is_completed'] == True])
        total_progress = user_progress['progress_percentage'].sum()
        
        total_points = completed_courses * 100 + int(total_progress * 0.5)
        
        # Generate streaks
        current_login_streak = max(0, int(np.random.poisson(7)))
        longest_login_streak = max(current_login_streak, int(np.random.poisson(15)))
        
        gamification = {
            'user_id': user['id'],
            'total_points': total_points,
            'current_login_streak': current_login_streak,
            'longest_login_streak': longest_login_streak,
            'current_course_streak': random.randint(0, 5),
            'current_quiz_streak': random.randint(0, 10),
            'streak_freeze_tokens': random.randint(0, 3)
        }
        gamification_data.append(gamification)
    
    return pd.DataFrame(gamification_data)

def save_datasets():
    """Generate and save all datasets"""
    print("Generating datasets...")
    
    # Generate base data
    categories_df = generate_course_categories()
    users_df = generate_users()
    courses_df = generate_courses(users_df, categories_df)
    enrollments_df = generate_enrollments(users_df, courses_df)
    progress_df = generate_progress(enrollments_df)
    quizzes_df = generate_quizzes(courses_df)
    quiz_attempts_df = generate_quiz_attempts(quizzes_df, enrollments_df)
    interactions_df = generate_user_interactions(users_df, courses_df, enrollments_df)
    gamification_df = generate_gamification_data(users_df, progress_df)
    
    # Create combined dataset for ML
    ml_dataset = enrollments_df.merge(users_df, left_on='user_id', right_on='id', suffixes=('', '_user'))
    ml_dataset = ml_dataset.merge(courses_df, left_on='course_id', right_on='id', suffixes=('', '_course'))
    ml_dataset = ml_dataset.merge(progress_df, on=['user_id', 'course_id'], suffixes=('', '_progress'))
    ml_dataset = ml_dataset.merge(interactions_df, on=['user_id', 'course_id'], how='left')
    ml_dataset = ml_dataset.merge(categories_df, left_on='category_id', right_on='id', suffixes=('', '_category'))
    
    # Save individual datasets
    categories_df.to_csv('ai-model/course_categories.csv', index=False)
    users_df.to_csv('ai-model/users.csv', index=False)
    courses_df.to_csv('ai-model/courses.csv', index=False)
    enrollments_df.to_csv('ai-model/enrollments.csv', index=False)
    progress_df.to_csv('ai-model/progress.csv', index=False)
    quizzes_df.to_csv('ai-model/quizzes.csv', index=False)
    quiz_attempts_df.to_csv('ai-model/quiz_attempts.csv', index=False)
    interactions_df.to_csv('ai-model/user_interactions.csv', index=False)
    gamification_df.to_csv('ai-model/user_gamification.csv', index=False)
    
    # Save combined ML dataset
    ml_dataset.to_csv('ai-model/ml_dataset.csv', index=False)
    
    print("Datasets generated successfully!")
    print(f"Users: {len(users_df)}")
    print(f"Courses: {len(courses_df)}")
    print(f"Enrollments: {len(enrollments_df)}")
    print(f"Progress records: {len(progress_df)}")
    print(f"Quiz attempts: {len(quiz_attempts_df)}")
    print(f"ML dataset rows: {len(ml_dataset)}")
    
    # Generate data summary
    summary = {
        'generation_date': datetime.now().isoformat(),
        'datasets': {
            'users': len(users_df),
            'courses': len(courses_df),
            'enrollments': len(enrollments_df),
            'progress': len(progress_df),
            'quiz_attempts': len(quiz_attempts_df),
            'ml_dataset': len(ml_dataset)
        },
        'data_ranges': {
            'date_range': '2023-01-01 to 2024-12-31',
            'user_roles': users_df['role'].value_counts().to_dict(),
            'course_categories': courses_df['category_id'].value_counts().to_dict(),
            'completion_rate': f"{(progress_df['is_completed'].sum() / len(progress_df) * 100):.1f}%"
        }
    }
    
    with open('ai-model/dataset_summary.json', 'w') as f:
        json.dump(summary, f, indent=2)

if __name__ == "__main__":
    save_datasets()