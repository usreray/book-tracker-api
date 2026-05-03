package com.example.book_tracker_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {
    @NotNull
    private Long receiverId;

    @NotBlank
    private String content;
}
