package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.dto.MfaVerificationRequest;
import com.donation.app.usecase.SendSmsCodeUseCase;
import com.donation.app.usecase.SetupGoogleMfaUseCase;
import com.donation.app.usecase.SetupSmsMfaUseCase;
import com.donation.app.usecase.VerifyAndEnableMfaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class MfaControllerTest {

    private SetupGoogleMfaUseCase setupGoogleMfaUseCase;
    private SetupSmsMfaUseCase setupSmsMfaUseCase;
    private SendSmsCodeUseCase sendSmsCodeUseCase;
    private VerifyAndEnableMfaUseCase verifyAndEnableMfaUseCase;
    private MfaController controller;

    @BeforeEach
    void setUp() {
        setupGoogleMfaUseCase = Mockito.mock(SetupGoogleMfaUseCase.class);
        setupSmsMfaUseCase = Mockito.mock(SetupSmsMfaUseCase.class);
        sendSmsCodeUseCase = Mockito.mock(SendSmsCodeUseCase.class);
        verifyAndEnableMfaUseCase = Mockito.mock(VerifyAndEnableMfaUseCase.class);
        controller = new MfaController(setupGoogleMfaUseCase, setupSmsMfaUseCase, sendSmsCodeUseCase, verifyAndEnableMfaUseCase);
    }

    @Test
    void setupGoogleMfa_ReturnsSecretAndQrUrl() {
        when(setupGoogleMfaUseCase.setupGoogleMfa("user@example.com")).thenReturn(Mono.just("SECRET"));

        StepVerifier.create(controller.setupGoogleMfa(null).contextWrite(securityContext()))
                .expectNextMatches(response -> response.getStatusCode().is2xxSuccessful()
                        && "SECRET".equals(response.getBody().getSecret())
                        && response.getBody().getQrCodeUrl().contains("user@example.com"))
                .verifyComplete();
    }

    @Test
    void sendSmsCode_ReturnsCodeFromUseCase() {
        when(sendSmsCodeUseCase.sendSmsCode("user@example.com")).thenReturn(Mono.just("123456"));

        StepVerifier.create(controller.sendSmsCode(null).contextWrite(securityContext()))
                .expectNextMatches(response -> "123456".equals(response.getBody()))
                .verifyComplete();
    }

    @Test
    void verifyAndEnableMfa_ReturnsVerificationResult() {
        when(verifyAndEnableMfaUseCase.verifyAndEnableMfa("user@example.com", "123456")).thenReturn(Mono.just(true));

        StepVerifier.create(controller.verifyAndEnableMfa(Mono.just(new MfaVerificationRequest().code("123456")), null).contextWrite(securityContext()))
                .expectNextMatches(response -> Boolean.TRUE.equals(response.getBody()))
                .verifyComplete();
    }

    @Test
    void setupSmsMfa_CompletesEmptyBecauseUseCaseReturnsMonoVoid() {
        when(setupSmsMfaUseCase.enableSmsMfa("user@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(controller.setupSmsMfa(null).contextWrite(securityContext()))
                .verifyComplete();
    }

    private static reactor.util.context.Context securityContext() {
        var authentication = new UsernamePasswordAuthenticationToken("user@example.com", null);
        return org.springframework.security.core.context.ReactiveSecurityContextHolder.withSecurityContext(
                Mono.just(new SecurityContextImpl(authentication))
        );
    }
}
