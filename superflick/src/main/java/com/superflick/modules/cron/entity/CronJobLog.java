package com.superflick.modules.cron.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cron_job_logs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CronJobLog {
    @Id @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;

    private LocalDateTime runAt;
    private int premiumUsersProcessed;
    private int jobsScanned;
    private int autoApplicationsCreated;
    private long durationMs;
}
