# Contributing to EduSmart

We welcome contributions from the community! This guide will help you get started.

---

## Code of Conduct

Be respectful, inclusive, and professional in all interactions. We're committed to providing a welcoming environment for all contributors.

---

## Getting Started

### Prerequisites

- Git installed
- Java 17+ (for backend)
- Node.js 16+ (for frontend)
- Python 3.9+ (for AI models)
- MySQL 8.0+
- Docker (recommended)

### Fork & Clone

```bash
# Fork the repository on GitHub
# Clone your fork
git clone https://github.com/YOUR_USERNAME/edusmart.git
cd edusmart

# Add upstream remote
git remote add upstream https://github.com/original-owner/edusmart.git
```

### Setup Development Environment

```bash
# Backend
cd backend
mvn clean install

# Frontend
cd frontend
npm install

# AI Model
cd ai-model
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

---

## Development Workflow

### 1. Create a Feature Branch

Use the naming convention: `{type}/{short-description}`

```bash
# From main branch
git checkout main
git pull upstream main

# Create feature branch
git checkout -b feature/add-recommendation-filtering
```

### 2. Commit with Conventional Commits

Follow the conventional commit format:

```
{type}({scope}): {short-description}

{detailed-description}

Fixes #{issue-number}
```

### Types

- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation only changes
- **style**: Changes that don't affect code meaning (formatting, whitespace)
- **refactor**: Code change that neither fixes a bug nor adds a feature
- **perf**: Performance improvement
- **test**: Adding or updating tests
- **chore**: Build process, dependencies, tooling

### Examples

```bash
# Add new recommendation algorithm
git commit -m "feat(ai): implement content-based filtering algorithm

- Add content similarity matrix calculation
- Integrate with recommendation service
- Add unit tests for algorithm

Fixes #42"

# Fix course enrollment bug
git commit -m "fix(course): resolve duplicate enrollment issue

Prevent multiple enrollments for same user-course pair

Fixes #85"

# Update documentation
git commit -m "docs(setup): update installation instructions"
```

---

## Code Standards

### Backend (Java)

**Naming Conventions:**
- Classes: PascalCase (e.g., `CourseService`)
- Methods: camelCase (e.g., `getCourseById`)
- Constants: UPPER_SNAKE_CASE (e.g., `MAX_COURSES`)
- Packages: reverse domain notation (e.g., `com.elearning.service`)

**Code Style:**
```java
@Slf4j
@Service
public class CourseService {
    // Proper spacing and formatting
    private final CourseRepository courseRepository;
    
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    
    public Optional<Course> getCourseById(Long id) {
        log.debug("Fetching course with ID: {}", id);
        return courseRepository.findById(id);
    }
}
```

**Best Practices:**
- Use dependency injection
- Avoid null pointers (use Optional)
- Add proper logging
- Write unit tests for all services
- Use meaningful variable names

### Frontend (TypeScript/React)

**Naming Conventions:**
- Components: PascalCase (e.g., `CourseCard`)
- Hooks: camelCase with `use` prefix (e.g., `useCourses`)
- Files: PascalCase for components, camelCase for utilities
- Constants: UPPER_SNAKE_CASE

**Code Style:**
```typescript
interface Course {
  id: number;
  title: string;
  description: string;
}

const CourseCard: React.FC<{ course: Course }> = ({ course }) => {
  return (
    <div className="course-card">
      <h2>{course.title}</h2>
      <p>{course.description}</p>
    </div>
  );
};
```

**Best Practices:**
- Use functional components
- Use TypeScript for type safety
- Add proper error handling
- Keep components small and focused
- Use meaningful component names

### Python (AI Models)

**Naming Conventions:**
- Functions: snake_case (e.g., `calculate_similarity`)
- Classes: PascalCase (e.g., `CollaborativeFilter`)
- Constants: UPPER_SNAKE_CASE
- Modules: lowercase

**Code Style:**
```python
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity

