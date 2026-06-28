package com.superflick.modules.auth;

import com.superflick.modules.auth.dto.*;
import com.superflick.modules.email.EmailRegisterRequest;
import com.superflick.modules.chat.dto.MessageResponse;
import com.superflick.modules.notification.EmailService;
import com.superflick.modules.notification.SmsService;
import com.superflick.modules.user.entity.User;
import com.superflick.modules.user.repository.UserRepository;
import com.superflick.shared.enums.AccountStatus;
import com.superflick.shared.enums.AuthProvider;
import com.superflick.shared.enums.UserRole;
import com.superflick.shared.exception.BadRequestException;
import com.superflick.shared.exception.ConflictException;
import com.superflick.shared.exception.NotFoundException;
import com.superflick.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository       userRepo;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final StringRedisTemplate  redisTemplate;
    private final EmailService         emailService;
    private final SmsService           smsService;

    @Value("${app.otp.ttl-seconds:300}")
    private int otpTtlSeconds;

    private static final String OTP_PREFIX     = "otp:";
    private static final String PENDING_PREFIX = "pending:";
    private static final SecureRandom RANDOM   = new SecureRandom();

    // ── Email Registration ─────────────────────────────────────

    public MessageResponse registerWithEmail(EmailRegisterRequest req) {
        String email = req.getEmail().toLowerCase().trim();

        if (userRepo.existsByEmail(email)) {
            throw new ConflictException("An account with this email already exists");
        }

        String otp     = generateOtp();
        String pending = passwordEncoder.encode(req.getPassword()) + "|" + req.getRole().name();

        redisTemplate.opsForValue().set(OTP_PREFIX     + "email:" + email, otp,     Duration.ofSeconds(otpTtlSeconds));
        redisTemplate.opsForValue().set(PENDING_PREFIX + "email:" + email, pending, Duration.ofSeconds(otpTtlSeconds));

        emailService.sendOtp(email, otp);
        log.info("Registration OTP sent to email={}", email);
        return new MessageResponse("OTP sent to " + email + ". Valid for 5 minutes.");
    }

    // ── Mobile Registration ────────────────────────────────────

    public MessageResponse registerWithMobile(MobileRegisterRequest req) {
        String phone = req.getMobile().trim();

        if (userRepo.existsByPhone(phone)) {
            throw new ConflictException("An account with this mobile already exists");
        }

        String otp     = generateOtp();
        String pending = passwordEncoder.encode(req.getPassword()) + "|" + req.getRole().name();

        redisTemplate.opsForValue().set(OTP_PREFIX     + "mobile:" + phone, otp,     Duration.ofSeconds(otpTtlSeconds));
        redisTemplate.opsForValue().set(PENDING_PREFIX + "mobile:" + phone, pending, Duration.ofSeconds(otpTtlSeconds));

        smsService.sendOtp(phone, otp);
        log.info("Registration OTP sent to mobile={}", phone);
        return new MessageResponse("OTP sent to " + phone + ". Valid for 5 minutes.");
    }

    // ── Login → OTP ────────────────────────────────────────────

    /**
     * Step 1 of OTP-based login.
     * Validates password, then sends OTP to the user's registered email/mobile.
     * Step 2: user calls POST /auth/otp/verify with the same identifier + OTP → gets JWT.
     */
//    public MessageResponse loginSendOtp(LoginRequest req) {
//        String identifier = req.getIdentifier().trim().toLowerCase();
//        boolean isEmail   = identifier.contains("@");
//
//        User user = userRepo.findByEmailOrPhone(identifier)
//                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
//
//        if (user.getStatus() == AccountStatus.BLOCKED) {
//            throw new UnauthorizedException("Your account has been blocked. Contact support.");
//        }
//
//        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
//            throw new UnauthorizedException("Invalid credentials");
//        }
//
//        String otp = generateOtp();
//        redisTemplate.opsForValue().set(
//                OTP_PREFIX + "login:" + identifier,
//                otp,
//                Duration.ofSeconds(otpTtlSeconds)
//        );
//
//        if (isEmail) {
//            emailService.sendOtp(identifier, otp);
//        } else {
//            smsService.sendOtp(identifier, otp);
//        }
//
//        log.info("Login OTP sent to {} (userId={})", identifier, user.getId());
//        return new MessageResponse("OTP sent to " + identifier + ". Valid for 5 minutes.");
//    }

    // ─────────────────────────────────────────────────────────────────────────────
