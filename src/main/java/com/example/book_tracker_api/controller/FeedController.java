package com.example.book_tracker_api.controller;

import com.example.book_tracker_api.dto.response.ActivityResponse;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getFeed(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(activityService.getFeedForUser(currentUser.getId()));
    }
}
