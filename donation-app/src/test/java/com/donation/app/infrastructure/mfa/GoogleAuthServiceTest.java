package com.donation.app.infrastructure.mfa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoogleAuthServiceTest {

    private final GoogleAuthService service = new GoogleAuthService();

    @Test
    void generateSecretKey_ReturnsBase32Secret() {
        String secret = service.generateSecretKey();

        assertNotNull(secret);
        assertFalse(secret.isBlank());
    }

    @Test
    void getOtpAuthUrl_ContainsEmailSecretAndIssuer() {
        String url = service.getOtpAuthURL("user@example.com", "SECRET");

        assertTrue(url.contains("DonationApp:user@example.com"));
        assertTrue(url.contains("secret=SECRET"));
        assertTrue(url.contains("issuer=DonationApp"));
    }

    @Test
    void authorize_BlankSecretReturnsFalse() {
        assertFalse(service.authorize(" ", 123456));
    }

    @Test
    void authorize_RandomCodeReturnsFalse() {
        assertFalse(service.authorize(service.generateSecretKey(), -1));
    }
}
