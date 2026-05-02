package com.example.book_tracker_api.dto.response;

import com.example.book_tracker_api.entity.Activity;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data @Builder
public class ActivityResponse {
    private Long id;
    private PublicUserResponse user;
    private String type; // STARTED_READING, FINISHED_READING, WROTE_REVIEW, EARNED_BADGE
    private String bookIsbn;
    private String bookTitle;
    private String bookThumbnailUrl;
    private Long reviewId;
    private String badgeType;
    private Instant createdAt;

    public static ActivityResponse from(Activity activity, PublicUserResponse userResponse) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .user(userResponse)
                .type(activity.getType().name())
                .bookIsbn(activity.getBookIsbn())
                .bookTitle(activity.getBookTitle())
                .bookThumbnailUrl(activity.getBookThumbnailUrl())
                .reviewId(activity.getReviewId())
                .badgeType(activity.getBadgeType() != null ? activity.getBadgeType().name() : null)
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
