package com.example.book_tracker_api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotBlank private String bookIsbn;
    @Min(1) @Max(5) private int rating;
    private String comment;

    // Book data — auto-upserted when writing a review if book doesn't exist
    private String title;
    private String author;
    private String description;
    private int pageCount;
    private double avgRating;
    private String thumbnailUrl;
    private Integer coverId;
    private String workKey;
}
