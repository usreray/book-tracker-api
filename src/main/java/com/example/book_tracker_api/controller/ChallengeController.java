package com.example.book_tracker_api.controller;

import com.example.book_tracker_api.dto.request.ChallengeRequest;
import com.example.book_tracker_api.dto.response.ChallengeResponse;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/current")
    public ResponseEntity<ChallengeResponse> getCurrentChallenge(@AuthenticationPrincipal User user) {
        return challengeService.getCurrentChallenge(user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<ChallengeResponse> setChallenge(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChallengeRequest request) {
        return ResponseEntity.ok(challengeService.setChallenge(user, request));
    }
}
