package com.donation.app.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationProfileConfigurationTest {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private static final List<String> FORBIDDEN_SECRET_FRAGMENTS = List.of(
            "mySecure",
            "SuperSecret"
    );

    @Test
    void commonConfigurationDoesNotContainSecrets() throws IOException {
        String content = readResource("application.yml");

        assertNoForbiddenSecrets(content);
        assertTrue(content.contains("enabled: false"));
    }

    @Test
    void localConfigurationContainsOnlyLocalDevelopmentDefaults() throws IOException {
        String content = readResource("application-local.yml");

        assertNoForbiddenSecrets(content);
        assertTrue(content.contains("local_dev_password_change_me"));
        assertTrue(content.contains("local-development-jwt-secret"));
    }

    @Test
    void productionConfigurationRequiresExternalSecretsWithoutDefaults() throws IOException {
        String content = readResource("application-prod.yml");

        assertNoForbiddenSecrets(content);
        assertTrue(content.contains("${JWT_SECRET}"));
        assertTrue(content.contains("${SPRING_LIQUIBASE_PASSWORD}"));
        assertTrue(content.contains("${SPRING_R2DBC_PASSWORD}"));

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(content);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            if (placeholder.endsWith("_PASSWORD") || placeholder.equals("JWT_SECRET")) {
                assertFalse(placeholder.contains(":"), "Production secret placeholder must not have default: " + placeholder);
            }
        }
    }

    private static String readResource(String fileName) throws IOException {
        return Files.readString(Path.of("src/main/resources", fileName));
    }

    private static void assertNoForbiddenSecrets(String content) {
        FORBIDDEN_SECRET_FRAGMENTS.forEach(secret -> assertFalse(content.contains(secret)));
    }
}
