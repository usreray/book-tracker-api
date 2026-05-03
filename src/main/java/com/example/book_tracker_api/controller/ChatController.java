package com.example.book_tracker_api.controller;

import com.example.book_tracker_api.dto.request.MessageRequest;
import com.example.book_tracker_api.dto.response.ConversationResponse;
import com.example.book_tracker_api.dto.response.MessageResponse;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth == null) return;
        
        User sender = (User) auth.getPrincipal();
        MessageResponse savedMsg = chatService.saveMessage(sender.getId(), request);

        // Send to receiver via topic
        messagingTemplate.convertAndSend(
                "/topic/chat.user." + request.getReceiverId(),
                savedMsg
        );
        
        // Also send back to sender so their UI updates if connected from multiple devices
        messagingTemplate.convertAndSend(
                "/topic/chat.user." + sender.getId(),
                savedMsg
        );
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(chatService.getRecentConversations(user.getId()));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<MessageResponse>> getHistory(@PathVariable Long userId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(chatService.getHistory(currentUser.getId(), userId));
    }
}
