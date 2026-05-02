package com.example.book_tracker_api.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ChallengeRequest {
    @Min(1) private int targetBookCount;
}
