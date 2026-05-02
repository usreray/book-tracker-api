package com.example.book_tracker_api.repository;

import com.example.book_tracker_api.entity.BadgeType;
import com.example.book_tracker_api.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUserId(Long userId);
    boolean existsByUserIdAndBadgeType(Long userId, BadgeType badgeType);
}
