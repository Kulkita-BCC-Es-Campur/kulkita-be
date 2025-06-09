package com.example.kulkita.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials.path:firebase-service-account.json}")
    private String firebaseCredentialsPath;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseCredentialsPath).getInputStream());

            FirebaseOptions firebaseOptions = FirebaseOptions
                    .builder()
                    .setCredentials(googleCredentials)
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "kulkita");
            log.info("Firebase app initialized successfully");

            return FirebaseMessaging.getInstance(app);
        } catch (Exception e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
            return null;
        }
    }
}