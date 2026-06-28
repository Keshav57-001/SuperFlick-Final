package com.superflick.modules.user;

import com.superflick.modules.auth.OtpService;
import com.superflick.modules.user.dto.UpdatePasswordRequest;
import com.superflick.modules.user.dto.UserResponse;
import com.superflick.modules.user.entity.User;
import com.superflick.modules.user.mapper.UserMapper;
import com.superflick.modules.user.repository.UserRepository;
import com.superflick.shared.exception.BadRequestException;
import com.superflick.shared.exception.ConflictException;
import com.superflick.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository  userRepo;
    private final PasswordEncoder passwordEncoder;
    private final OtpService      otpService;
    private final UserMapper      userMapper;    // injected — NOT static

    public UserResponse getById(UUID id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toResponse(user);      // instance call ✓
    }

    public void updateEmail(UUID userId, String newEmail, String otp) {
        otpService.verifyRaw(newEmail, otp);
        User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (userRepo.existsByEmail(newEmail))
            throw new ConflictException("Email already in use");
        user.setEmail(newEmail);
        userRepo.save(user);
    }

    public void updatePhone(UUID userId, String newPhone, String otp) {
        otpService.verifyRaw(newPhone, otp);
        User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        user.setPhone(newPhone);
        userRepo.save(user);
    }

    public void updatePassword(UUID userId, UpdatePasswordRequest req) {
        if (!req.getNewPassword().equals(req.getConfirmPassword()))
            throw new BadRequestException("Passwords do not match");
        User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash()))
            throw new BadRequestException("Current password is incorrect");
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);
    }

    public void deleteAccount(UUID userId) {
        userRepo.deleteById(userId);
    }
}