package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateUserProfileUseCase {

    private static final String TECHNICAL_PASSWORD_PLACEHOLDER = "managed-by-auth-service";
    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final UserRepository userRepository;

    public Mono<User> createProfile(UUID userUuid, String email) {
        if (userUuid == null || email == null || email.isBlank()) {
            return Mono.error(new DonationException("BAD_REQUEST", "User UUID and email cannot be empty"));
        }

        return userRepository.findByUuid(userUuid)
                .switchIfEmpty(Mono.defer(() -> userRepository.save(User.builder()
                        .uuid(userUuid)
                        .email(email)
                        .password(TECHNICAL_PASSWORD_PLACEHOLDER)
                        .role(DEFAULT_ROLE)
                        .createdAt(LocalDateTime.now())
                        .build())));
    }
}
