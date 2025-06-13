package com.example.LMS.service;

import com.example.LMS.client.EmailClient;
import com.example.LMS.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final EmailClient emailClient;

    public void sendEmail(String to, String subject, String body) {
        try {
            EmailRequest emailRequest = new EmailRequest(
                    to,
                    subject,
                    body);

            emailClient.sendEmail(emailRequest);

            log.info("Email sent successfully to: {}", to);
        } catch(Exception e) {
            log.error("Could not send email to {}: {}", to, e.getMessage());
        }
    }
}

