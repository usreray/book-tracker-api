package com.example.book_tracker_api.dto.request;

import com.example.book_tracker_api.entity.ReadingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotNull private ReadingStatus status;
}
