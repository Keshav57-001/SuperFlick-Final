package com.superflick.modules.payment.gateway;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.superflick.shared.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class RazorpayService {

    @Value("${app.razorpay.key-id}")
    private String keyId;

    @Value("${app.razorpay.key-secret}")
    private String keySecret;

    @Value("${app.razorpay.webhook-secret}")
    private String webhookSecret;

    public String createOrder(BigDecimal amountInRupees, String currency) {
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInRupees.multiply(BigDecimal.valueOf(100)).intValue());
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "sf_order_" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1);

            Order order = client.orders.create(orderRequest);

            log.info("Razorpay order created: id={} amount={}", order.get("id"), amountInRupees);
            return order.get("id");

        } catch (RazorpayException ex) {
            log.error("Razorpay order creation failed: {}", ex.getMessage());
            throw new BadRequestException("Payment order creation failed: " + ex.getMessage());
        }
    }

    public void verifyWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            if (!hex.toString().equals(signature)) {
                throw new BadRequestException("Invalid Razorpay webhook signature");
            }

            log.info("Razorpay webhook signature verified");

        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Webhook signature error: {}", ex.getMessage());
            throw new BadRequestException("Webhook signature verification failed");
        }
    }

    public String getKeyId() {
        return keyId;
    }
}