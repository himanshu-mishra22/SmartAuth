package com.Backend.Auth.service;

import com.Backend.Auth.dtos.UserDto;
import com.Backend.Auth.entities.Provider;
import com.Backend.Auth.entities.User;
import com.Backend.Auth.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if(userDto.getEmail()==null || userDto.getEmail().isBlank()){
            throw new IllegalArgumentException("Email is required");
        }

        if(userRepo.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        User user = modelMapper.map(userDto, User.class);
        user.setProvider(userDto.getProvider()!= null ? userDto.getProvider() : Provider.LOCAL);

        //TODO: assign role to new user for auth

        User savedUser = userRepo.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        return null;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        userRepo.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("Email not found"));
        
    }

    @Override
    public UserDto getUserByUsername(String username) {
        return null;
    }

    @Override
    public UserDto getUserById(String userId) {
        return null;
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    @Transactional
    public Iterable<UserDto> getAllUsers() {
        return userRepo.findAll().stream().map(user -> modelMapper.map(user,UserDto.class)).toList();
    }
}
