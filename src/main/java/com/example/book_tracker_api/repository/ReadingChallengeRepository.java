package com.example.book_tracker_api.repository;

import com.example.book_tracker_api.entity.ReadingChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReadingChallengeRepository extends JpaRepository<ReadingChallenge, Long> {
    Optional<ReadingChallenge> findByUserIdAndYear(Long userId, int year);
}
