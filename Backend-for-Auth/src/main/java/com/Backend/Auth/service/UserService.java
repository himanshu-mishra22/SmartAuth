package com.Backend.Auth.service;

import com.Backend.Auth.dtos.UserDto;

public interface UserService {

    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto, String userId );
    UserDto getUserByEmail(String email);
    UserDto getUserByUsername(String username);
    UserDto getUserById(String userId);
    void deleteUser(String userId);
    Iterable<UserDto> getAllUsers();



}
