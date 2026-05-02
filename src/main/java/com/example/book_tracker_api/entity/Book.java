package com.example.book_tracker_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {

    @Id
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String description = "";

    @Builder.Default
    private int pageCount = 0;

    @Builder.Default
    private double avgRating = 0.0;

    @Builder.Default
    private String thumbnailUrl = "";

    private Integer coverId;

    @Builder.Default
    private String workKey = "";
}
