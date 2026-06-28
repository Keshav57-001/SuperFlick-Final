package com.superflick.modules.notification;

import com.superflick.modules.notification.dto.NotificationResponse;
import com.superflick.modules.notification.entity.Notification;
import com.superflick.modules.notification.mapper.NotificationMapper;
import com.superflick.modules.notification.repository.NotificationRepository;
import com.superflick.shared.enums.NotificationType;
import com.superflick.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

   // private static final Logger log = LoggerFactory.LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notifRepo;
    private final NotificationMapper     notifMapper;
    private final SimpMessagingTemplate  messagingTemplate;
    private final EmailService           emailService;

    // Manual constructor setup — clears missing Lombok annotation issues cleanly
    public NotificationService(NotificationRepository notifRepo,
                               NotificationMapper notifMapper,
                               SimpMessagingTemplate messagingTemplate,
                               EmailService emailService) {
        this.notifRepo = notifRepo;
        this.notifMapper = notifMapper;
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
    }

    // ── Domain events ─────────────────────────────────────────

    public void sendMatchNotification(UUID candidateUserId, UUID hrUserId, UUID jobId) {
        String msg = "🎉 You matched! Chat is now available.";
        persist(candidateUserId, NotificationType.MATCH, msg);
        persist(hrUserId,        NotificationType.MATCH, msg);
        pushRealtime(candidateUserId, msg);
        pushRealtime(hrUserId,        msg);
    }

    public void sendAutoApplyNotification(UUID candidateUserId, String jobTitle, String company) {
        String msg = "SuperFlick AutoApply submitted your application to "
                + company + " – " + jobTitle + ".";
        persist(candidateUserId, NotificationType.AUTO_APPLY, msg);
        pushRealtime(candidateUserId, msg);
        emailService.sendAutoApplyAlert(candidateUserId, msg);
    }

    public void sendPaymentSuccessNotification(UUID userId, String plan) {
        String msg = "✅ Premium activated! Your " + plan + " plan is now live.";
        persist(userId, NotificationType.PAYMENT, msg);
        pushRealtime(userId, msg);
    }

    // ── Read operations ───────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getForUser(UUID userId, Pageable pageable) {
        return notifRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notifMapper::toResponse);
    }

    /**
     * Mapped for components using 'getUnreadCount' directly
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notifRepo.countByUserIdAndReadFalse(userId);
    }

    /**
     * ✅ Added Alias: Resolves the missing method error in your controller
     */
    @Transactional(readOnly = true)
    public long getUnreadCountForUser(UUID userId) {
        return this.getUnreadCount(userId);
    }

    public void markRead(UUID userId, UUID notifId) {
        Notification notif = notifRepo.findById(notifId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        if (!notif.getUserId().equals(userId)) return;
        notif.setRead(true);
        notifRepo.save(notif);
    }

    public void markAllRead(UUID userId) {
        notifRepo.markAllReadForUser(userId);
    }

    // ── Helpers ───────────────────────────────────────────────

    private void persist(UUID userId, NotificationType type, String message) {
        Notification notif = new Notification();
        notif.setUserId(userId);
        notif.setType(type);
        notif.setMessage(message);
        notif.setRead(false);

        notifRepo.save(notif);
    }

    @Async
    void pushRealtime(UUID userId, String message) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(), "/queue/notifications",
                    Map.of("message", message));
        } catch (Exception ex) {
           //log.warn("WebSocket push failed for userId={}: {}", userId, ex.getMessage());
        }
    }
}
