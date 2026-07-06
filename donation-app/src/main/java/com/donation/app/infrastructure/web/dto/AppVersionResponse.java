package com.donation.app.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о текущей версии приложения")
public class AppVersionResponse {

    @Schema(description = "Название приложения", example = "donation-app")
    private String name;

    @Schema(description = "Версия в формате MAJOR.RELEASE.DATE.TIME.HASH", example = "1.1.20260706.1950.c7bb097")
    private String version;

    @Schema(description = "Хэш последнего коммита", example = "c7bb097")
    private String commitHash;

    @Schema(description = "Дата и время сборки", example = "2026-07-06T19:50:00")
    private String buildTime;
}
