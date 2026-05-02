package com.example.book_tracker_api.service;

import com.example.book_tracker_api.dto.request.UpdateProfileRequest;
import com.example.book_tracker_api.dto.response.PublicUserResponse;
import com.example.book_tracker_api.dto.response.UserResponse;
import com.example.book_tracker_api.entity.ReadingStatus;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.exception.ResourceNotFoundException;
import com.example.book_tracker_api.repository.ReadingRecordRepository;
import com.example.book_tracker_api.repository.ReviewRepository;
import com.example.book_tracker_api.repository.UserRepository;
import com.example.book_tracker_api.repository.UserBadgeRepository;
import com.example.book_tracker_api.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final ReviewRepository reviewRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final FollowRepository followRepository;

    private List<String> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId).stream()
                .map(badge -> badge.getBadgeType().name())
                .collect(Collectors.toList());
    }

    public List<PublicUserResponse> searchUsers(String query) {
        return userRepository.findAll().stream()
                .filter(u -> u.getName().toLowerCase().contains(query.toLowerCase()) || 
                             u.getEmail().toLowerCase().contains(query.toLowerCase()))
                .map(u -> getUserPublicProfile(u.getId()))
                .collect(Collectors.toList());
    }

    public UserResponse getProfile(User user) {
        return UserResponse.from(user, getUserBadges(user.getId()));
    }

    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        user = userRepository.save(user);
        return UserResponse.from(user, getUserBadges(user.getId()));
    }

    public PublicUserResponse getUserPublicProfile(Long userId) {
        return getUserPublicProfile(userId, null);
    }

    public PublicUserResponse getUserPublicProfile(Long userId, Long requesterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        long booksRead = readingRecordRepository.countByUserIdAndStatus(userId, ReadingStatus.READ);
        long reviewCount = reviewRepository.countByUserId(userId);
        long followersCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        boolean isFollowing = requesterId != null && followRepository.existsByFollowerIdAndFollowingId(requesterId, userId);
        return PublicUserResponse.from(user, booksRead, reviewCount, followersCount, followingCount, isFollowing, getUserBadges(userId));
    }

    public UserResponse uploadProfilePhoto(User user, MultipartFile file) throws IOException {
        String uploadDir = "uploads/profile_photos/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";

        String fileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);

        String fileUrl = "/uploads/profile_photos/" + fileName;
        user.setProfilePicturePath(fileUrl);
        user = userRepository.save(user);

        return UserResponse.from(user, getUserBadges(user.getId()));
    }
}
