package com.donation.app.infrastructure.persistence;

import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataUserRepository springRepository;

    @Override
    public Mono<User> findByEmail(String email) {
        return springRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Mono<User> save(User user) {
        return springRepository.save(toEntity(user))
                .map(this::toDomain);
    }

    @Override
    public Mono<User> findById(UUID id) {
        return springRepository.findById(id)
                .map(this::toDomain);
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(entity.getRole())
                .nickname(entity.getNickname())
                .avatarUrl(entity.getAvatarUrl())
                .headerUrl(entity.getHeaderUrl())
                .mfaEnabled(entity.isMfaEnabled())
                .mfaType(entity.getMfaType())
                .mfaSecret(entity.getMfaSecret())
                .phoneNumber(entity.getPhoneNumber())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .headerUrl(user.getHeaderUrl())
                .mfaEnabled(user.isMfaEnabled())
                .mfaType(user.getMfaType())
                .mfaSecret(user.getMfaSecret())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .isNew(user.getId() == null) // Если ID в домене пустой - значит сущность абсолютно новая
                .build();
    }
}
