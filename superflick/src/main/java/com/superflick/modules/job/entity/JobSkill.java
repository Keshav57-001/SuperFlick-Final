package com.superflick.modules.job.entity;

import com.superflick.modules.skill.entity.Skill;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "job_skills",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "skill_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class JobSkill {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "is_required", nullable = false) private boolean required;

    public boolean isRequired()           { return required; }
    public void    setRequired(boolean v) { this.required = v; }
}