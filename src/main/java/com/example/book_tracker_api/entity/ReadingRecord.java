package com.example.book_tracker_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "reading_records", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_isbn"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReadingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    @Builder.Default
    private int currentPage = 0;

    @Builder.Default
    private LocalDate dateStarted = LocalDate.now();

    private LocalDate dateFinished;
}