class CollaborativeFilter:
    """Collaborative filtering recommendation engine."""
    
    def __init__(self, min_common_items: int = 3):
        """
        Initialize the collaborative filter.
        
        Args:
            min_common_items: Minimum common rated items between users
        """
        self.min_common_items = min_common_items
    
    def calculate_similarity(self, user_history1: list, user_history2: list) -> float:
        """Calculate similarity between two users."""
        common = set(user_history1) & set(user_history2)
        if len(common) < self.min_common_items:
            return 0.0
        return cosine_similarity([user_history1], [user_history2])[0][0]
```

**Best Practices:**
- Add docstrings to functions
- Use type hints
- Handle edge cases
- Add proper error handling
- Write tests with pytest

---

## Testing

### Backend Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CourseServiceTest

# Run with coverage
mvn clean test jacoco:report
```

### Frontend Tests

```bash
# Run tests
npm test

# Run with coverage
npm test -- --coverage

# E2E tests (if available)
npm run e2e
```

### Python Tests

```bash
# Run all tests
pytest

# Run with coverage
pytest --cov=src

# Run specific test
pytest tests/test_recommender.py
```

---

## Pull Request Process

### Before Submitting

1. **Update from upstream:**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run tests locally:**
   ```bash
   # Backend
   cd backend && mvn clean test
   
   # Frontend
   cd frontend && npm test
   
   # Python
   cd ai-model && pytest
   ```

3. **Check code quality:**
   ```bash
   # For Java: Use SonarQube or similar tools
   # For TypeScript: Run linter
   cd frontend && npm run lint
   ```

4. **Update documentation** if needed

### Submit Pull Request

1. Push your branch to your fork:
   ```bash
   git push origin feature/add-recommendation-filtering
   ```

2. Create a Pull Request with:
   - Clear title describing the changes
   - Detailed description of what was changed and why
   - Link to related issues (e.g., `Fixes #42`)
   - Screenshots for UI changes
   - Test results

3. PR Template:
   ```markdown
   ## Description
   Brief description of changes

   ## Type of Change
   - [ ] Bug fix
   - [ ] New feature
   - [ ] Breaking change
   - [ ] Documentation update

   ## Related Issues
   Fixes #42

   ## Testing
   - [ ] Unit tests added
   - [ ] Integration tests passed
   - [ ] Manual testing completed

   ## Screenshots (if applicable)
   [Add screenshots here]

   ## Checklist
   - [ ] Code follows style guidelines
   - [ ] Documentation updated
   - [ ] No new warnings generated
   - [ ] Tests pass locally
   ```

---

## Review Process

- At least 2 approvals from maintainers required
- All CI/CD checks must pass
- Code coverage should not decrease
- All conversations must be resolved

---

## Development Tips

### Docker Development

```bash
# Build and run all services
docker-compose up -d

# View logs
docker-compose logs -f

# Rebuild after changes
docker-compose up -d --build
```

### Database Management

```bash
# Connect to MySQL
mysql -u root -p edusmart_db

# View migrations
show migrations;

# Reset database
mysql -u root -p < database/schema.sql
```

### Debug Mode

**Backend:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

**Frontend:**
```bash
npm start  # Automatically enables debug tools in React DevTools
```

---

## Common Issues & Solutions

### Issue: Database Connection Failed
```bash
# Verify MySQL is running
mysql -u root -p -e "SELECT 1"

# Check database configuration
cat backend/src/main/resources/application.properties
```

### Issue: Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 {PID}
```

### Issue: Node Modules Conflicts
```bash
# Clear cache and reinstall
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

---

## Documentation

- Update README.md for user-facing changes
- Update docs/ folder for architecture changes
- Use clear, concise language
- Include examples where applicable

---

## Recognition

Contributors will be recognized in:
- CONTRIBUTORS.md file
- GitHub contributors page
- Project README (for significant contributions)

---

## Questions?

- Check existing issues and discussions
- Open a new discussion for questions
- Email: contributors@edusmart.com
- Join our Discord community

---

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

**Thank you for contributing to EduSmart!**

Last Updated: February 12, 2026
