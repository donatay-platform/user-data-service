package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateUserProfileUseCaseTest {

    private UserRepository userRepository;
    private CreateUserProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        useCase = new CreateUserProfileUseCase(userRepository);
    }

    @Test
    void createProfile_Success() {
        UUID uuid = UUID.randomUUID();
        when(userRepository.findByUuid(uuid)).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.createProfile(uuid, "user@example.com"))
                .expectNextMatches(user -> uuid.equals(user.getUuid())
                        && "user@example.com".equals(user.getEmail())
                        && "ROLE_USER".equals(user.getRole())
                        && user.getCreatedAt() != null)
                .verifyComplete();
    }

    @Test
    void createProfile_RejectsEmptyInput() {
        StepVerifier.create(useCase.createProfile(null, "user@example.com"))
                .expectErrorMatches(error -> error instanceof DonationException && "BAD_REQUEST".equals(((DonationException) error).getCode()))
                .verify();
    }

    @Test
    void createProfile_IsIdempotentWhenProfileAlreadyExists() {
        UUID uuid = UUID.randomUUID();
        User existing = User.builder()
                .uuid(uuid)
                .email("user@example.com")
                .nickname("ExistingNick")
                .build();
        when(userRepository.findByUuid(uuid)).thenReturn(Mono.just(existing));

        StepVerifier.create(useCase.createProfile(uuid, "user@example.com"))
                .expectNext(existing)
                .verifyComplete();

        verify(userRepository, never()).save(any(User.class));
    }
}
