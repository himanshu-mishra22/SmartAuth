package com.Backend.Auth.dtos;

import jakarta.persistence.Column;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleDto {
    private UUID id = UUID.randomUUID();
    private String name;
}
