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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RegisterUserUseCaseTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RegisterUserUseCase registerUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        registerUserUseCase = new RegisterUserUseCase(userRepository, passwordEncoder);
    }

    @Test
    void register_Success() {
        String email = "new@example.com";
        String password = "rawPassword";

        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .email(email)
                .password("encodedPassword")
                .role("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(registerUserUseCase.register(email, password))
                .expectNextMatches(user -> user.getEmail().equals(email))
                .verifyComplete();
    }

    @Test
    void register_AlreadyExists() {
        String email = "existing@example.com";
        String password = "password";

        User existingUser = User.builder()
                .id(1L)
                .email(email)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(existingUser));

        StepVerifier.create(registerUserUseCase.register(email, password))
                .expectErrorMatches(throwable -> throwable instanceof DonationException &&
                        ((DonationException) throwable).getCode().equals("USER_ALREADY_EXISTS"))
                .verify();
    }

    @Test
    void register_EmptyEmailOrPassword() {
        StepVerifier.create(registerUserUseCase.register("", "password"))
                .expectErrorMatches(throwable -> throwable instanceof DonationException &&
                        ((DonationException) throwable).getCode().equals("BAD_REQUEST"))
                .verify();

        StepVerifier.create(registerUserUseCase.register("email@test.com", ""))
                .expectErrorMatches(throwable -> throwable instanceof DonationException &&
                        ((DonationException) throwable).getCode().equals("BAD_REQUEST"))
                .verify();
    }
}
