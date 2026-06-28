package com.superflick.modules.chat.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessageResponse {
    private UUID id;
    private UUID matchId;
    private UUID senderId;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;

    public ChatMessageResponse() {}

    public ChatMessageResponse(UUID id, UUID matchId, UUID senderId, String content, LocalDateTime sentAt, boolean isRead) {
        this.id = id;
        this.matchId = matchId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getMatchId() { return matchId; }
    public void setMatchId(UUID matchId) { this.matchId = matchId; }

    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}