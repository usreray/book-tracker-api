package com.example.book_tracker_api.service;

import com.example.book_tracker_api.dto.request.MessageRequest;
import com.example.book_tracker_api.dto.response.ConversationResponse;
import com.example.book_tracker_api.dto.response.MessageResponse;
import com.example.book_tracker_api.dto.response.PublicUserResponse;
import com.example.book_tracker_api.entity.Message;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.exception.ResourceNotFoundException;
import com.example.book_tracker_api.repository.MessageRepository;
import com.example.book_tracker_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse saveMessage(Long senderId, MessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .build();

        Message saved = messageRepository.save(message);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getHistory(Long userId1, Long userId2) {
        return messageRepository.findConversationBetween(userId1, userId2)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getRecentConversations(Long userId) {
        List<Message> recentMessages = messageRepository.findRecentConversationsForUser(userId);
        
        return recentMessages.stream().map(msg -> {
            User otherUser = msg.getSender().getId().equals(userId) ? msg.getReceiver() : msg.getSender();
            return ConversationResponse.builder()
                    .otherUser(PublicUserResponse.builder()
                            .id(otherUser.getId())
                            .name(otherUser.getName())
                            .profilePicturePath(otherUser.getProfilePicturePath())
                            .build())
                    .lastMessage(mapToResponse(msg))
                    .build();
        }).collect(Collectors.toList());
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .isRead(message.isRead())
                .build();
    }
}
