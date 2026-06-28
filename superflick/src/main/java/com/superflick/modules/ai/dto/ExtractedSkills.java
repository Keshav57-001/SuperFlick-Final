package com.superflick.modules.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedSkills {

    /**
     * Raw skill names extracted from the resume by the AI model.
     * These are matched against the skills table by SkillRepository.findByNameIgnoreCase().
     * Example: ["Java", "Spring Boot", "Docker", "AWS"]
     */
    private List<String> skillNames;

    /**
     * The total number of skills found in the resume.
     */
    private int totalFound;

    /**
     * How many of the extracted skill names were successfully matched
     * to existing records in the skills master table.
     */
    private int totalMatched;

    /**
     * Skill names that were extracted but did NOT match any existing skill
     * in the database. These can be reviewed by admins to add new skills.
     */
    private List<String> unmatchedSkillNames;

    /**
     * Raw text from which skills were extracted. Useful for debugging
     * or feeding into a second extraction pass.
     */
    private String extractedFromText;
}