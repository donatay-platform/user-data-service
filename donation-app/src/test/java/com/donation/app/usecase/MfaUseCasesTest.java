package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import com.donation.app.infrastructure.mfa.GoogleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MfaUseCasesTest {

    private UserRepository userRepository;
    private GoogleAuthService googleAuthService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        googleAuthService = Mockito.mock(GoogleAuthService.class);
    }

    @Test
    void setupGoogleMfa_SavesGeneratedSecret() {
        User user = User.builder().email("user@example.com").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));
        when(googleAuthService.generateSecretKey()).thenReturn("SECRET");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(new SetupGoogleMfaUseCase(userRepository, googleAuthService).setupGoogleMfa("user@example.com"))
                .expectNext("SECRET")
                .verifyComplete();

        verify(userRepository).save(Mockito.argThat(saved -> "GOOGLE".equals(saved.getMfaType()) && "SECRET".equals(saved.getMfaSecret())));
    }

    @Test
    void setupGoogleMfa_UserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(new SetupGoogleMfaUseCase(userRepository, googleAuthService).setupGoogleMfa("missing@example.com"))
                .expectErrorMatches(error -> error instanceof DonationException && "USER_NOT_FOUND".equals(((DonationException) error).getCode()))
                .verify();
    }

    @Test
    void setupSmsMfa_Success() {
        User user = User.builder().email("user@example.com").phoneNumber("+79990000000").mfaEnabled(true).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(new SetupSmsMfaUseCase(userRepository).enableSmsMfa("user@example.com"))
                .verifyComplete();

        verify(userRepository).save(Mockito.argThat(saved -> "SMS".equals(saved.getMfaType()) && !saved.isMfaEnabled()));
    }

    @Test
    void setupSmsMfa_RequiresPhoneNumber() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(User.builder().email("user@example.com").build()));

        StepVerifier.create(new SetupSmsMfaUseCase(userRepository).enableSmsMfa("user@example.com"))
                .expectErrorMatches(error -> error instanceof DonationException && "BAD_REQUEST".equals(((DonationException) error).getCode()))
                .verify();
    }

    @Test
    void sendSmsCode_Success() {
        User user = User.builder().email("user@example.com").mfaType("SMS").phoneNumber("+79990000000").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(new SendSmsCodeUseCase(userRepository).sendSmsCode("user@example.com"))
                .expectNextMatches(code -> code.matches("\\d{6}"))
                .verifyComplete();
    }

    @Test
    void sendSmsCode_RejectsWhenSmsMfaIsNotSelected() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(User.builder().email("user@example.com").mfaType("GOOGLE").build()));

        StepVerifier.create(new SendSmsCodeUseCase(userRepository).sendSmsCode("user@example.com"))
                .expectErrorMatches(error -> error instanceof DonationException && "BAD_REQUEST".equals(((DonationException) error).getCode()))
                .verify();
    }

    @Test
    void verifyAndEnableMfa_SmsSuccess() {
        User user = User.builder().email("user@example.com").mfaType("SMS").mfaSecret("123456").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(new VerifyAndEnableMfaUseCase(userRepository, googleAuthService).verifyAndEnableMfa("user@example.com", "123456"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void verifyAndEnableMfa_GoogleInvalidCodeFormat() {
        User user = User.builder().email("user@example.com").mfaType("GOOGLE").mfaSecret("SECRET").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));

        StepVerifier.create(new VerifyAndEnableMfaUseCase(userRepository, googleAuthService).verifyAndEnableMfa("user@example.com", "bad"))
                .expectNext(false)
                .verifyComplete();
    }
}
