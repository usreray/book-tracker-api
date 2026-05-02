package com.example.book_tracker_api.service;

import com.example.book_tracker_api.dto.request.LibraryEntryRequest;
import com.example.book_tracker_api.dto.request.UpdateProgressRequest;
import com.example.book_tracker_api.dto.request.UpdateStatusRequest;
import com.example.book_tracker_api.dto.response.LibraryEntryResponse;
import com.example.book_tracker_api.entity.*;
import com.example.book_tracker_api.exception.DuplicateResourceException;
import com.example.book_tracker_api.exception.ResourceNotFoundException;
import com.example.book_tracker_api.repository.BookRepository;
import com.example.book_tracker_api.repository.ReadingRecordRepository;
import com.example.book_tracker_api.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LibraryService {

    private final ReadingRecordRepository recordRepository;
    private final BookRepository bookRepository;
    private final ActivityService activityService;
    private final UserBadgeRepository userBadgeRepository;

    public List<LibraryEntryResponse> getUserLibrary(Long userId) {
        return recordRepository.findByUserId(userId).stream()
                .map(LibraryEntryResponse::from)
                .toList();
    }

    public List<LibraryEntryResponse> getUserLibraryByStatus(Long userId, ReadingStatus status) {
        return recordRepository.findByUserIdAndStatus(userId, status).stream()
                .map(LibraryEntryResponse::from)
                .toList();
    }

    @Transactional
    public LibraryEntryResponse addToLibrary(User user, LibraryEntryRequest request) {
        // Check for duplicate
        recordRepository.findByUserIdAndBookIsbn(user.getId(), request.getBookIsbn())
                .ifPresent(r -> { throw new DuplicateResourceException("Book already in library"); });

        // Upsert book
        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseGet(() -> bookRepository.save(Book.builder()
                        .isbn(request.getBookIsbn())
                        .title(request.getTitle() != null ? request.getTitle() : "Unknown")
                        .author(request.getAuthor() != null ? request.getAuthor() : "Unknown")
                        .description(request.getDescription() != null ? request.getDescription() : "")
                        .pageCount(request.getPageCount())
                        .avgRating(request.getAvgRating())
                        .thumbnailUrl(request.getThumbnailUrl() != null ? request.getThumbnailUrl() : "")
                        .coverId(request.getCoverId())
                        .workKey(request.getWorkKey() != null ? request.getWorkKey() : "")
                        .build()));

        ReadingRecord record = ReadingRecord.builder()
                .user(user)
                .book(book)
                .status(request.getStatus())
                .dateStarted(LocalDate.now())
                .build();

        if (request.getStatus() == ReadingStatus.READ) {
            record.setDateFinished(LocalDate.now());
        }

        record = recordRepository.save(record);
        
        activityService.logReadingActivity(user, ActivityType.ADDED_TO_LIBRARY, book.getIsbn(), book.getTitle(), book.getThumbnailUrl());
        
        return LibraryEntryResponse.from(record);
    }

    @Transactional
    public LibraryEntryResponse updateStatus(User user, String isbn, UpdateStatusRequest request) {
        ReadingRecord record = recordRepository.findByUserIdAndBookIsbn(user.getId(), isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not in library"));

        ReadingStatus oldStatus = record.getStatus();
        record.setStatus(request.getStatus());
        
        if (request.getStatus() == ReadingStatus.READ && record.getDateFinished() == null) {
            record.setDateFinished(LocalDate.now());
        }
        if (request.getStatus() != ReadingStatus.READ) {
            record.setDateFinished(null);
        }

        record = recordRepository.save(record);

        if (oldStatus != request.getStatus()) {
            if (request.getStatus() == ReadingStatus.CURRENTLY_READING) {
                activityService.logReadingActivity(user, ActivityType.STARTED_READING, record.getBook().getIsbn(), record.getBook().getTitle(), record.getBook().getThumbnailUrl());
            } else if (request.getStatus() == ReadingStatus.READ) {
                activityService.logReadingActivity(user, ActivityType.FINISHED_READING, record.getBook().getIsbn(), record.getBook().getTitle(), record.getBook().getThumbnailUrl());
                checkAndAwardReadingBadges(user);
            }
        }

        return LibraryEntryResponse.from(record);
    }

    @Transactional
    public LibraryEntryResponse updateProgress(User user, String isbn, UpdateProgressRequest request) {
        ReadingRecord record = recordRepository.findByUserIdAndBookIsbn(user.getId(), isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not in library"));

        record.setCurrentPage(request.getCurrentPage());
        record = recordRepository.save(record);
        return LibraryEntryResponse.from(record);
    }

    @Transactional
    public void removeFromLibrary(User user, String isbn) {
        recordRepository.findByUserIdAndBookIsbn(user.getId(), isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not in library"));
        recordRepository.deleteByUserIdAndBookIsbn(user.getId(), isbn);
    }

    public long countByStatus(Long userId, ReadingStatus status) {
        return recordRepository.countByUserIdAndStatus(userId, status);
    }

    private void checkAndAwardReadingBadges(User user) {
        long readCount = recordRepository.countByUserIdAndStatus(user.getId(), ReadingStatus.READ);
        
        awardBadgeIfEligible(user, BadgeType.FIRST_BOOK, readCount >= 1);
        awardBadgeIfEligible(user, BadgeType.BOOKWORM, readCount >= 10);
        awardBadgeIfEligible(user, BadgeType.BIBLIOPHILE, readCount >= 50);
        awardBadgeIfEligible(user, BadgeType.SCHOLAR, readCount >= 100);
    }

    private void awardBadgeIfEligible(User user, BadgeType badgeType, boolean conditionMet) {
        if (conditionMet && !userBadgeRepository.existsByUserIdAndBadgeType(user.getId(), badgeType)) {
            UserBadge badge = UserBadge.builder().user(user).badgeType(badgeType).build();
            userBadgeRepository.save(badge);
            activityService.logBadgeActivity(user, badgeType);
        }
    }
}
