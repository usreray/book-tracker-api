package com.example.book_tracker_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reading_challenges", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReadingChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "\"year\"", nullable = false)
    private int year;

    @Column(nullable = false)
    private int targetBookCount;

    @Builder.Default
    private int currentCount = 0;
}
