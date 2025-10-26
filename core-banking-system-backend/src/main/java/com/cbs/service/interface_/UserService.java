package com.cbs.service.interface_;

import com.cbs.model.dto.request.UserRegistrationRequest;
import com.cbs.model.dto.response.UserResponse;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    UserResponse registerUser(UserRegistrationRequest registrationRequest);
    
    Optional<UserResponse> getUserById(Long userId);
    
    Optional<UserResponse> getUserByUsername(String username);
    
    List<UserResponse> getAllUsers();
    
    UserResponse updateUser(Long userId, UserRegistrationRequest updateRequest);
    
    void deleteUser(Long userId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}