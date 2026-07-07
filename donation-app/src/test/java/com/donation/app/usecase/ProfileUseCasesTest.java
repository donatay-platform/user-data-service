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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProfileUseCasesTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
    }

    @Test
    void getProfile_Success() {
        User user = User.builder().email("user@example.com").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));

        StepVerifier.create(new GetProfileUseCase(userRepository).getProfile("user@example.com"))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void getProfile_UserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(new GetProfileUseCase(userRepository).getProfile("missing@example.com"))
                .expectErrorMatches(error -> error instanceof DonationException && "USER_NOT_FOUND".equals(((DonationException) error).getCode()))
                .verify();
    }

    @Test
    void updateProfile_ChangesEmailAndPassword() {
        User user = User.builder().email("old@example.com").password("old").build();
        when(userRepository.findByEmail("old@example.com")).thenReturn(Mono.just(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Mono.empty());
        when(passwordEncoder.encode("new-password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(new UpdateProfileUseCase(userRepository, passwordEncoder)
                        .updateProfile("old@example.com", null, null, null, "new@example.com", "new-password", "+79990000000"))
                .expectNextMatches(updated -> "new@example.com".equals(updated.getEmail())
                        && "encoded".equals(updated.getPassword())
                        && "+79990000000".equals(updated.getPhoneNumber()))
                .verifyComplete();
    }

    @Test
    void updateProfile_EmailTaken() {
        User user = User.builder().email("old@example.com").build();
        when(userRepository.findByEmail("old@example.com")).thenReturn(Mono.just(user));
        when(userRepository.findByEmail("busy@example.com")).thenReturn(Mono.just(User.builder().email("busy@example.com").build()));

        StepVerifier.create(new UpdateProfileUseCase(userRepository, passwordEncoder)
                        .updateProfile("old@example.com", null, null, null, "busy@example.com", null, null))
                .expectErrorMatches(error -> error instanceof DonationException && "EMAIL_TAKEN".equals(((DonationException) error).getCode()))
                .verify();
    }

    @Test
    void updateProfile_UserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(new UpdateProfileUseCase(userRepository, passwordEncoder)
                        .updateProfile("missing@example.com", null, null, null, null, null, null))
                .expectErrorMatches(error -> error instanceof DonationException && "USER_NOT_FOUND".equals(((DonationException) error).getCode()))
                .verify();
    }
}
