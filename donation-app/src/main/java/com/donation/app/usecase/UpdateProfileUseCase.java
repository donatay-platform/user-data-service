package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> updateProfile(String currentEmail, String nickname, String avatarUrl, String headerUrl, String newEmail, String newPassword, String phoneNumber) {
        return userRepository.findByEmail(currentEmail)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")))
                .flatMap(user -> {
                    if (nickname != null) user.setNickname(nickname);
                    if (avatarUrl != null) user.setAvatarUrl(avatarUrl);
                    if (headerUrl != null) user.setHeaderUrl(headerUrl);
                    if (phoneNumber != null) user.setPhoneNumber(phoneNumber);

                    Mono<Void> emailCheck = Mono.empty();
                    if (newEmail != null && !newEmail.equalsIgnoreCase(currentEmail)) {
                        emailCheck = userRepository.findByEmail(newEmail)
                                .flatMap(existing -> Mono.<Void>error(new DonationException("EMAIL_TAKEN", "Email already taken")))
                                .switchIfEmpty(Mono.fromRunnable(() -> user.setEmail(newEmail)));
                    }

                    if (newPassword != null && !newPassword.isBlank()) {
                        if (newPassword.length() < 6) {
                            return Mono.error(new DonationException("BAD_REQUEST", "Password must be at least 6 characters long"));
                        }
                        user.setPassword(passwordEncoder.encode(newPassword));
                    }

                    return emailCheck.then(Mono.defer(() -> userRepository.save(user)));
                });
    }
}
