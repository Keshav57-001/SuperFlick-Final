package com.superflick.modules.hr.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Company {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @Column(nullable = false)        private String name;
    private String website;
    private String industry;
    private String size;
    private String location;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "logo_url")       private String logoUrl;
    @Column(name = "is_premium", nullable = false) private boolean premium;
    @Column(name = "created_at")     private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}