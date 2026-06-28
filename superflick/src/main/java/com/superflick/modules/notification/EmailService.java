package com.superflick.modules.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Sends transactional emails via Brevo (formerly Sendinblue) SMTP.
 *
 * JavaMailSender is injected with required=false so the app starts
 * cleanly if spring.mail.password is not set in the environment.
 * When mailSender is null every send is a no-op and a WARN is logged.
 *
 * NOTE: @Value must be org.springframework.beans.factory.annotation.Value
 * NOT lombok.Value — they share the same simple name and cause a compile error.
 */
@Slf4j
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    // The "from" address must match the verified sender in your Brevo account.
    // Brevo rejects mails sent from addresses that aren't verified/domain-authenticated.
    //@Value("${app.mail.from:noreply@superflick.com}")
    @Value("${app.mail.from:ranjankeshav0@gmail.com}")
    private String fromAddress;

    @Value("${app.mail.name:SuperFlick}")
    private String fromName;

    // ── Public API ────────────────────────────────────────────

    /**
     * Sends a 6-digit OTP to the given email address.
     * Called by AuthService during email registration and password reset.
     *
     * @param to  recipient email (the email the user just registered with)
     * @param otp 6-digit numeric OTP stored in Redis
     */
    public void sendOtp(String to, String otp) {
        if (mailSender == null) {
            log.warn("[MAIL NOT CONFIGURED] OTP email for {} → OTP={}", to, otp);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromName + " <" + fromAddress + ">");
            msg.setTo(to);
            msg.setSubject("SuperFlick — Your Verification Code");
            msg.setText(buildOtpEmailBody(otp));
            mailSender.send(msg);
            log.info("OTP email sent to {}", to);
        } catch (Exception ex) {
            log.error("Failed to send OTP email to {}: {}", to, ex.getMessage());
            // Don't rethrow — a mail failure must NOT break the registration flow.
            // The OTP is still stored in Redis; user can request a resend.
        }
    }

    /**
     * Sends an auto-apply notification email asynchronously.
     * Async so it never blocks the cron job thread.
     */
    @Async
    public void sendAutoApplyAlert(UUID userId, String message) {
        // Wire in UserRepository lookup to get the user's email by userId
        // when you're ready. For now it just logs.
        log.info("Auto-apply email queued for userId={}: {}", userId, message);
    }

    /**
     * Generic plain-text email utility used by other services.
     */
    public void send(String to, String subject, String body) {
        if (mailSender == null) {
            log.warn("[MAIL NOT CONFIGURED] Would send '{}' to {}", subject, to);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromName + " <" + fromAddress + ">");
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            log.info("Email sent to {}: {}", to, subject);
        } catch (Exception ex) {
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }

    // ── Private helpers ───────────────────────────────────────

    private String buildOtpEmailBody(String otp) {
        return """
                Hi there 👋
                
                Your SuperFlick verification code is:
                
                    %s
                
                This code expires in 5 minutes.
                Do NOT share this code with anyone — SuperFlick will never ask for it.
                
                If you didn't request this, please ignore this email.
                
                — The SuperFlick Team
                """.formatted(otp);
    }
}