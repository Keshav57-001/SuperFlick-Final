package com.superflick.modules.swipe.dto;

import java.util.UUID;

public class SwipeResponse {
    private String action;
    private boolean matched;
    private UUID matchId;

    // No-Args Constructor
    public SwipeResponse() {}

    // All-Args Constructor
    public SwipeResponse(String action, boolean matched, UUID matchId) {
        this.action = action;
        this.matched = matched;
        this.matchId = matchId;
    }

    // Standard Getters and Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public boolean isMatched() { return matched; }
    public void setMatched(boolean matched) { this.matched = matched; }

    public UUID getMatchId() { return matchId; }
    public void setMatchId(UUID matchId) { this.matchId = matchId; }
}