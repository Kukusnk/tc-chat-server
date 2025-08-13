package com.example.chatapp.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:chat.app@chat.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:Chat App}")
    private String fromName;

    public void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Email address confirmation");

            String htmlContent = generateEmailContent(code);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification code sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String generateEmailContent(String code) {
        Context context = new Context();
        context.setVariable("code", code);

        return templateEngine.process("email-verification", context);
    }
}
