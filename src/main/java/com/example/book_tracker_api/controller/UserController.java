package com.example.book_tracker_api.controller;

import com.example.book_tracker_api.dto.request.UpdateProfileRequest;
import com.example.book_tracker_api.dto.response.PublicUserResponse;
import com.example.book_tracker_api.dto.response.UserResponse;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }

    @PostMapping("/me/photo")
    public ResponseEntity<UserResponse> uploadProfilePhoto(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.uploadProfilePhoto(user, file));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PublicUserResponse>> searchUsers(@RequestParam("q") String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<PublicUserResponse> getPublicProfile(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        Long requesterId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(userService.getUserPublicProfile(id, requesterId));
    }
}
