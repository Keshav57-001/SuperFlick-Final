package com.superflick.modules.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.superflick.modules.auth.dto.RegisterRequest;
import com.superflick.modules.notification.EmailService;
import com.superflick.modules.notification.SmsService;
import com.superflick.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService  emailService;   // injected ✓
    private final SmsService    smsService;     // injected ✓
    private final ObjectMapper  objectMapper;

    @Value("${app.otp.ttl-seconds:300}")
    private long ttlSeconds;

    public void sendEmailOtp(String email, Object pendingData) {
        String otp = generateOtp();
        redisTemplate.opsForValue().set("otp:" + email, otp, ttlSeconds, TimeUnit.SECONDS);
        if (pendingData != null) {
            try {
                redisTemplate.opsForValue().set(
                        "pending:" + email,
                        objectMapper.writeValueAsString(pendingData),
                        ttlSeconds, TimeUnit.SECONDS);
            } catch (Exception ex) {
                log.error("Failed to store pending registration: {}", ex.getMessage());
            }
        }
        emailService.sendOtp(email, otp);
    }

    public void sendSmsOtp(String mobile, Object pendingData) {
        String otp = generateOtp();
        redisTemplate.opsForValue().set("otp:" + mobile, otp, ttlSeconds, TimeUnit.SECONDS);
        if (pendingData != null) {
            try {
                redisTemplate.opsForValue().set(
                        "pending:" + mobile,
                        objectMapper.writeValueAsString(pendingData),
                        ttlSeconds, TimeUnit.SECONDS);
            } catch (Exception ex) {
                log.error("Failed to store pending registration: {}", ex.getMessage());
            }
        }
        smsService.sendOtp(mobile, otp);
    }

    public RegisterRequest verifyAndFetch(String identifier, String otp) {
        verifyRaw(identifier, otp);
        String json = redisTemplate.opsForValue().get("pending:" + identifier);
        if (json == null) throw new BadRequestException("Registration session expired. Please register again.");
        redisTemplate.delete("pending:" + identifier);
        try {
            return objectMapper.readValue(json, RegisterRequest.class);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid registration data. Please register again.");
        }
    }

    public void verifyRaw(String identifier, String otp) {
        String stored = redisTemplate.opsForValue().get("otp:" + identifier);
        if (stored == null) throw new BadRequestException("OTP expired. Please request a new one.");
        if (!stored.equals(otp)) throw new BadRequestException("Invalid OTP.");
        redisTemplate.delete("otp:" + identifier);
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}