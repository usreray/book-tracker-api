package com.example.book_tracker_api.service;

import com.example.book_tracker_api.dto.request.ReviewRequest;
import com.example.book_tracker_api.dto.response.ReviewResponse;
import com.example.book_tracker_api.entity.ActivityType;
import com.example.book_tracker_api.entity.BadgeType;
import com.example.book_tracker_api.entity.Book;
import com.example.book_tracker_api.entity.Review;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.entity.UserBadge;
import com.example.book_tracker_api.exception.ResourceNotFoundException;
import com.example.book_tracker_api.repository.BookRepository;
import com.example.book_tracker_api.repository.ReviewRepository;
import com.example.book_tracker_api.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final ActivityService activityService;
    private final UserBadgeRepository userBadgeRepository;

    public List<ReviewResponse> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public List<ReviewResponse> getBookReviews(String bookIsbn) {
        return reviewRepository.findByBookIsbn(bookIsbn).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public ReviewResponse submitReview(User user, ReviewRequest request) {
        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseGet(() -> bookRepository.save(Book.builder()
                        .isbn(request.getBookIsbn())
                        .title(request.getTitle() != null ? request.getTitle() : "Unknown")
                        .author(request.getAuthor() != null ? request.getAuthor() : "Unknown")
                        .description(request.getDescription() != null ? request.getDescription() : "")
                        .pageCount(request.getPageCount())
                        .avgRating(request.getAvgRating())
                        .thumbnailUrl(request.getThumbnailUrl() != null ? request.getThumbnailUrl() : "")
                        .coverId(request.getCoverId())
                        .workKey(request.getWorkKey() != null ? request.getWorkKey() : "")
                        .build()));

        boolean isNew = reviewRepository.findByUserIdAndBookIsbn(user.getId(), request.getBookIsbn()).isEmpty();
        
        Review review = reviewRepository.findByUserIdAndBookIsbn(user.getId(), request.getBookIsbn())
                .orElse(Review.builder()
                        .user(user)
                        .bookIsbn(request.getBookIsbn())
                        .build());

        review.setRating(request.getRating());
        review.setComment(request.getComment() != null ? request.getComment() : "");

        review = reviewRepository.save(review);
        updateBookAverageRating(book);
        
        if (isNew) {
            activityService.logReviewActivity(user, book.getIsbn(), book.getTitle(), book.getThumbnailUrl(), review.getId());
            checkAndAwardReviewBadges(user);
        }

        return ReviewResponse.from(review);
    }

    public ReviewResponse updateReview(User user, Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cannot edit another user's review");
        }

        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        review.setRating(request.getRating());
        review.setComment(request.getComment() != null ? request.getComment() : "");

        review = reviewRepository.save(review);
        updateBookAverageRating(book);

        return ReviewResponse.from(review);
    }

    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cannot delete another user's review");
        }

        Book book = bookRepository.findById(review.getBookIsbn())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        reviewRepository.delete(review);
        updateBookAverageRating(book);
    }

    public long countUserReviews(Long userId) {
        return reviewRepository.countByUserId(userId);
    }

    private void updateBookAverageRating(Book book) {
        List<Review> reviews = reviewRepository.findByBookIsbn(book.getIsbn());
        if (reviews.isEmpty()) {
            book.setAvgRating(0.0);
        } else {
            double sum = reviews.stream().mapToDouble(Review::getRating).sum();
            book.setAvgRating(Math.round((sum / reviews.size()) * 10.0) / 10.0);
        }
        bookRepository.save(book);
    }

    private void checkAndAwardReviewBadges(User user) {
        long reviewCount = reviewRepository.countByUserId(user.getId());
        
        awardBadgeIfEligible(user, BadgeType.FIRST_REVIEW, reviewCount >= 1);
        awardBadgeIfEligible(user, BadgeType.CRITIC, reviewCount >= 10);
    }

    private void awardBadgeIfEligible(User user, BadgeType badgeType, boolean conditionMet) {
        if (conditionMet && !userBadgeRepository.existsByUserIdAndBadgeType(user.getId(), badgeType)) {
            UserBadge badge = UserBadge.builder().user(user).badgeType(badgeType).build();
            userBadgeRepository.save(badge);
            activityService.logBadgeActivity(user, badgeType);
        }
    }
}
