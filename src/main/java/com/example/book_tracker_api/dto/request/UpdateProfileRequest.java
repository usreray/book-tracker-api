package com.example.book_tracker_api.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String bio;
}
