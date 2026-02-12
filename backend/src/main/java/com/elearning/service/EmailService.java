package com.elearning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Async
    public void sendCourseStatusEmail(String to, String instructorName, String courseTitle, String status) {
        String subject = "Course Status Update - " + courseTitle;
        String text = String.format(
                "Dear %s,\n\n" +
                "We are writing to inform you about the status of your course '%s'.\n\n" +
                "Status: %s\n\n" +
                "%s\n\n" +
                "Best regards,\n" +
                "E-Learning Platform Team",
                instructorName,
                courseTitle,
                status.toUpperCase(),
                getStatusMessage(status)
        );
        
        sendEmail(to, subject, text);
    }

    private String getStatusMessage(String status) {
        switch (status.toLowerCase()) {
            case "accepted":
                return "Congratulations! Your course has been approved and is now visible to students.";
            case "declined":
                return "Unfortunately, your course has been declined. Please review our guidelines and submit again.";
            default:
                return "Your course status has been updated.";
        }
    }
}
