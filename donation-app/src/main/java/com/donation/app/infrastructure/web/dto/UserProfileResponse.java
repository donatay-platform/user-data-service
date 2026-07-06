package com.donation.app.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Полный профиль пользователя")
public class UserProfileResponse {
    @Schema(description = "Порядковый номер")
    private Long id;

    @Schema(description = "Уникальный внешний UUID")
    private UUID uuid;

    @Schema(description = "Электронная почта")
    private String email;

    @Schema(description = "Никнейм")
    private String nickname;

    @Schema(description = "Ссылка на аватар")
    private String avatarUrl;

    @Schema(description = "Ссылка на баннер профиля")
    private String headerUrl;

    @Schema(description = "Роль")
    private String role;

    @Schema(description = "Включена ли двухфакторная аутентификация")
    private boolean mfaEnabled;

    @Schema(description = "Тип двухфакторной аутентификации (GOOGLE / SMS)")
    private String mfaType;

    @Schema(description = "Номер телефона")
    private String phoneNumber;
}
