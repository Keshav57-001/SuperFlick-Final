package com.superflick.modules.candidate.entity;

import com.superflick.modules.skill.entity.Skill;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "candidate_skills",
        uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_id", "skill_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidateSkill {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    private String proficiency;

    @Column(name = "is_ai_extracted") private boolean aiExtracted;

    public boolean isAiExtracted()          { return aiExtracted; }
    public void    setAiExtracted(boolean v) { this.aiExtracted = v; }
}