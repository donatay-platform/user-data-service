package com.donation.app.usecase;

import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import com.donation.app.domain.DonationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> register(String email, String rawPassword) {
        if (email == null || email.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return Mono.error(new DonationException("BAD_REQUEST", "Email and password cannot be empty"));
        }

        return userRepository.findByEmail(email)
                .flatMap(existingUser -> Mono.<User>error(new DonationException("USER_ALREADY_EXISTS", "User with this email already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    User newUser = User.builder()
                            // Оставляем ID пустым для доменного сохранения, 
                            // чтобы база сгенерировала его сама, либо задаем его при конвертации
                            .email(email)
                            .password(passwordEncoder.encode(rawPassword))
                            .role("ROLE_USER")
                            .createdAt(LocalDateTime.now())
                            .build();
                    return userRepository.save(newUser);
                }));
    }
}
