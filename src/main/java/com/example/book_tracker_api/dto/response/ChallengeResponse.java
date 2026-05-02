package com.example.book_tracker_api.dto.response;

import com.example.book_tracker_api.entity.ReadingChallenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class ChallengeResponse {
    private Long id;
    private int year;
    private int targetBookCount;
    private int currentCount;
    private double progress;
    private boolean goalReached;

    public static ChallengeResponse from(ReadingChallenge challenge) {
        double progress = challenge.getTargetBookCount() > 0
                ? Math.min(1.0, (double) challenge.getCurrentCount() / challenge.getTargetBookCount())
                : 0.0;
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .year(challenge.getYear())
                .targetBookCount(challenge.getTargetBookCount())
                .currentCount(challenge.getCurrentCount())
                .progress(progress)
                .goalReached(challenge.getCurrentCount() >= challenge.getTargetBookCount())
                .build();
    }
}
