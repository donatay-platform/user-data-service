package com.donation.app.infrastructure.web;

import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import com.donation.app.domain.DonationException;
import com.donation.app.infrastructure.web.dto.UpdateProfileRequest;
import com.donation.app.infrastructure.web.dto.UserProfileResponse;
import com.donation.app.usecase.UpdateProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Профиль", description = "Управление профилем пользователя")
@SecurityRequirement(name = "BearerAuth")
public class ProfileController {

    private final UserRepository userRepository;
    private final UpdateProfileUseCase updateProfileUseCase;

    @GetMapping
    @Operation(summary = "Получить профиль текущего авторизованного пользователя")
    public Mono<UserProfileResponse> getProfile(@AuthenticationPrincipal String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")))
                .map(this::toResponse);
    }

    @PutMapping
    @Operation(summary = "Редактировать данные профиля")
    public Mono<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal String currentEmail,
            @RequestBody UpdateProfileRequest request) {
        return updateProfileUseCase.updateProfile(
                currentEmail,
                request.getNickname(),
                request.getAvatarUrl(),
                request.getHeaderUrl(),
                request.getEmail(),
                request.getPassword(),
                request.getPhoneNumber()
        ).map(this::toResponse);
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
