package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UpdateProfileUseCaseTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UpdateProfileUseCase updateProfileUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        updateProfileUseCase = new UpdateProfileUseCase(userRepository, passwordEncoder);
    }

    @Test
    void updateProfile_Success() {
        String currentEmail = "test@example.com";
        User user = User.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .email(currentEmail)
                .password("oldPassword")
                .build();

        when(userRepository.findByEmail(currentEmail)).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(updateProfileUseCase.updateProfile(currentEmail, "NewNick", "avatar", "header", null, null, null))
                .expectNextMatches(updated -> "NewNick".equals(updated.getNickname()) && "avatar".equals(updated.getAvatarUrl()))
                .verifyComplete();
    }

    @Test
    void updateProfile_PasswordTooShort() {
        User user = User.builder().email("test@example.com").build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.just(user));

        StepVerifier.create(updateProfileUseCase.updateProfile("test@example.com", null, null, null, null, "123", null))
                .expectErrorMatches(t -> t instanceof DonationException && "BAD_REQUEST".equals(((DonationException) t).getCode()))
                .verify();
    }
}
