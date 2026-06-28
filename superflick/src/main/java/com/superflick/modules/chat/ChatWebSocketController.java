package com.superflick.modules.chat;

import com.superflick.modules.chat.dto.ChatMessageResponse;
import com.superflick.modules.chat.dto.MessageResponse;
import com.superflick.modules.chat.dto.SendMessageRequest;
import com.superflick.modules.chat.entity.Message;
import com.superflick.modules.chat.mapper.MessageMapper;
import com.superflick.modules.match.entity.Match;
import com.superflick.modules.match.repository.MatchRepository;
import com.superflick.shared.exception.ForbiddenException;
import com.superflick.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService           chatService;
    private final MatchRepository       matchRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageMapper         messageMapper;    // injected instance ✓

    @MessageMapping("/chat.send/{matchId}")
    public void sendMessage(@DestinationVariable String matchId,
                            @Payload SendMessageRequest req,
                            Principal principal) {

        UUID matchUuid  = UUID.fromString(matchId);
        UUID senderUuid = UUID.fromString(principal.getName());

        Match match = matchRepo.findById(matchUuid)
                .orElseThrow(() -> new NotFoundException("Match not found"));

        if (!senderUuid.equals(match.getCandidateUserId())
                && !senderUuid.equals(match.getHrUserId())) {
            throw new ForbiddenException("Not a participant");
        }

        Message saved = chatService.saveMessage(matchUuid, senderUuid, req.getContent());
        ChatMessageResponse response = messageMapper.toResponse(saved);  // instance call ✓

        UUID recipientId = senderUuid.equals(match.getCandidateUserId())
                ? match.getHrUserId() : match.getCandidateUserId();

        messagingTemplate.convertAndSendToUser(
                recipientId.toString(), "/queue/messages", response);

        log.debug("WS message: matchId={} from={}", matchId, senderUuid);
    }
}