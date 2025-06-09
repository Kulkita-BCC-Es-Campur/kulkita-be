package com.example.kulkita.service.impl;

import com.example.kulkita.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from:noreply@foodia.com}")
    private String fromEmail;

    @Value("${app.name:Kulkita}")
    private String appName;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendVerificationEmail(String to, String otp) {
        try {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("otp", otp);
            templateModel.put("appName", appName);
            templateModel.put("validityMinutes", 5);

            String htmlContent = getEmailContent("email-verification", templateModel);

            sendHtmlEmail(to, "Verify Your Email - " + appName, htmlContent);
            log.info("Verification email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String otp) {
        try {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("otp", otp);
            templateModel.put("appName", appName);
            templateModel.put("validityMinutes", 5);

            String htmlContent = getEmailContent("password-reset", templateModel);

            sendHtmlEmail(to, "Reset Your Password - " + appName, htmlContent);
            log.info("Password reset email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String username) {
        try {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("username", username);
            templateModel.put("appName", appName);

            String htmlContent = getEmailContent("welcome", templateModel);

            sendHtmlEmail(to, "Welcome to " + appName + "!", htmlContent);
            log.info("Welcome email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {}", to, e);
        }
    }

    private String getEmailContent(String templateName, Map<String, Object> templateModel) {
        Context context = new Context();
        context.setVariables(templateModel);
        return templateEngine.process(templateName, context);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom(fromEmail);

        mailSender.send(message);
    }
}