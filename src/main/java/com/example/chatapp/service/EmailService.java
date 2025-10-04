package com.example.chatapp.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
public class EmailService {

    //private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private final SendGrid sendGrid;

    @Value("${sendgrid.from.email:chat.app@chat.com}")
    private String fromEmail;

    @Value("${sendgrid.from.name:Chat App}")
    private String fromName;

    public EmailService(SpringTemplateEngine templateEngine, @Value("${sendgrid.api-key}") String apiKey) {
        this.templateEngine = templateEngine;
        this.sendGrid = new SendGrid(apiKey);
    }

    public void sendVerificationEmail(String toEmail, String code) {
        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail, fromName);
//            helper.setTo(toEmail);
//            helper.setSubject("Email address confirmation");
//
//            String htmlContent = generateEmailContent(code);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//            log.info("Verification code sent to {}", toEmail);
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            String subject = "Email address confirmation";

            String htmlContent = generateEmailContent(code);
            Content content = new Content("text/html", htmlContent);  // HTML из Thymeleaf

            Mail mail = new Mail(from, subject, to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            if (response.getStatusCode() >= 400) {  // Ошибка от SendGrid
                log.error("SendGrid error: Status {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to send email via SendGrid");
            }

            log.info("Verification code sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String code) {
        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail, fromName);
//            helper.setTo(toEmail);
//            helper.setSubject("Password reset");
//
//            String htmlContent = generatePasswordResetEmailContent(code);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//            log.info("Password reset code sent to{}", toEmail);
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            String subject = "Password reset";

            String htmlContent = generatePasswordResetEmailContent(code);
            Content content = new Content("text/html", htmlContent);  // HTML из Thymeleaf

            Mail mail = new Mail(from, subject, to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            if (response.getStatusCode() >= 400) {
                log.error("SendGrid error: Status {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to send email via SendGrid");
            }

            log.info("Password reset code sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String generateEmailContent(String code) {
        Context context = new Context();
        context.setVariable("code", code);

        return templateEngine.process("email-verification", context);
    }

    private String generatePasswordResetEmailContent(String code) {
        Context context = new Context();
        context.setVariable("code", code);

        return templateEngine.process("password-reset", context);
    }

}
