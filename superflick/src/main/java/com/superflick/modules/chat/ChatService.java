package com.superflick.modules.chat;

import com.superflick.modules.chat.dto.ChatMessageResponse;
import com.superflick.modules.chat.entity.Message;
import com.superflick.modules.chat.mapper.MessageMapper;
import com.superflick.modules.chat.repository.MessageRepository;
import com.superflick.modules.match.entity.Match;
import com.superflick.modules.match.repository.MatchRepository;
import com.superflick.modules.match.dto.MatchWithLastMessageResponse;
import com.superflick.modules.match.mapper.MatchMapper;
import com.superflick.shared.exception.ForbiddenException;
import com.superflick.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final MessageRepository messageRepo;
    private final MatchRepository matchRepo;
    private final MessageMapper messageMapper;
    private final MatchMapper matchMapper;

    // Manual constructor for dependency injection instead of @RequiredArgsConstructor
    public ChatService(MessageRepository messageRepo,
                       MatchRepository matchRepo,
                       MessageMapper messageMapper,
                       MatchMapper matchMapper) {
        this.messageRepo = messageRepo;
        this.matchRepo = matchRepo;
        this.messageMapper = messageMapper;
        this.matchMapper = matchMapper;
    }

    /**
     * Bridges the gap with ChatController.
     * Takes structural inputs, verifies participant permission, saves the message,
     * and maps the entity to the expected ChatMessageResponse DTO.
     */
    @Transactional
    public ChatMessageResponse sendMessage(UUID senderId, UUID matchId, String content) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new NotFoundException("Match not found"));

        assertParticipant(senderId, match);

        Message message = new Message();
        message.setMatch(match);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setRead(false);

        Message savedMessage = messageRepo.save(message);
        log.info("[CHAT] Message saved successfully: id={} matchId={} senderId={}",
                savedMessage.getId(), matchId, senderId);

        return messageMapper.toResponse(savedMessage);
    }

    @Transactional
    public Message saveMessage(UUID matchId, UUID senderId, String content) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new NotFoundException("Match not found"));

        assertParticipant(senderId, match);

        Message message = new Message();
        message.setMatch(match);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setRead(false);

        return messageRepo.save(message);
    }

    public Page<ChatMessageResponse> getMessages(UUID userId, UUID matchId, Pageable pageable) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new NotFoundException("Match not found"));

        assertParticipant(userId, match);

        return messageRepo.findByMatchIdOrderBySentAtDesc(matchId, pageable)
                .map(messageMapper::toResponse);
    }

    @Transactional
    public void markAllRead(UUID userId, UUID matchId) {
        messageRepo.markAllReadForRecipient(userId, matchId);
    }

    public List<MatchWithLastMessageResponse> getChatsForUser(UUID userId) {
        return matchRepo.findByParticipant(userId).stream()
                .map(match -> {
                    Message last = messageRepo
                            .findTopByMatchIdOrderBySentAtDesc(match.getId())
                            .orElse(null);
                    int unread = messageRepo.countUnreadForRecipient(userId, match.getId());
                    return matchMapper.toWithLastMessage(match, last, userId, unread);
                })
                .collect(Collectors.toList());
    }

    private void assertParticipant(UUID userId, Match match) {
        if (!userId.equals(match.getCandidateUserId()) && !userId.equals(match.getHrUserId())) {
            throw new ForbiddenException("You are not a participant in this chat");
        }
    }
}