package com.cbs.service.impl;

import com.cbs.model.dto.request.UserRegistrationRequest;
import com.cbs.model.dto.response.UserResponse;
import com.cbs.model.entity.User;
import com.cbs.model.enums.UserStatus;
import com.cbs.repository.UserRepository;
import com.cbs.service.interface_.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserResponse registerUser(UserRegistrationRequest registrationRequest) {
        if (existsByUsername(registrationRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (existsByEmail(registrationRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setEmail(registrationRequest.getEmail());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setPhoneNumber(registrationRequest.getPhoneNumber());
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    @Override
    public Optional<UserResponse> getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertToResponse);
    }
    
    @Override
    public Optional<UserResponse> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToResponse);
    }
    
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public UserResponse updateUser(Long userId, UserRegistrationRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if the new username or email is already taken by another user
        if (!user.getUsername().equals(updateRequest.getUsername()) && 
            existsByUsername(updateRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (!user.getEmail().equals(updateRequest.getEmail()) && 
            existsByEmail(updateRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setUsername(updateRequest.getUsername());
        user.setEmail(updateRequest.getEmail());
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        
        // Only update password if it's provided
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }
    
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setStatus(user.getStatus());
        return response;
    }
}