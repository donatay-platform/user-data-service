package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.api.MfaApi;
import com.donation.app.infrastructure.web.dto.MfaSetupResponse;
import com.donation.app.infrastructure.web.dto.MfaVerificationRequest;
import com.donation.app.usecase.SendSmsCodeUseCase;
import com.donation.app.usecase.SetupGoogleMfaUseCase;
import com.donation.app.usecase.SetupSmsMfaUseCase;
import com.donation.app.usecase.VerifyAndEnableMfaUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "MFA / Двухфакторная аутентификация", description = "Настройка и управление Google & SMS MFA")
@SecurityRequirement(name = "BearerAuth")
public class MfaController implements MfaApi {

    private final SetupGoogleMfaUseCase setupGoogleMfaUseCase;
    private final SetupSmsMfaUseCase setupSmsMfaUseCase;
    private final SendSmsCodeUseCase sendSmsCodeUseCase;
    private final VerifyAndEnableMfaUseCase verifyAndEnableMfaUseCase;

    @Override
    public Mono<ResponseEntity<MfaSetupResponse>> setupGoogleMfa(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(email -> setupGoogleMfaUseCase.setupGoogleMfa(email)
                        .map(secret -> ResponseEntity.ok(new MfaSetupResponse()
                                .secret(secret)
                                .qrCodeUrl("otpauth://totp/DonationApp:" + email + "?secret=" + secret + "&issuer=DonationApp"))));
    }

    @Override
    public Mono<ResponseEntity<Void>> setupSmsMfa(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(setupSmsMfaUseCase::enableSmsMfa)
                .map(v -> ResponseEntity.ok().build());
    }

    @Override
    public Mono<ResponseEntity<String>> sendSmsCode(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(sendSmsCodeUseCase::sendSmsCode)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Boolean>> verifyAndEnableMfa(Mono<MfaVerificationRequest> mfaVerificationRequest, ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(email -> mfaVerificationRequest.flatMap(request -> 
                        verifyAndEnableMfaUseCase.verifyAndEnableMfa(email, request.getCode())
                ))
                .map(ResponseEntity::ok);
    }
}
