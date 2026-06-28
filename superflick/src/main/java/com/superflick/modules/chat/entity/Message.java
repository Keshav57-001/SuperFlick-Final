package com.superflick.modules.chat.entity;

import com.superflick.modules.match.entity.Match;
import com.superflick.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Message {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "sender_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    @Column(name = "is_read", nullable = false) private boolean read;
    @Column(name = "sent_at") private LocalDateTime sentAt;

    @PrePersist protected void onCreate() { if (sentAt == null) sentAt = LocalDateTime.now(); }
}