package com.elearning.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class AIModelConfig {
    
    private static final Logger logger = Logger.getLogger(AIModelConfig.class.getName());
    
    @Value("${ai.model.svd.path:classpath:ai-models/svd_model.pkl}")
    private String svdModelPath;
    
    @Value("${ai.model.encoders.path:classpath:ai-models/encoders.pkl}")
    private String encodersPath;
    
    @Value("${ai.model.hybrid.path:classpath:ai-models/hybrid_system.pkl}")
    private String hybridSystemPath;
    
    @Value("${ai.model.content-based.path:classpath:ai-models/content_based_recommender.pkl}")
    private String contentBasedPath;
    
    @Value("${ai.model.als.path:classpath:ai-models/als_model.pkl}")
    private String alsModelPath;
    
    private Map<String, String> modelPaths = new HashMap<>();
    private boolean modelsLoaded = false;
    
    @PostConstruct
    public void init() {
        try {
            setupModelPaths();
            validateModelFiles();
            modelsLoaded = true;
            logger.info("AI models configuration initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize AI models: " + e.getMessage());
            modelsLoaded = false;
        }
    }
    
    private void setupModelPaths() {
        modelPaths.put("svd_model", svdModelPath);
        modelPaths.put("encoders", encodersPath);
        modelPaths.put("hybrid_system", hybridSystemPath);
        modelPaths.put("content_based", contentBasedPath);
        modelPaths.put("als_model", alsModelPath);
    }
    
    private void validateModelFiles() throws IOException {
        for (Map.Entry<String, String> entry : modelPaths.entrySet()) {
            String modelName = entry.getKey();
            String path = entry.getValue();
            
            if (path.startsWith("classpath:")) {
                Resource resource = new ClassPathResource(path.substring(10));
                if (!resource.exists()) {
                    throw new FileNotFoundException("Model file not found: " + path + 
                        ". Please ensure you've copied the .pkl files from Google Colab to src/main/resources/ai-models/");
                }
                logger.info("Found model file: " + modelName + " at " + path);
            }
        }
    }
    
    public String getModelPath(String modelName) {
        return modelPaths.get(modelName);
    }
    
    public boolean areModelsLoaded() {
        return modelsLoaded;
    }
    
    public Map<String, String> getAllModelPaths() {
        return new HashMap<>(modelPaths);
    }
    
    // Method to get absolute path for Python script execution
    public String getAbsoluteModelPath(String modelName) throws IOException {
        String path = modelPaths.get(modelName);
        if (path != null && path.startsWith("classpath:")) {
            Resource resource = new ClassPathResource(path.substring(10));
            if (resource.exists()) {
                // For Spring Boot fat jar, we need to extract the file to temp directory
                File tempFile = File.createTempFile(modelName, ".pkl");
                tempFile.deleteOnExit();
                
                try (InputStream inputStream = resource.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    inputStream.transferTo(outputStream);
                }
                
                return tempFile.getAbsolutePath();
            }
        }
        throw new FileNotFoundException("Model file not found: " + modelName);
    }
}