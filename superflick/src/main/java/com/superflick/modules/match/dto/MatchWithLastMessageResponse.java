package com.superflick.modules.match.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchWithLastMessageResponse {
    private UUID matchId;
    private UUID otherPartyUserId;
    private String otherPartyName;
    private String otherPartyRole;
    private UUID jobId;
    private String jobTitle;
    private String companyName;
    private String lastMessagePreview;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageAt;
    private int unreadCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchedAt;
}