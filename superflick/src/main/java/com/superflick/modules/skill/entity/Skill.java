package com.superflick.modules.skill.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "skills")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Skill {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @Column(unique = true, nullable = false) private String name;
    @Column(nullable = false) private String category;
}