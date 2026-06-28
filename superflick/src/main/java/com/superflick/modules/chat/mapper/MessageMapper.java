package com.superflick.modules.chat.mapper;

import com.superflick.modules.chat.dto.ChatMessageResponse;
import com.superflick.modules.chat.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public ChatMessageResponse toResponse(Message message) {
        if (message == null) {
            return null;
        }

        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setSenderId(message.getSenderId());
        response.setRead(message.isRead());

        // Safely extract fields from the parent Match relationship if present
        if (message.getMatch() != null) {
            response.setMatchId(message.getMatch().getId());
        }

        // Ensure this matches the timestamp getter field name inside your Message entity (e.g., getSentAt() or getCreatedAt())
        response.setSentAt(message.getSentAt());

        return response;
    }
}