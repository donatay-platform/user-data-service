package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.api.AuthenticationApi;
import com.donation.app.infrastructure.web.dto.AuthRequest;
import com.donation.app.infrastructure.web.dto.LoginResponse;
import com.donation.app.infrastructure.web.dto.UserResponse;
import com.donation.app.infrastructure.web.dto.MfaVerificationRequest;
import com.donation.app.usecase.LoginUserUseCase;
import com.donation.app.usecase.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private LoginUserUseCase loginUserUseCase;

    @Test
    @WithMockUser
    void login_Success() {
        AuthRequest request = new AuthRequest()
                .email("test@example.com")
                .password("password123");

        LoginResponse loginResponse = new LoginResponse()
                .mfaRequired(false)
                .token("mockToken");

        when(loginUserUseCase.login("test@example.com", "password123"))
                .thenReturn(Mono.just(loginResponse));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("mockToken");
    }
}
