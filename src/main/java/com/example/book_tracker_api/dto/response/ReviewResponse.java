package com.example.book_tracker_api.dto.response;

import com.example.book_tracker_api.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data @Builder @AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String bookIsbn;
    private int rating;
    private String comment;
    private Instant createdAt;
    private String userName;
    private Long userId;
    private String userProfilePicturePath;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .bookIsbn(review.getBookIsbn())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .userName(review.getUser().getName())
                .userId(review.getUser().getId())
                .userProfilePicturePath(review.getUser().getProfilePicturePath())
                .build();
    }
}
