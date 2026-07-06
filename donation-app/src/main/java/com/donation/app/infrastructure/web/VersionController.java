package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.api.VersionApi;
import com.donation.app.infrastructure.web.dto.AppVersionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@RestController
@RequestMapping("/api/version")
@RequiredArgsConstructor
@Tag(name = "Версионирование", description = "Получение информации о текущей версии сервиса")
public class VersionController implements VersionApi {

    @Override
    @GetMapping
    @Operation(summary = "Получить информацию о версии сервиса", 
               description = "Возвращает версию в формате: Мажорная.Релизная.Дата.Время.ХэшКомита")
    public Mono<ResponseEntity<AppVersionResponse>> getVersion(final ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            String commitHash = "unknown";
            String buildTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String formattedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"));

            try (InputStream is = getClass().getClassLoader().getResourceAsStream("git.properties")) {
                if (is != null) {
                    Properties prop = new Properties();
                    prop.load(is);
                    commitHash = prop.getProperty("git.commit.id.abbrev", "unknown");
                    String gitTime = prop.getProperty("git.build.time", "");
                    if (!gitTime.isEmpty()) {
                        buildTime = gitTime;
                    }
                }
            } catch (Exception e) {
                // Игнорируем, если файла нет
            }

            String fullVersion = String.format("1.1.%s.%s.%s", formattedDate, formattedTime, commitHash);

            return ResponseEntity.ok(AppVersionResponse.builder()
                    .name("donation-app")
                    .version(fullVersion)
                    .commitHash(commitHash)
                    .buildTime(buildTime)
                    .build());
        });
    }
}
