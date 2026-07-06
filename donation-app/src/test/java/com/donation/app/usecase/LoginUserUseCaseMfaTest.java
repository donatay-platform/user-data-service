package com.donation.app.usecase;

import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import com.donation.app.infrastructure.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class LoginUserUseCaseMfaTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private LoginUserUseCase loginUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtProvider = Mockito.mock(JwtProvider.class);
        loginUserUseCase = new LoginUserUseCase(userRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void login_MfaRequired() {
        String email = "mfa@example.com";
        String password = "rawPassword";

        User user = User.builder()
                .email(email)
                .password("encodedPassword")
                .role("ROLE_USER")
                .mfaEnabled(true)
                .mfaType("SMS")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        StepVerifier.create(loginUserUseCase.login(email, password))
                .expectNextMatches(result -> result.getMfaRequired() && "SMS".equals(result.getMfaType()))
                .verifyComplete();
    }
}
