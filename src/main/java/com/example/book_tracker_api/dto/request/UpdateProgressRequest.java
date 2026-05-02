package com.example.book_tracker_api.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateProgressRequest {
    @Min(0) private int currentPage;
}
