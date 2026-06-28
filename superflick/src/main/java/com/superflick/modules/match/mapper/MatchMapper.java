package com.superflick.modules.match.mapper;

import com.superflick.modules.chat.entity.Message;
import com.superflick.modules.match.dto.MatchResponse;
import com.superflick.modules.match.dto.MatchWithLastMessageResponse;
import com.superflick.modules.match.entity.Match;
import com.superflick.modules.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MatchMapper {

    public MatchResponse toResponse(Match match, User candidate, User hr) {
        if (match == null) return null;

        String hrCompany = null;
        if (hr != null && hr.getHrProfile() != null && hr.getHrProfile().getCompany() != null) {
            hrCompany = hr.getHrProfile().getCompany().getName();
        }

        return MatchResponse.builder()
                .id(match.getId())
                .candidateUserId(match.getCandidateUserId())
                .candidateName(candidate != null ? candidate.getEmail() : null)
                .hrUserId(match.getHrUserId())
                .hrName(hr != null ? hr.getEmail() : null)
                .hrCompany(hrCompany)
                .jobId(match.getJobId())
                .jobTitle(match.getJob() != null ? match.getJob().getTitle() : null)
                .matchedAt(match.getMatchedAt())
                .build();
    }

    public MatchWithLastMessageResponse toWithLastMessage(Match match,
                                                          Message lastMessage,
                                                          UUID viewerUserId,
                                                          int unreadCount) {
        if (match == null) return null;

        boolean viewerIsCandidate = viewerUserId.equals(match.getCandidateUserId());
        UUID otherPartyId  = viewerIsCandidate ? match.getHrUserId() : match.getCandidateUserId();
        String otherPartyRole = viewerIsCandidate ? "HR" : "CANDIDATE";

        String preview = null;
        if (lastMessage != null && lastMessage.getContent() != null) {
            String raw = lastMessage.getContent();
            preview = raw.length() > 100 ? raw.substring(0, 100) + "..." : raw;
        }

        String jobTitle    = match.getJob() != null ? match.getJob().getTitle() : null;
        String companyName = (match.getJob() != null && match.getJob().getCompany() != null)
                ? match.getJob().getCompany().getName() : null;

        return MatchWithLastMessageResponse.builder()
                .matchId(match.getId())
                .otherPartyUserId(otherPartyId)
                .otherPartyRole(otherPartyRole)
                .jobId(match.getJobId())
                .jobTitle(jobTitle)
                .companyName(companyName)
                .lastMessagePreview(preview)
                .lastMessageAt(lastMessage != null ? lastMessage.getSentAt() : null)
                .unreadCount(unreadCount)
                .matchedAt(match.getMatchedAt())
                .build();
    }
}