package com.superflick.modules.matching;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchScoreResult {
    private int score;
    private int matchedSkillCount;
    private boolean meetsAutoApplyCriteria;
    private List<String> reasons;
}