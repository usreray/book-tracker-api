package com.example.book_tracker_api.controller;

import com.example.book_tracker_api.dto.request.LibraryEntryRequest;
import com.example.book_tracker_api.dto.request.UpdateProgressRequest;
import com.example.book_tracker_api.dto.request.UpdateStatusRequest;
import com.example.book_tracker_api.dto.response.LibraryEntryResponse;
import com.example.book_tracker_api.entity.ReadingStatus;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<List<LibraryEntryResponse>> getLibrary(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) ReadingStatus status) {
        if (status != null) {
            return ResponseEntity.ok(libraryService.getUserLibraryByStatus(user.getId(), status));
        }
        return ResponseEntity.ok(libraryService.getUserLibrary(user.getId()));
    }

    @PostMapping
    public ResponseEntity<LibraryEntryResponse> addToLibrary(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody LibraryEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryService.addToLibrary(user, request));
    }

    @PutMapping("/{isbn}/status")
    public ResponseEntity<LibraryEntryResponse> updateStatus(
            @AuthenticationPrincipal User user,
            @PathVariable String isbn,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(libraryService.updateStatus(user, isbn, request));
    }

    @PutMapping("/{isbn}/progress")
    public ResponseEntity<LibraryEntryResponse> updateProgress(
            @AuthenticationPrincipal User user,
            @PathVariable String isbn,
            @Valid @RequestBody UpdateProgressRequest request) {
        return ResponseEntity.ok(libraryService.updateProgress(user, isbn, request));
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> removeFromLibrary(
            @AuthenticationPrincipal User user,
            @PathVariable String isbn) {
        libraryService.removeFromLibrary(user, isbn);
        return ResponseEntity.noContent().build();
    }
}
