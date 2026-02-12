
# Production Deployment Script
# Save this as 'deploy_model.py' in your Spring Boot project

import pickle
import pandas as pd
import numpy as np
from tensorflow import keras

def load_models():
    """Load all trained models for production use"""
    
    models = {}
    
    # Load SVD model (recommended for production due to speed)
    with open('models/svd_model.pkl', 'rb') as f:
        models['svd'] = pickle.load(f)
    
    # Load course data
    models['courses_df'] = pd.read_csv('data/courses.csv')
    
    # Load encoders
    with open('models/encoders.pkl', 'rb') as f:
        models['encoders'] = pickle.load(f)
    
    return models

def predict_rating(models, user_id, course_id):
    """Predict rating for user-course pair"""
    return models['svd'].predict(user_id, course_id).est

def get_recommendations(models, user_id, n_recommendations=10):
    """Get course recommendations for a user"""
    
    recommendations = []
    courses_df = models['courses_df']
    
    for course_id in courses_df['course_id']:
        pred_rating = predict_rating(models, user_id, course_id)
        course_info = courses_df[courses_df['course_id'] == course_id].iloc[0]
        
        recommendations.append({
            'course_id': int(course_id),
            'predicted_rating': float(pred_rating),
            'category': str(course_info['category']),
            'difficulty': str(course_info['difficulty'])
        })
    
    # Sort by predicted rating
    recommendations.sort(key=lambda x: x['predicted_rating'], reverse=True)
    
    return recommendations[:n_recommendations]

# Example usage:
# models = load_models()
# recommendations = get_recommendations(models, user_id=1, n_recommendations=5)
