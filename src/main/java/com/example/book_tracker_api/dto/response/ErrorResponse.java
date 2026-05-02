package com.example.book_tracker_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data @Builder @AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    @Builder.Default
    private Instant timestamp = Instant.now();
}
