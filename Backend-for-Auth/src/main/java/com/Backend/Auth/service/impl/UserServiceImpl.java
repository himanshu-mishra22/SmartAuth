package com.Backend.Auth.service.impl;

import com.Backend.Auth.dtos.UserDto;
import com.Backend.Auth.entities.Provider;
import com.Backend.Auth.entities.User;
import com.Backend.Auth.exceptions.ResourceNotFoundException;
import com.Backend.Auth.helpers.UserHelper;
import com.Backend.Auth.repository.UserRepo;
import com.Backend.Auth.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        UUID uId = UserHelper.parseUUId(userId);
        User existingUser = userRepo.findById(uId).orElseThrow(()-> new ResourceNotFoundException("User not found"));

        //assuming email id is unique
        if(userDto.getName() !=null){
            existingUser.setName(userDto.getName());
        }
        if(userDto.getImage() !=null){
            existingUser.setImage(userDto.getImage());
        }
        if(userDto.getProvider() !=null){
            existingUser.setProvider(userDto.getProvider());
        }

        //TODO: Improve Password updation logic
//        if(userDto.getPassword() !=null){
//            existingUser.setPassword(userDto.getPassword());
//        }
        existingUser.setEnable(userDto.isEnable());
        User user = userRepo.save(existingUser);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
       User user = userRepo.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found!"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepo.findById(UserHelper.parseUUId(userId)).orElseThrow(()-> new ResourceNotFoundException("User not found!"));
        return  modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        UUID user_id = UserHelper.parseUUId(userId);
        User user =  userRepo.findById(user_id).orElseThrow(()-> new ResourceNotFoundException("User not found!"));
        userRepo.delete(user);
    }

    @Override
    @Transactional
    public Iterable<UserDto> getAllUsers() {
        return userRepo.findAll().stream().map(user -> modelMapper.map(user,UserDto.class)).toList();
    }
}
