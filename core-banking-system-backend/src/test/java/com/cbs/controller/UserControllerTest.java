package com.cbs.controller;

import com.cbs.config.SecurityConfig;
import com.cbs.model.dto.request.UserRegistrationRequest;
import com.cbs.model.dto.response.UserResponse;
import com.cbs.model.enums.UserStatus;
import com.cbs.service.impl.CustomUserDetailsService;
import com.cbs.service.interface_.UserService;
import com.cbs.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse testUserResponse;
    private UserRegistrationRequest testUserRequest;

    @BeforeEach
    void setUp() {
        testUserResponse = new UserResponse();
        testUserResponse.setUserId(1L);
        testUserResponse.setUsername("testuser");
        testUserResponse.setEmail("test@example.com");
        testUserResponse.setFirstName("Test");
        testUserResponse.setLastName("User");
        testUserResponse.setPhoneNumber("1234567890");
        testUserResponse.setCreatedAt(LocalDateTime.now());
        testUserResponse.setStatus(UserStatus.ACTIVE);

        testUserRequest = new UserRegistrationRequest();
        testUserRequest.setUsername("testuser");
        testUserRequest.setPassword("password123");
        testUserRequest.setEmail("test@example.com");
        testUserRequest.setFirstName("Test");
        testUserRequest.setLastName("User");
        testUserRequest.setPhoneNumber("1234567890");
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void registerUser_Success() throws Exception {
        // Arrange
        when(userService.registerUser(any(UserRegistrationRequest.class)))
                .thenReturn(testUserResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should return 400 when registering with invalid data")
    void registerUser_InvalidData() throws Exception {
        // Arrange
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest();
        invalidRequest.setUsername(""); // Invalid empty username

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get all users successfully")
    @WithMockUser
    void getAllUsers_Success() throws Exception {
        // Arrange
        List<UserResponse> users = Arrays.asList(testUserResponse);
        when(userService.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_Success() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUserResponse));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("Should return 404 when user not found by ID")
    void getUserById_NotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void getUserByUsername_Success() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUserResponse));

        // Act & Assert
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_Success() throws Exception {
        // Arrange
        when(userService.updateUser(eq(1L), any(UserRegistrationRequest.class)))
                .thenReturn(testUserResponse);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should check if username exists")
    void checkUsernameExists_True() throws Exception {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/users/exists/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("Should check if email exists")
    void checkEmailExists_False() throws Exception {
        // Arrange
        when(userService.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/users/exists/email/nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
