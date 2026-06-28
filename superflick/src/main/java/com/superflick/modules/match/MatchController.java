package com.superflick.modules.match;

import com.superflick.modules.chat.ChatService;
import com.superflick.modules.match.dto.MatchResponse;
import com.superflick.modules.match.dto.MatchWithLastMessageResponse;
import com.superflick.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Matches", description = "Match management")
public class MatchController {

    private final MatchService matchService;
    private final ChatService  chatService;   // chat list lives here ✓

    @GetMapping
    @Operation(summary = "Get all my matches")
    public ResponseEntity<List<MatchResponse>> getMyMatches(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(matchService.getMatchesForUser(user.getId()));
    }

    @GetMapping("/chats")
    @Operation(summary = "Get chat list with last message")
    public ResponseEntity<List<MatchWithLastMessageResponse>> getChatList(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getChatsForUser(user.getId())); // ✓
    }

    @GetMapping("/{matchId}")
    @Operation(summary = "Get a specific match")
    public ResponseEntity<MatchResponse> getMatch(@AuthenticationPrincipal User user,
                                                  @PathVariable UUID matchId) {
        return ResponseEntity.ok(matchService.getMatch(user.getId(), matchId));
    }
}