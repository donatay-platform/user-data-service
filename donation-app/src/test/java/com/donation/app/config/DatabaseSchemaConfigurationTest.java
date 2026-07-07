package com.donation.app.config;

import com.donation.app.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.relational.core.mapping.Table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseSchemaConfigurationTest {

    @Test
    void userEntityUsesDedicatedUserDataSchema() {
        Table table = UserEntity.class.getAnnotation(Table.class);

        assertEquals("user_data_service", table.schema());
        assertEquals("users", table.name());
    }

    @Test
    void liquibaseMovesUsersObjectsToDedicatedSchema() throws IOException {
        String changelog = Files.readString(Path.of("src/main/resources/db/changelog/db.changelog-master.yaml"));

        assertTrue(changelog.contains("CREATE SCHEMA IF NOT EXISTS user_data_service"));
        assertTrue(changelog.contains("ALTER TABLE IF EXISTS public.users SET SCHEMA user_data_service"));
        assertTrue(changelog.contains("user_data_service.users_id_seq"));
    }
}
