package com.example.book_tracker_api.controller;

import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{followingId}/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<Void> followUser(@AuthenticationPrincipal User currentUser, @PathVariable Long followingId) {
        followService.followUser(currentUser.getId(), followingId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unfollowUser(@AuthenticationPrincipal User currentUser, @PathVariable Long followingId) {
        followService.unfollowUser(currentUser.getId(), followingId);
        return ResponseEntity.ok().build();
    }
}
