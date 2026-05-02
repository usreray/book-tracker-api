package com.example.book_tracker_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "activities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    // Optional fields depending on activity type
    private String bookIsbn;
    private String bookTitle;       // Cached for easy display in feed
    private String bookThumbnailUrl;// Cached for easy display in feed
    
    private Long reviewId;
    
    @Enumerated(EnumType.STRING)
    private BadgeType badgeType;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
