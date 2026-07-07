package com.donation.app.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider("test-jwt-secret-at-least-32-characters-long", 3600);
    }

    @Test
    void generateAndValidateToken_Success() {
        String email = "test@example.com";
        String role = "ROLE_USER";

        String token = jwtProvider.generateToken(email, role);
        assertNotNull(token);

        assertTrue(jwtProvider.validateToken(token));

        Claims claims = jwtProvider.getClaims(token);
        assertEquals(email, claims.getSubject());
        assertEquals(role, claims.get("role"));
    }

    @Test
    void validateToken_Invalid() {
        assertFalse(jwtProvider.validateToken("invalidTokenStructure"));
    }
}
