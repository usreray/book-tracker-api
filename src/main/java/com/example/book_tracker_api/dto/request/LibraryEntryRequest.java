package com.example.book_tracker_api.dto.request;

import com.example.book_tracker_api.entity.ReadingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LibraryEntryRequest {
    @NotBlank private String bookIsbn;
    @NotNull private ReadingStatus status;

    // Book data — auto-upserted when adding to library
    private String title;
    private String author;
    private String description;
    private int pageCount;
    private double avgRating;
    private String thumbnailUrl;
    private Integer coverId;
    private String workKey;
}
