package com.superflick.modules.cron;

import com.superflick.modules.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionExpiryCronJob {

    private final SubscriptionService subscriptionService;

    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Kolkata")
    public void expireStaleSubscriptions() {
        log.info("Running subscription expiry check...");
        subscriptionService.expireStaleSubscriptions();
    }
}