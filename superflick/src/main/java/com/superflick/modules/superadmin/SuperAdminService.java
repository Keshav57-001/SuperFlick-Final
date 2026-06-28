package com.superflick.modules.superadmin;

import com.superflick.modules.superadmin.dto.AuditLogResponse;
import com.superflick.modules.superadmin.entity.AuditLog;
import com.superflick.modules.superadmin.mapper.AuditLogMapper;
import com.superflick.modules.superadmin.repository.AuditLogRepository;
import com.superflick.modules.user.dto.AccountResponse;
import com.superflick.modules.user.entity.User;
import com.superflick.modules.user.mapper.AccountMapper;
import com.superflick.modules.user.repository.UserRepository;
import com.superflick.shared.enums.AccountStatus;
import com.superflick.shared.enums.UserRole;
import com.superflick.shared.exception.ForbiddenException;
import com.superflick.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SuperAdminService {

    private final UserRepository    userRepo;
    private final AuditLogRepository auditLogRepo;
    private final AccountMapper     accountMapper;
    private final AuditLogMapper    auditLogMapper;

    public Page<AccountResponse> getAllAccounts(UserRole role, Pageable pageable) {
        if (role != null) {
            return userRepo.findByRole(role, pageable).map(accountMapper::toResponse);
        }
        return userRepo.findAll(pageable).map(accountMapper::toResponse);
    }

    @Transactional
    public void toggleAccountStatus(User superAdmin, UUID targetUserId, AccountStatus newStatus) {
        User target = userRepo.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (target.getRole() == UserRole.SUPER_ADMIN) {
            throw new ForbiddenException("Super admin accounts cannot be modified");
        }

        AccountStatus oldStatus = target.getStatus();
        target.setStatus(newStatus);
        userRepo.save(target);

        // Write audit log
        AuditLog auditLog = AuditLog.builder()
                .actorId(superAdmin.getId())
                .targetId(targetUserId)
                .targetRole(target.getRole())
                .action(oldStatus.name() + " → " + newStatus.name())
                .build();
        auditLogRepo.save(auditLog);

        log.info("Account status changed: target={} {} → {} by superAdmin={}",
                targetUserId, oldStatus, newStatus, superAdmin.getId());
    }

    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        return auditLogRepo.findAllByOrderByPerformedAtDesc(pageable)
                .map(auditLog -> {
                    User actor  = userRepo.findById(auditLog.getActorId()).orElse(null);
                    User target = userRepo.findById(auditLog.getTargetId()).orElse(null);
                    return auditLogMapper.toResponse(auditLog, actor, target);
                });
    }
}