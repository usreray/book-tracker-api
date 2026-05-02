package com.example.book_tracker_api.service;

import com.example.book_tracker_api.dto.response.ActivityResponse;
import com.example.book_tracker_api.dto.response.PublicUserResponse;
import com.example.book_tracker_api.entity.Activity;
import com.example.book_tracker_api.entity.ActivityType;
import com.example.book_tracker_api.entity.BadgeType;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserService userService;

    @Transactional
    public void logReadingActivity(User user, ActivityType type, String isbn, String title, String thumbnailUrl) {
        Activity activity = Activity.builder()
                .user(user)
                .type(type)
                .bookIsbn(isbn)
                .bookTitle(title)
                .bookThumbnailUrl(thumbnailUrl)
                .build();
        activityRepository.save(activity);
    }

    @Transactional
    public void logReviewActivity(User user, String isbn, String title, String thumbnailUrl, Long reviewId) {
        Activity activity = Activity.builder()
                .user(user)
                .type(ActivityType.WROTE_REVIEW)
                .bookIsbn(isbn)
                .bookTitle(title)
                .bookThumbnailUrl(thumbnailUrl)
                .reviewId(reviewId)
                .build();
        activityRepository.save(activity);
    }

    @Transactional
    public void logBadgeActivity(User user, BadgeType badgeType) {
        Activity activity = Activity.builder()
                .user(user)
                .type(ActivityType.EARNED_BADGE)
                .badgeType(badgeType)
                .build();
        activityRepository.save(activity);
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> getFeedForUser(Long userId) {
        return activityRepository.findFeedForUser(userId).stream()
                .map(activity -> {
                    PublicUserResponse publicUser = userService.getUserPublicProfile(activity.getUser().getId(), userId);
                    return ActivityResponse.from(activity, publicUser);
                })
                .collect(Collectors.toList());
    }
}
