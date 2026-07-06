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
@Schema(description = "Ответ с данными зарегистрированного пользователя")
public class UserResponse {
    @Schema(description = "Порядковый номер пользователя (id)")
    private Long id;

    @Schema(description = "Уникальный внешний UUID пользователя")
    private UUID uuid;

    @Schema(description = "Электронная почта")
    private String email;

    @Schema(description = "Роль в системе")
    private String role;
}
