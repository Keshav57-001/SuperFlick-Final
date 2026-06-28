package com.superflick.modules.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class SmsService {

    @Value("${app.sms.msg91.api-key:}")
    private String msg91ApiKey;

    @Value("${app.sms.msg91.sender-id:SFLICK}")
    private String senderId;

    @Value("${app.sms.msg91.template-id:}")
    private String templateId;

    private final RestTemplate restTemplate;

    public SmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void sendOtp(String mobile, String otp) {
        if (msg91ApiKey == null || msg91ApiKey.isBlank()) {
            log.info("[DEV] SMS OTP for mobile={} → OTP={}", mobile, otp);
            return;
        }
        try {
            String url = "https://api.msg91.com/api/v5/otp";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authkey", msg91ApiKey);
            Map<String, Object> body = Map.of(
                    "template_id", templateId,
                    "mobile",      "91" + mobile,
                    "authkey",     msg91ApiKey,
                    "otp",         otp
            );
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, headers), String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("OTP SMS sent to mobile={}", mobile);
            } else {
                log.warn("OTP SMS failed for mobile={} status={}", mobile, response.getStatusCode());
            }
        } catch (Exception ex) {
            log.error("OTP SMS error for mobile={}: {}", mobile, ex.getMessage());
        }
    }
}