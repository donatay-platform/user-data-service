package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.api.AuthenticationApi;
import com.donation.app.infrastructure.web.dto.AuthRequest;
import com.donation.app.infrastructure.web.dto.LoginResponse;
import com.donation.app.infrastructure.web.dto.UserResponse;
import com.donation.app.infrastructure.web.dto.MfaVerificationRequest;
import com.donation.app.usecase.LoginUserUseCase;
import com.donation.app.usecase.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы для регистрации и входа")
public class AuthController implements AuthenticationApi {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    @Override
    public Mono<ResponseEntity<UserResponse>> register(Mono<AuthRequest> authRequest, ServerWebExchange exchange) {
        return authRequest.flatMap(request -> registerUserUseCase.register(request.getEmail(), request.getPassword()))
                .map(user -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new UserResponse()
                                .id(user.getId())
                                .uuid(user.getUuid())
                                .email(user.getEmail())
                                .role(user.getRole())));
    }

    @Override
    public Mono<ResponseEntity<LoginResponse>> login(Mono<AuthRequest> authRequest, ServerWebExchange exchange) {
        return authRequest.flatMap(request -> loginUserUseCase.login(request.getEmail(), request.getPassword()))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<LoginResponse>> verifyMfaAndLogin(Mono<MfaVerificationRequest> mfaVerificationRequest, ServerWebExchange exchange) {
        return mfaVerificationRequest.flatMap(request -> loginUserUseCase.verifyMfaAndLogin(request.getEmail(), request.getCode()))
                .map(ResponseEntity::ok);
    }
}
