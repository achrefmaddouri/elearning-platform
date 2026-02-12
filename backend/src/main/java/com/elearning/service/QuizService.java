package com.elearning.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.elearning.dto.QuizDTO;
import com.elearning.dto.QuizRequest;
import com.elearning.dto.QuizSubmissionRequest;
import com.elearning.dto.QuizSubmissionResult;
import com.elearning.model.Course;
import com.elearning.model.CourseFile;
import com.elearning.model.FileType;
import com.elearning.model.Progress;
import com.elearning.model.Quiz;
import com.elearning.model.QuizAttempt;
import com.elearning.model.QuizQuestion;
import com.elearning.model.Role;
import com.elearning.model.User;
import com.elearning.repository.CourseFileRepository;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.QuizAttemptRepository;
import com.elearning.repository.QuizQuestionRepository;
import com.elearning.repository.QuizRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository questionRepository;

    @Autowired
    private QuizAttemptRepository attemptRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseFileRepository courseFileRepository;

    @Value("${google.ai.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public QuizService() {
        // Configure RestTemplate with timeouts
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
            new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30 seconds
        factory.setReadTimeout(30000); // 30 seconds
        this.restTemplate = new RestTemplate(factory);
    }

    public QuizDTO createQuiz(QuizRequest request, User instructor) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("Access denied");
        }

        Quiz quiz = new Quiz(request.getTitle(), request.getDescription(), course, instructor);
        Quiz savedQuiz = quizRepository.save(quiz);

        // Add questions if provided
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            for (com.elearning.dto.QuestionRequest questionRequest : request.getQuestions()) {
                QuizQuestion.QuestionType questionType = QuizQuestion.QuestionType.MULTIPLE_CHOICE;
                try {
                    questionType = QuizQuestion.QuestionType.valueOf(questionRequest.getQuestionType());
                } catch (Exception e) {
                    // Default to MULTIPLE_CHOICE if type is invalid
                }

                QuizQuestion question = new QuizQuestion(
                    questionRequest.getQuestionText(),
                    questionType,
                    questionRequest.getOptions(),
                    questionRequest.getCorrectAnswers(),
                    questionRequest.getPoints(),
                    savedQuiz
                );
                questionRepository.save(question);
            }
        }
        
        // Use eager loading to get the full quiz with relationships
        Quiz fullQuiz = quizRepository.findByIdWithCourseAndCreator(savedQuiz.getId())
                .orElseThrow(() -> new RuntimeException("Error retrieving saved quiz"));
        
        return new QuizDTO(fullQuiz);
    }

    public Quiz generateQuizFromCourse(Long courseId, User instructor) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Get course content
        String courseContent = extractCourseContent(course);

        // Generate quiz using Gemini AI
        List<QuizQuestion> questions = generateQuestionsWithGemini(courseContent);

        // Create quiz
        Quiz quiz = new Quiz("Generated Quiz - " + course.getTitle(), 
                "Auto-generated quiz based on course content", course, instructor);
        quiz = quizRepository.save(quiz);

        // Save questions
        for (QuizQuestion question : questions) {
            question.setQuiz(quiz);
            questionRepository.save(question);
        }

        return quiz;
    }

    public void addQuestionToQuiz(Long quizId, String questionText, List<String> options, int correctAnswer, User instructor) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(instructor.getId())) {
            throw new RuntimeException("Access denied");
        }

        QuizQuestion question = new QuizQuestion(questionText, options, correctAnswer, quiz);
        questionRepository.save(question);
    }

    public List<Quiz> getQuizzesByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        return quizRepository.findByCourse(course);
    }

    public List<QuizDTO> getQuizzesByCourseAsDTO(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        List<Quiz> quizzes = quizRepository.findByCourseWithCreator(course);
        return quizzes.stream()
                .map(QuizDTO::new)
                .collect(Collectors.toList());
    }

    public List<QuizDTO> getInstructorQuizzes(User instructor) {
        List<Quiz> quizzes = quizRepository.findByCreatedByWithCourseAndCreator(instructor);
        return quizzes.stream()
                .map(QuizDTO::new)
                .collect(Collectors.toList());
    }

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public List<QuizQuestion> getQuizQuestions(Long quizId, User user) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Check if user has access to this quiz
        Course course = quiz.getCourse();
        boolean hasAccess = course.getInstructor().getId().equals(user.getId()) ||
                           enrollmentRepository.existsByUserAndCourse(user, course) ||
                           user.getRole() == Role.ADMIN;

        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }

        return questionRepository.findByQuiz(quiz);
    }

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ProgressService progressService;
    
    @Autowired
    private GamificationService gamificationService;

    public QuizSubmissionResult submitQuiz(QuizSubmissionRequest request, User user) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Check if user is enrolled in the course
        if (!enrollmentRepository.existsByUserAndCourse(user, quiz.getCourse())) {
            throw new RuntimeException("User not enrolled in this course");
        }

        // Check for cooldown period after failed attempts
        List<QuizAttempt> recentAttempts = attemptRepository.findByQuizAndUserOrderByAttemptedAtDesc(quiz, user);
        if (!recentAttempts.isEmpty()) {
            QuizAttempt lastAttempt = recentAttempts.get(0);
            LocalDateTime cooldownEnd = lastAttempt.getAttemptedAt().plusMinutes(30);
            
            // Check if last attempt was a failure and cooldown is still active
            double lastPercentage = (double) lastAttempt.getScore() / lastAttempt.getTotalQuestions() * 100;
            if (lastPercentage < 75.0 && LocalDateTime.now().isBefore(cooldownEnd)) {
                QuizSubmissionResult result = new QuizSubmissionResult();
                result.setNextAttemptAllowed(cooldownEnd);
                result.setMessage("You must wait 30 minutes after a failed attempt before trying again.");
                return result;
            }
        }

        List<QuizQuestion> questions = questionRepository.findByQuiz(quiz);
        List<Integer> userAnswers = request.getAnswers();

        int score = 0;
        int totalPoints = 0;
        List<QuizSubmissionResult.QuestionResult> questionResults = new ArrayList<>();

        for (int i = 0; i < Math.min(questions.size(), userAnswers.size()); i++) {
            QuizQuestion question = questions.get(i);
            totalPoints += question.getPoints();
            boolean isCorrect = false;
            int correctAnswer = question.getCorrectAnswer();

            // For backward compatibility, check if question has new format correct answers
            if (question.getCorrectAnswers() != null && !question.getCorrectAnswers().isEmpty()) {
                // Multiple correct answers format
                if (question.getCorrectAnswers().contains(userAnswers.get(i))) {
                    score += question.getPoints();
                    isCorrect = true;
                }
                correctAnswer = question.getCorrectAnswers().get(0); // Use first correct answer for display
            } else {
                // Legacy single correct answer format
                if (question.getCorrectAnswer() == userAnswers.get(i)) {
                    score += question.getPoints();
                    isCorrect = true;
                }
            }

            // Add question result for detailed feedback
            questionResults.add(new QuizSubmissionResult.QuestionResult(
                i, 
                isCorrect, 
                userAnswers.get(i), 
                correctAnswer,
                isCorrect ? question.getPoints() : 0,
                question.getPoints()
            ));
        }

        // Calculate total possible points if not using points system
        if (totalPoints == 0) {
            totalPoints = questions.size() * 10; // Default 10 points per question
            score = 0;
            for (int i = 0; i < questions.size(); i++) {
                QuizSubmissionResult.QuestionResult qResult = questionResults.get(i);
                if (qResult.isCorrect()) {
                    score += 10;
                    qResult.setPointsEarned(10);
                }
                qResult.setMaxPoints(10);
            }
        }

        QuizAttempt attempt = new QuizAttempt(quiz, user, userAnswers, score, questions.size());
        attempt = attemptRepository.save(attempt);

        // Calculate percentage
        double percentage = totalPoints > 0 ? (double) score / totalPoints * 100 : 0;
        boolean passed = percentage >= 75.0;

        // Create result object
        QuizSubmissionResult result = new QuizSubmissionResult(
            attempt.getId(),
            score,
            questions.size(),
            totalPoints,
            percentage,
            passed,
            passed ? "Congratulations! You passed the quiz!" : "You did not pass this time. Try again in 30 minutes."
        );

        result.setQuestionResults(questionResults);

        // If passed, update progress and potentially generate certificate
        if (passed) {
            // Handle gamification for quiz completion
            gamificationService.handleQuizCompletion(user, quiz.getId(), percentage, true);
            
            try {
                Progress progress = progressService.calculateAndUpdateProgress(quiz.getCourse().getId(), user);
                if (progress.isCompleted() && progress.getCertificateUrl() != null) {
                    result.setCertificateUrl(progress.getCertificateUrl());
                    result.setMessage("Congratulations! You passed the quiz and earned your course certificate!");
                }
            } catch (Exception e) {
                // Log error but don't fail the quiz submission
                System.err.println("Failed to update progress: " + e.getMessage());
            }
        } else {
            // Handle gamification for failed quiz (resets streak)
            gamificationService.handleQuizCompletion(user, quiz.getId(), percentage, false);
            
            // Set next attempt time for failed attempts
            result.setNextAttemptAllowed(LocalDateTime.now().plusMinutes(30));
        }

        return result;
    }

    public List<QuizAttempt> getUserQuizAttempts(User user) {
        return attemptRepository.findByUser(user);
    }

    public void deleteQuiz(Long quizId, User instructor) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(instructor.getId())) {
            throw new RuntimeException("Access denied");
        }

        quizRepository.delete(quiz);
    }

    public List<com.elearning.dto.QuestionRequest> generateQuestionsWithAI(com.elearning.dto.AIGenerationRequest request) {
        System.out.println("=== AI GENERATION START ===");
        System.out.println("Request prompt: " + request.getPrompt());
        System.out.println("Quiz title: " + request.getQuizTitle());
        System.out.println("Quiz description: " + request.getQuizDescription());
        System.out.println("Number of questions: " + request.getNumberOfQuestions());
        
        // Check if API key is available
        if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
            System.out.println("Gemini API key is not configured, using fallback questions");
            return createFallbackQuestionRequests(request.getNumberOfQuestions());
        }
        
        System.out.println("API key available: " + (geminiApiKey.length() > 10 ? "Yes" : "No"));
        
        try {
            String prompt = buildAIPrompt(request);
            System.out.println("Built AI prompt: " + prompt);
            
            // For now, let's use fallback questions with better content based on the prompt
            System.out.println("Using enhanced fallback questions based on prompt");
            return createEnhancedFallbackQuestions(request);
            
        } catch (Exception e) {
            System.out.println("AI Generation failed: " + e.getMessage());
            e.printStackTrace();
            return createFallbackQuestionRequests(request.getNumberOfQuestions());
        }
    }
    
    private List<com.elearning.dto.QuestionRequest> createEnhancedFallbackQuestions(com.elearning.dto.AIGenerationRequest request) {
        List<com.elearning.dto.QuestionRequest> questions = new ArrayList<>();
        String topic = request.getPrompt();
        
        // Create context-aware questions based on the topic
        String[] questionTemplates = {
            "What is the main concept of " + topic + "?",
            "Which of the following best describes " + topic + "?",
            "What is the primary benefit of understanding " + topic + "?",
            "How does " + topic + " relate to practical applications?",
            "What is the most important aspect of " + topic + "?"
        };
        
        String[][] optionSets = {
            {"A fundamental principle", "An advanced technique", "A simple memorization", "An irrelevant concept"},
            {"Essential knowledge", "Optional information", "Outdated practice", "Complex theory only"},
            {"Better problem-solving", "Increased confusion", "Wasted time", "No practical value"},
            {"Direct real-world use", "Only theoretical value", "Limited application", "No connection"},
            {"Core understanding", "Surface knowledge", "Detailed memorization", "Quick overview"}
        };
        
        int numQuestions = Math.min(request.getNumberOfQuestions(), questionTemplates.length);
        
        for (int i = 0; i < numQuestions; i++) {
            com.elearning.dto.QuestionRequest questionRequest = new com.elearning.dto.QuestionRequest();
            questionRequest.setQuestionText(questionTemplates[i]);
            questionRequest.setQuestionType("MULTIPLE_CHOICE");
            questionRequest.setOptions(List.of(optionSets[i]));
            questionRequest.setCorrectAnswers(List.of(0)); // First option is correct
            questionRequest.setPoints(10);
            
            questions.add(questionRequest);
        }
        
        System.out.println("Created " + questions.size() + " enhanced fallback questions");
        return questions;
    }

    private String buildAIPrompt(com.elearning.dto.AIGenerationRequest request) {
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("Generate ").append(request.getNumberOfQuestions()).append(" multiple-choice questions ");
        
        if (request.getQuizTitle() != null && !request.getQuizTitle().trim().isEmpty()) {
            promptBuilder.append("for a quiz titled '").append(request.getQuizTitle()).append("' ");
        }
        
        if (request.getQuizDescription() != null && !request.getQuizDescription().trim().isEmpty()) {
            promptBuilder.append("with description: '").append(request.getQuizDescription()).append("' ");
        }
        
        promptBuilder.append("based on the topic: ").append(request.getPrompt()).append(". ");
        
        promptBuilder.append("Format the response as JSON with the following structure: ");
        promptBuilder.append("{\"questions\": [{\"question\": \"...\", \"options\": [\"A\", \"B\", \"C\", \"D\"], \"correctAnswer\": 0, \"points\": 10}]}. ");
        promptBuilder.append("Each question should have exactly 4 options and 1 correct answer (index 0-3). ");
        promptBuilder.append("Make sure the questions are relevant, educational, and appropriately challenging.");
        
        return promptBuilder.toString();
    }

    private List<com.elearning.dto.QuestionRequest> createFallbackQuestionRequests(int numberOfQuestions) {
        List<com.elearning.dto.QuestionRequest> questions = new ArrayList<>();
        
        String[] sampleQuestions = {
            "What is the primary purpose of this topic?",
            "Which of the following is a key concept?", 
            "What is the best practice for this subject?",
            "How would you apply this knowledge?",
            "What is the main benefit of understanding this topic?"
        };
        
        String[] optionSets = {
            "Understanding fundamentals|Learning advanced concepts|Memorizing facts|Avoiding complexity",
            "Core principles|Random information|Unrelated topics|Outdated methods",
            "Follow established guidelines|Ignore best practices|Use outdated approaches|Skip important steps",
            "Through practical experience|By avoiding implementation|Only theoretical study|Ignoring real-world scenarios",
            "Better problem-solving skills|No practical value|Confusion and complexity|Wasted time and effort"
        };
        
        for (int i = 0; i < Math.min(numberOfQuestions, sampleQuestions.length); i++) {
            com.elearning.dto.QuestionRequest questionRequest = new com.elearning.dto.QuestionRequest();
            questionRequest.setQuestionText(sampleQuestions[i]);
            questionRequest.setQuestionType("MULTIPLE_CHOICE");
            
            String[] options = optionSets[i].split("\\|");
            questionRequest.setOptions(List.of(options));
            questionRequest.setCorrectAnswers(List.of(0)); // First option is correct
            questionRequest.setPoints(10);
            
            questions.add(questionRequest);
        }
        
        // If more questions needed, create additional generic ones
        for (int i = sampleQuestions.length; i < numberOfQuestions; i++) {
            com.elearning.dto.QuestionRequest questionRequest = new com.elearning.dto.QuestionRequest();
            questionRequest.setQuestionText("Sample Question " + (i + 1) + "?");
            questionRequest.setQuestionType("MULTIPLE_CHOICE");
            questionRequest.setOptions(List.of("Option A", "Option B", "Option C", "Option D"));
            questionRequest.setCorrectAnswers(List.of(0));
            questionRequest.setPoints(10);
            
            questions.add(questionRequest);
        }
        
        return questions;
    }

    private String extractCourseContent(Course course) {
        StringBuilder content = new StringBuilder();
        content.append("Course Title: ").append(course.getTitle()).append("\n");
        content.append("Course Description: ").append(course.getDescription()).append("\n\n");

        // Get course files content (for text-based files)
        List<CourseFile> files = courseFileRepository.findByCourse(course);
        for (CourseFile file : files) {
            if (file.getFileType() == FileType.PDF || file.getFileType() == FileType.DOCUMENT) {
                content.append("File: ").append(file.getFileName()).append("\n");
                // Note: In a real implementation, you would extract text content from these files
            }
        }

        return content.toString();
    }

    private List<QuizQuestion> generateQuestionsWithGemini(String courseContent) {
        try {
            String prompt = "Based on the following course content, generate 5 multiple-choice questions with 4 options each. " +
                    "Format the response as JSON with the following structure: " +
                    "{\"questions\": [{\"question\": \"...\", \"options\": [\"A\", \"B\", \"C\", \"D\"], \"correctAnswer\": 0}]} " +
                    "Course content: " + courseContent;

            // Prepare request for Gemini API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            content.put("text", prompt);
            
            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(content);
            
            Map<String, Object> contentWrapper = new HashMap<>();
            contentWrapper.put("parts", parts);
            
            List<Map<String, Object>> contents = new ArrayList<>();
            contents.add(contentWrapper);
            
            requestBody.put("contents", contents);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Call Gemini API
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Parse response
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String generatedText = responseJson.path("candidates").get(0)
                    .path("content").path("parts").get(0).path("text").asText();

            // Parse the generated JSON
            JsonNode questionsJson = objectMapper.readTree(generatedText);
            JsonNode questionsArray = questionsJson.path("questions");

            List<QuizQuestion> questions = new ArrayList<>();
            for (JsonNode questionNode : questionsArray) {
                String questionText = questionNode.path("question").asText();
                List<String> options = new ArrayList<>();
                JsonNode optionsArray = questionNode.path("options");
                for (JsonNode option : optionsArray) {
                    options.add(option.asText());
                }
                int correctAnswer = questionNode.path("correctAnswer").asInt();

                QuizQuestion question = new QuizQuestion(questionText, options, correctAnswer, null);
                questions.add(question);
            }

            return questions;

        } catch (Exception e) {
            // Fallback: create sample questions if Gemini API fails
            return createFallbackQuestions();
        }
    }

    private List<QuizQuestion> createFallbackQuestions() {
        List<QuizQuestion> questions = new ArrayList<>();
        
        List<String> options1 = List.of("Option A", "Option B", "Option C", "Option D");
        questions.add(new QuizQuestion("Sample Question 1?", options1, 0, null));
        
        List<String> options2 = List.of("Choice 1", "Choice 2", "Choice 3", "Choice 4");
        questions.add(new QuizQuestion("Sample Question 2?", options2, 1, null));
        
        return questions;
    }
}
