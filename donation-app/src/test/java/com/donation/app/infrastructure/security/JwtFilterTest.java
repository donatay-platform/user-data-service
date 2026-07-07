package com.donation.app.infrastructure.security;

import com.donation.app.infrastructure.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtFilterTest {

    private JwtProvider jwtProvider;
    private JwtFilter filter;

    @BeforeEach
    void setUp() {
        jwtProvider = Mockito.mock(JwtProvider.class);
        filter = new JwtFilter(jwtProvider);
    }

    @Test
    void filter_NoAuthorizationHeader_ContinuesWithoutAuthentication() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/profile"));
        WebFilterChain chain = chainExpectingAuthentication(false);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(jwtProvider, never()).validateToken(Mockito.anyString());
    }

    @Test
    void filter_InvalidBearerToken_ContinuesWithoutAuthentication() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer bad-token"));
        when(jwtProvider.validateToken("bad-token")).thenReturn(false);

        StepVerifier.create(filter.filter(exchange, chainExpectingAuthentication(false)))
                .verifyComplete();
    }

    @Test
    void filter_ValidBearerToken_WritesSecurityContext() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer good-token"));
        Claims claims = Jwts.claims().subject("user@example.com").add("role", "ROLE_USER").build();
        when(jwtProvider.validateToken("good-token")).thenReturn(true);
        when(jwtProvider.getClaims("good-token")).thenReturn(claims);

        StepVerifier.create(filter.filter(exchange, chainExpectingAuthentication(true)))
                .verifyComplete();
    }

    private static WebFilterChain chainExpectingAuthentication(boolean authenticated) {
        return exchange -> ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication() != null && context.getAuthentication().isAuthenticated())
                .defaultIfEmpty(false)
                .flatMap(actual -> actual == authenticated
                        ? Mono.empty()
                        : Mono.error(new AssertionError("Authentication expectation failed")));
    }
}
