package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.api.AuthenticationApi;
import com.donation.app.infrastructure.web.dto.*;
import com.donation.app.usecase.LoginUserUseCase;
import com.donation.app.usecase.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы для регистрации и входа")
public class AuthController implements AuthenticationApi {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    @Override
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация нового пользователя", description = "Создает пользователя по почте и паролю")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    public Mono<ResponseEntity<UserResponse>> register(@Valid @RequestBody AuthRequest request, final ServerWebExchange exchange) {
        return registerUserUseCase.register(request.getEmail(), request.getPassword())
                .map(user -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(UserResponse.builder()
                                .id(user.getId())
                                .uuid(user.getUuid())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build()));
    }

    @Override
    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя (вход)", description = "Возвращает JWT токен или требует прохождения MFA")
    @ApiResponse(responseCode = "200", description = "Успешный первый шаг авторизации",
            content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "401", description = "Неверная почта или пароль")
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody AuthRequest request, final ServerWebExchange exchange) {
        return loginUserUseCase.login(request.getEmail(), request.getPassword())
                .map(ResponseEntity::ok);
    }

    @Override
    @PostMapping("/login/verify-mfa")
    @Operation(summary = "Второй шаг авторизации: верификация кода 2FA для входа")
    public Mono<ResponseEntity<LoginResponse>> verifyMfaAndLogin(@RequestBody MfaVerificationRequest request, final ServerWebExchange exchange) {
        return loginUserUseCase.verifyMfaAndLogin(request.getEmail(), request.getCode())
                .map(ResponseEntity::ok);
    }
}
