package com.example.book_tracker_api.dto.response;

import com.example.book_tracker_api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private String profilePicturePath;
    private List<String> badges;

    public static UserResponse from(User user, List<String> badges) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .bio(user.getBio())
                .profilePicturePath(user.getProfilePicturePath())
                .badges(badges)
                .build();
    }
}