// ADD this method to AuthService.java in the backend
// This enables POST /auth/login/otp → validate credentials → send OTP →
// user verifies OTP at /auth/otp/verify → JWT returned
// ─────────────────────────────────────────────────────────────────────────────

    /**
     * Login via OTP: validates password first, then sends OTP to user's email/mobile.
     * The user then calls POST /auth/otp/verify with the same identifier to get JWT.
     */
    public MessageResponse loginSendOtp(LoginRequest req) {
        String identifier = req.getIdentifier().trim().toLowerCase();

        User user = userRepo.findByEmailOrPhone(identifier)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (user.getStatus() == AccountStatus.BLOCKED) {
            throw new UnauthorizedException("Your account has been blocked. Contact support.");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Generate OTP and store it in Redis (reuse the same key format as registration)
        String otp = generateOtp();
        boolean isEmail = identifier.contains("@");
        String channel = isEmail ? "email" : "mobile";

        // Store OTP for login verification (5-min TTL, same as registration)
        redisTemplate.opsForValue().set(
                OTP_PREFIX + "login:" + identifier,
                otp,
                Duration.ofSeconds(otpTtlSeconds)
        );

        // Send OTP
        if (isEmail) {
            emailService.sendOtp(identifier, otp);
        } else {
            smsService.sendOtp(identifier, otp);
        }

        log.info("Login OTP sent to {} channel={}", identifier, channel);
        return new MessageResponse("OTP sent to " + identifier + ". Valid for 5 minutes.");
    }

// ─────────────────────────────────────────────────────────────────────────────
// ALSO UPDATE verifyOtp() to check login OTP keys in addition to register OTPs:
// ─────────────────────────────────────────────────────────────────────────────
// In verifyOtp(), after the existing register OTP check, add:
//
//   // Check login OTP if registration OTP not found
//   if (redisOtp == null) {
//       redisOtp = redisTemplate.opsForValue().get(OTP_PREFIX + "login:" + identifier);
//       isLoginFlow = true;
//   }
//
// For login flow: don't create a new user, just find existing user and return JWT:
//
//   if (isLoginFlow) {
//       User user = isEmail
//           ? userRepo.findByEmail(identifier).orElseThrow(...)
//           : userRepo.findByPhone(identifier).orElseThrow(...);
//       redisTemplate.delete(OTP_PREFIX + "login:" + identifier);
//       return AuthResponse.builder()
//           .token(jwtService.generateToken(user))
//           .role(user.getRole())
//           .needsProfileSetup(!user.isProfileComplete())
//           .build();
//   }
// ─────────────────────────────────────────────────────────────────────────────

    // ── OTP Verification (Registration + Login) ───────────────

    /**
     * Unified OTP verification endpoint for both registration and login flows.
     * Checks registration OTP first, then login OTP.
     */
    public AuthResponse verifyOtp(OtpVerifyRequest req) {
        String identifier = req.getIdentifier().trim();
        boolean isEmail   = identifier.contains("@");
        String  channel   = isEmail ? "email" : "mobile";

        // 1) Check registration OTP
        String registerOtp = redisTemplate.opsForValue().get(OTP_PREFIX + channel + ":" + identifier);
        if (registerOtp != null) {
            if (!registerOtp.equals(req.getOtp())) {
                throw new BadRequestException("Incorrect OTP. Please try again.");
            }
            return completeRegistration(identifier, isEmail, channel);
        }

        // 2) Check login OTP
        String loginOtp = redisTemplate.opsForValue().get(OTP_PREFIX + "login:" + identifier);
        if (loginOtp != null) {
            if (!loginOtp.equals(req.getOtp())) {
                throw new BadRequestException("Incorrect OTP. Please try again.");
            }
            return completeLogin(identifier);
        }

        // 3) Neither found → expired
        throw new BadRequestException("OTP has expired or was already used. Please request a new one.");
    }

    private AuthResponse completeRegistration(String identifier, boolean isEmail, String channel) {
        String pendingData = redisTemplate.opsForValue().get(PENDING_PREFIX + channel + ":" + identifier);
        if (pendingData == null) {
            throw new BadRequestException("Registration session expired. Please register again.");
        }

        String[] parts        = pendingData.split("\\|", 2);
        String   passwordHash = parts[0];
        UserRole role         = UserRole.valueOf(parts[1]);

        User user = User.builder()
                .email(isEmail  ? identifier : null)
                .phone(!isEmail ? identifier : null)
                .passwordHash(passwordHash)
                .role(role)
                .authProvider(isEmail ? AuthProvider.EMAIL : AuthProvider.MOBILE)
                .status(AccountStatus.ACTIVE)
                .profileComplete(false)
                .build();
        userRepo.save(user);

        redisTemplate.delete(OTP_PREFIX     + channel + ":" + identifier);
        redisTemplate.delete(PENDING_PREFIX + channel + ":" + identifier);

        log.info("User registered via OTP: id={} role={}", user.getId(), role);
        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .role(role)
                .needsProfileSetup(true)
                .build();
    }

    private AuthResponse completeLogin(String identifier) {
        User user = userRepo.findByEmailOrPhone(identifier)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getStatus() == AccountStatus.BLOCKED) {
            throw new UnauthorizedException("Your account has been blocked.");
        }

        redisTemplate.delete(OTP_PREFIX + "login:" + identifier);

        log.info("User logged in via OTP: id={}", user.getId());
        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .role(user.getRole())
                .needsProfileSetup(!user.isProfileComplete())
                .build();
    }

    // ── Direct Login (fallback if OTP not available) ──────────

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmailOrPhone(req.getIdentifier())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (user.getStatus() == AccountStatus.BLOCKED) throw new UnauthorizedException("Account blocked.");
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) throw new UnauthorizedException("Invalid credentials");
        return AuthResponse.builder().token(jwtService.generateToken(user)).role(user.getRole()).needsProfileSetup(!user.isProfileComplete()).build();
    }

    // ── Forgot Password ────────────────────────────────────────

    public MessageResponse forgotPassword(String email) {
        email = email.toLowerCase().trim();
        if (!userRepo.existsByEmail(email)) {
            return new MessageResponse("If that email is registered, you'll receive an OTP shortly.");
        }
        String otp = generateOtp();
        redisTemplate.opsForValue().set(OTP_PREFIX + "reset:" + email, otp, Duration.ofSeconds(otpTtlSeconds));
        emailService.sendOtp(email, otp);
        return new MessageResponse("Password reset OTP sent to " + email);
    }

    public MessageResponse resetPassword(ResetPasswordRequest req) {
        String email    = req.getIdentifier().toLowerCase().trim();
        String redisOtp = redisTemplate.opsForValue().get(OTP_PREFIX + "reset:" + email);
        if (redisOtp == null || !redisOtp.equals(req.getOtp())) throw new BadRequestException("OTP invalid or expired");
        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);
        redisTemplate.delete(OTP_PREFIX + "reset:" + email);
        return new MessageResponse("Password updated. Please log in.");
    }

    // ── Private ───────────────────────────────────────────────

    private String generateOtp() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}