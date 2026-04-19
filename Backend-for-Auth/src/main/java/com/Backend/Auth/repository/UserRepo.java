package com.Backend.Auth.repository;

import com.Backend.Auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface UserRepo extends JpaRepository<User, UUID> {
//    Optional<User> findByUsername(String username);
}
