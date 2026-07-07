package com.donation.app.infrastructure.web;

import com.donation.app.domain.User;
import com.donation.app.infrastructure.web.dto.UpdateProfileRequest;
import com.donation.app.usecase.GetProfileUseCase;
import com.donation.app.usecase.UpdateProfileUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

class ProfileControllerTest {

    private GetProfileUseCase getProfileUseCase;
    private UpdateProfileUseCase updateProfileUseCase;
    private ProfileController controller;

    @BeforeEach
    void setUp() {
        getProfileUseCase = Mockito.mock(GetProfileUseCase.class);
        updateProfileUseCase = Mockito.mock(UpdateProfileUseCase.class);
        controller = new ProfileController(getProfileUseCase, updateProfileUseCase);
    }

    @Test
    void getProfile_UsesAuthenticatedEmail() {
        User user = user();
        when(getProfileUseCase.getProfile("user@example.com")).thenReturn(Mono.just(user));

        StepVerifier.create(controller.getProfile(null).contextWrite(securityContext()))
                .expectNextMatches(response -> response.getStatusCode().is2xxSuccessful()
                        && "user@example.com".equals(response.getBody().getEmail())
                        && user.getUuid().equals(response.getBody().getUuid()))
                .verifyComplete();
    }

    @Test
    void updateProfile_UsesAuthenticatedEmail() {
        User updated = user().toBuilder().nickname("NewNick").build();
        UpdateProfileRequest request = new UpdateProfileRequest().nickname("NewNick");
        when(updateProfileUseCase.updateProfile("user@example.com", "NewNick", null, null, null, null, null))
                .thenReturn(Mono.just(updated));

        StepVerifier.create(controller.updateProfile(Mono.just(request), null).contextWrite(securityContext()))
                .expectNextMatches(ResponseEntity::hasBody)
                .verifyComplete();
    }

    private static User user() {
        return User.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .email("user@example.com")
                .role("ROLE_USER")
                .nickname("Nick")
                .avatarUrl("avatar")
                .headerUrl("header")
                .mfaEnabled(true)
                .mfaType("SMS")
                .phoneNumber("+79990000000")
                .build();
    }

    private static reactor.util.context.Context securityContext() {
        var authentication = new UsernamePasswordAuthenticationToken("user@example.com", null);
        return org.springframework.security.core.context.ReactiveSecurityContextHolder.withSecurityContext(
                Mono.just(new SecurityContextImpl(authentication))
        );
    }
}
