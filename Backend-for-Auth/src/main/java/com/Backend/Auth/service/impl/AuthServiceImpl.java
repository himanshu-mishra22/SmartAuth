package com.Backend.Auth.service.impl;

import com.Backend.Auth.dtos.UserDto;
import com.Backend.Auth.service.AuthService;
import com.Backend.Auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDto registerUser(UserDto userDto) {
//        TODO: create verification logic & role
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userService.createUser(userDto);
    }
}
