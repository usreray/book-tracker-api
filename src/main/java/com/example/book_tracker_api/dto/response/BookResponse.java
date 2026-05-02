package com.example.book_tracker_api.dto.response;

import com.example.book_tracker_api.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class BookResponse {
    private String isbn;
    private String title;
    private String author;
    private String description;
    private int pageCount;
    private double avgRating;
    private String thumbnailUrl;
    private Integer coverId;
    private String workKey;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .pageCount(book.getPageCount())
                .avgRating(book.getAvgRating())
                .thumbnailUrl(book.getThumbnailUrl())
                .coverId(book.getCoverId())
                .workKey(book.getWorkKey())
                .build();
    }
}
