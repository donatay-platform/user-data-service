package com.donation.app.infrastructure.web;

import com.donation.app.domain.User;
import com.donation.app.infrastructure.web.api.ProfileApi;
import com.donation.app.infrastructure.web.dto.UpdateProfileRequest;
import com.donation.app.infrastructure.web.dto.UserProfileResponse;
import com.donation.app.usecase.GetProfileUseCase;
import com.donation.app.usecase.UpdateProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Профиль", description = "Управление профилем пользователя")
@SecurityRequirement(name = "BearerAuth")
public class ProfileController implements ProfileApi {

    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    @Override
    @GetMapping
    @Operation(summary = "Получить профиль текущего авторизованного пользователя")
    public Mono<ResponseEntity<UserProfileResponse>> getProfile(final ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(getProfileUseCase::getProfile)
                .map(user -> ResponseEntity.ok(toResponse(user)));
    }

    @Override
    @PutMapping
    @Operation(summary = "Редактировать данные профиля")
    public Mono<ResponseEntity<UserProfileResponse>> updateProfile(
            @RequestBody UpdateProfileRequest request,
            final ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(currentEmail -> updateProfileUseCase.updateProfile(
                        currentEmail,
                        request.getNickname(),
                        request.getAvatarUrl(),
                        request.getHeaderUrl(),
                        request.getEmail(),
                        request.getPassword(),
                        request.getPhoneNumber()
                ))
                .map(user -> ResponseEntity.ok(toResponse(user)));
    }

    private UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .headerUrl(user.getHeaderUrl())
                .role(user.getRole())
                .mfaEnabled(user.isMfaEnabled())
                .mfaType(user.getMfaType())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
