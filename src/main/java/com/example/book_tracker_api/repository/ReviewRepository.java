package com.example.book_tracker_api.repository;

import com.example.book_tracker_api.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);
    List<Review> findByBookIsbn(String bookIsbn);
    Optional<Review> findByUserIdAndBookIsbn(Long userId, String bookIsbn);
    long countByUserId(Long userId);
}
