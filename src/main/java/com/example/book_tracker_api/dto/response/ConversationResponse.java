package com.example.book_tracker_api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationResponse {
    private PublicUserResponse otherUser;
    private MessageResponse lastMessage;
}
