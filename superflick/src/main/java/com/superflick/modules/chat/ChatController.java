
package com.superflick.modules.chat;
import com.superflick.modules.chat.dto.ChatMessageResponse;
import com.superflick.modules.chat.dto.SendMessageRequest;
import com.superflick.modules.match.dto.MatchWithLastMessageResponse;
import com.superflick.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/chat")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Chat", description = "Real-time chat between matched candidates and HR")
public class ChatController {
    private final ChatService chatService;
    // Explicit dependency injection constructor
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    @GetMapping("/matches")
    @Operation(summary = "Get chat list with last message preview")
    public ResponseEntity<List<MatchWithLastMessageResponse>> getChats(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getChatsForUser(user.getId()));
    }
    @GetMapping("/{matchId}/messages")
    @Operation(summary = "Get paginated message history for a match")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
            @AuthenticationPrincipal User user,
            @PathVariable UUID matchId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(chatService.getMessages(user.getId(), matchId, pageable));
    }
    @PostMapping("/{matchId}/messages")
    @Operation(summary = "Send a new message into a conversation thread")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @AuthenticationPrincipal User user,
            @PathVariable UUID matchId,
            @RequestBody SendMessageRequest request) {
        ChatMessageResponse response = chatService.sendMessage(user.getId(), matchId, request.getContent());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{matchId}/read")
    @Operation(summary = "Mark all messages in a match as read")
    public ResponseEntity<Void> markRead(@AuthenticationPrincipal User user,
                                         @PathVariable UUID matchId) {
        chatService.markAllRead(user.getId(), matchId);
        return ResponseEntity.ok().build();
    }
}