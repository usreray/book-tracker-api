package com.example.book_tracker_api.dto.response;

import com.example.book_tracker_api.entity.ReadingRecord;
import com.example.book_tracker_api.entity.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data @Builder @AllArgsConstructor
public class LibraryEntryResponse {
    private Long id;
    private BookResponse book;
    private ReadingStatus status;
    private int currentPage;
    private LocalDate dateStarted;
    private LocalDate dateFinished;

    public static LibraryEntryResponse from(ReadingRecord record) {
        return LibraryEntryResponse.builder()
                .id(record.getId())
                .book(BookResponse.from(record.getBook()))
                .status(record.getStatus())
                .currentPage(record.getCurrentPage())
                .dateStarted(record.getDateStarted())
                .dateFinished(record.getDateFinished())
                .build();
    }
}
