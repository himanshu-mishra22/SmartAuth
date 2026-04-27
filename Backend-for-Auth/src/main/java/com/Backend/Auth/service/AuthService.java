package com.Backend.Auth.service;

import com.Backend.Auth.dtos.UserDto;

public interface AuthService {
    UserDto registerUser(UserDto userDto);
}
