package com.donation.app.infrastructure.web;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class VersionControllerTest {

    @Test
    void getVersion_ReturnsApplicationVersion() {
        StepVerifier.create(new VersionController().getVersion(null))
                .expectNextMatches(response -> response.getStatusCode().is2xxSuccessful()
                        && "donation-app".equals(response.getBody().getName())
                        && response.getBody().getVersion() != null
                        && response.getBody().getBuildTime() != null)
                .verifyComplete();
    }
}
