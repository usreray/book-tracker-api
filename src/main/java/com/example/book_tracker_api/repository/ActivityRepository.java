package com.example.book_tracker_api.repository;

import com.example.book_tracker_api.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    // Query to fetch feed: activities from people the user follows
    @Query("SELECT a FROM Activity a WHERE a.user.id IN " +
           "(SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId) " +
           "ORDER BY a.createdAt DESC")
    List<Activity> findFeedForUser(@Param("userId") Long userId);
}
