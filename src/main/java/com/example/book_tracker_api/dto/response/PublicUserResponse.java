package com.example.book_tracker_api.dto.response;

import com.example.book_tracker_api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class PublicUserResponse {
    private Long id;
    private String name;
    private String bio;
    private String profilePicturePath;
    private long booksRead;
    private long reviewCount;
    private long followersCount;
    private long followingCount;
    private boolean following; // true if the requester follows this user (named 'following' not 'isFollowing' to avoid Lombok/Jackson stripping the 'is' prefix)
    private java.util.List<String> badges;

    public static PublicUserResponse from(User user, long booksRead, long reviewCount, long followersCount, long followingCount, boolean following, java.util.List<String> badges) {
        return PublicUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .bio(user.getBio())
                .profilePicturePath(user.getProfilePicturePath())
                .booksRead(booksRead)
                .reviewCount(reviewCount)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .following(following)
                .badges(badges)
                .build();
    }
}
