package com.superflick.modules.cron.repository;

import com.superflick.modules.cron.entity.CronJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CronJobLogRepository extends JpaRepository<CronJobLog, UUID> {}
