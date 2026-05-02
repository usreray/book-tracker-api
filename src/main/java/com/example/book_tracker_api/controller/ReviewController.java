package com.example.book_tracker_api.controller;

import com.example.book_tracker_api.dto.request.ReviewRequest;
import com.example.book_tracker_api.dto.response.ReviewResponse;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewService.getUserReviews(user.getId()));
    }

    @GetMapping("/book/{isbn}")
    public ResponseEntity<List<ReviewResponse>> getBookReviews(@PathVariable String isbn) {
        return ResponseEntity.ok(reviewService.getBookReviews(isbn));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.submitReview(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(user, id, request));
    }
}
