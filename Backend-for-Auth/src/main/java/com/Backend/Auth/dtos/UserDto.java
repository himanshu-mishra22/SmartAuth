package com.Backend.Auth.dtos;

import com.Backend.Auth.entities.Provider;
import com.Backend.Auth.entities.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String name;
    private String password;
    private String image;
    private boolean enable =true;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Provider provider = Provider.LOCAL;
    private Set<RoleDto> roles = new HashSet<>();
}
