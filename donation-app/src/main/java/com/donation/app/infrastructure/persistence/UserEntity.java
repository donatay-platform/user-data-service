package com.donation.app.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserEntity implements Persistable<UUID> {
    @Id
    private UUID id;
    private String email;
    private String password;
    private String role;
    
    private String nickname;
    private String avatarUrl;
    private String headerUrl;
    
    private boolean mfaEnabled;
    private String mfaType;
    private String mfaSecret;
    private String phoneNumber;
    
    private LocalDateTime createdAt;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
