package com.example.book_tracker_api.service;

import com.example.book_tracker_api.dto.request.LoginRequest;
import com.example.book_tracker_api.dto.request.RegisterRequest;
import com.example.book_tracker_api.dto.response.AuthResponse;
import com.example.book_tracker_api.dto.response.UserResponse;
import com.example.book_tracker_api.entity.User;
import com.example.book_tracker_api.exception.DuplicateResourceException;
import com.example.book_tracker_api.repository.UserRepository;
import com.example.book_tracker_api.repository.UserBadgeRepository;
import com.example.book_tracker_api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.from(user, Collections.emptyList()))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        List<String> badges = userBadgeRepository.findByUserId(user.getId()).stream()
                .map(badge -> badge.getBadgeType().name())
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.from(user, badges))
                .build();
    }
}
