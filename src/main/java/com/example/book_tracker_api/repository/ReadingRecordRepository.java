package com.example.book_tracker_api.repository;

import com.example.book_tracker_api.entity.ReadingRecord;
import com.example.book_tracker_api.entity.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    List<ReadingRecord> findByUserId(Long userId);
    List<ReadingRecord> findByUserIdAndStatus(Long userId, ReadingStatus status);
    Optional<ReadingRecord> findByUserIdAndBookIsbn(Long userId, String bookIsbn);
    long countByUserIdAndStatus(Long userId, ReadingStatus status);
    void deleteByUserIdAndBookIsbn(Long userId, String bookIsbn);
}
