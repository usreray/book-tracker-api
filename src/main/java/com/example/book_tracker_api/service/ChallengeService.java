package com.example.book_tracker_api.service;

import com.example.book_tracker_api.dto.request.ChallengeRequest;
import com.example.book_tracker_api.dto.response.ChallengeResponse;
import com.example.book_tracker_api.entity.ReadingChallenge;
import com.example.book_tracker_api.entity.ReadingStatus;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.repository.ReadingChallengeRepository;
import com.example.book_tracker_api.repository.ReadingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ReadingChallengeRepository challengeRepository;
    private final ReadingRecordRepository recordRepository;

    public Optional<ChallengeResponse> getCurrentChallenge(User user) {
        int year = Year.now().getValue();
        return challengeRepository.findByUserIdAndYear(user.getId(), year)
                .map(this::syncAndRespond);
    }

    public ChallengeResponse setChallenge(User user, ChallengeRequest request) {
        int year = Year.now().getValue();

        ReadingChallenge challenge = challengeRepository.findByUserIdAndYear(user.getId(), year)
                .orElse(ReadingChallenge.builder()
                        .user(user)
                        .year(year)
                        .build());

        challenge.setTargetBookCount(request.getTargetBookCount());

        // Sync current count with actual read books
        long readCount = recordRepository.countByUserIdAndStatus(user.getId(), ReadingStatus.READ);
        challenge.setCurrentCount((int) readCount);

        challenge = challengeRepository.save(challenge);
        return ChallengeResponse.from(challenge);
    }

    private ChallengeResponse syncAndRespond(ReadingChallenge challenge) {
        long readCount = recordRepository.countByUserIdAndStatus(challenge.getUser().getId(), ReadingStatus.READ);
        if (challenge.getCurrentCount() != (int) readCount) {
            challenge.setCurrentCount((int) readCount);
            challenge = challengeRepository.save(challenge);
        }
        return ChallengeResponse.from(challenge);
    }
}
