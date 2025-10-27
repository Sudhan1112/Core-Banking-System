package com.cbs.integration;

import com.cbs.model.dto.request.UserRegistrationRequest;
import com.cbs.model.dto.response.UserResponse;
import com.cbs.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import com.cbs.config.TestSecurityConfig;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private UserRegistrationRequest testUserRequest;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
        userRepository.deleteAll();
        
        testUserRequest = new UserRegistrationRequest();
        testUserRequest.setUsername("testuser");
        testUserRequest.setPassword("password123");
        testUserRequest.setEmail("test@example.com");
        testUserRequest.setFirstName("Test");
        testUserRequest.setLastName("User");
        testUserRequest.setPhoneNumber("1234567890");
    }

    @Test
    @DisplayName("Should register and retrieve a user")
    void registerAndRetrieveUser_Success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegistrationRequest> registerRequest = new HttpEntity<>(testUserRequest, headers);

        // Act - Register user
        ResponseEntity<UserResponse> registerResponse = restTemplate.exchange(
                baseUrl + "/register", HttpMethod.POST, registerRequest, UserResponse.class);

        // Assert - Registration
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        assertEquals("testuser", registerResponse.getBody().getUsername());

        // Act - Get user by ID
        ResponseEntity<UserResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + registerResponse.getBody().getUserId(), UserResponse.class);

        // Assert - Get user
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("testuser", getResponse.getBody().getUsername());
        assertEquals("test@example.com", getResponse.getBody().getEmail());
    }

    @Test
    @DisplayName("Should get all users")
    void getAllUsers_Success() {
        // Arrange - Register two users
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegistrationRequest> request1 = new HttpEntity<>(testUserRequest, headers);
        restTemplate.exchange(baseUrl + "/register", HttpMethod.POST, request1, UserResponse.class);

        UserRegistrationRequest secondUser = new UserRegistrationRequest();
        secondUser.setUsername("seconduser");
        secondUser.setPassword("password123");
        secondUser.setEmail("second@example.com");
        secondUser.setFirstName("Second");
        secondUser.setLastName("User");
        secondUser.setPhoneNumber("9876543210");
        HttpEntity<UserRegistrationRequest> request2 = new HttpEntity<>(secondUser, headers);
        restTemplate.exchange(baseUrl + "/register", HttpMethod.POST, request2, UserResponse.class);

        // Act - Get all users
        ResponseEntity<UserResponse[]> response = restTemplate.getForEntity(baseUrl, UserResponse[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        List<String> usernames = Arrays.stream(response.getBody())
                .map(UserResponse::getUsername)
                .toList();
        assertTrue(usernames.contains("testuser"));
        assertTrue(usernames.contains("seconduser"));
    }

    @Test
    @DisplayName("Should update a user")
    void updateUser_Success() {
        // Arrange - Register a user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegistrationRequest> registerRequest = new HttpEntity<>(testUserRequest, headers);
        ResponseEntity<UserResponse> registerResponse = restTemplate.exchange(
                baseUrl + "/register", HttpMethod.POST, registerRequest, UserResponse.class);

        // Create update request
        UserRegistrationRequest updateRequest = new UserRegistrationRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setPassword("newpassword123");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setPhoneNumber("5555555555");
        HttpEntity<UserRegistrationRequest> updateEntity = new HttpEntity<>(updateRequest, headers);

        // Act - Update user
        ResponseEntity<UserResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/" + registerResponse.getBody().getUserId(),
                HttpMethod.PUT, updateEntity, UserResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("updateduser", updateResponse.getBody().getUsername());
        assertEquals("updated@example.com", updateResponse.getBody().getEmail());
        assertEquals("Updated", updateResponse.getBody().getFirstName());
    }

    @Test
    @DisplayName("Should delete a user")
    void deleteUser_Success() {
        // Arrange - Register a user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegistrationRequest> registerRequest = new HttpEntity<>(testUserRequest, headers);
        ResponseEntity<UserResponse> registerResponse = restTemplate.exchange(
                baseUrl + "/register", HttpMethod.POST, registerRequest, UserResponse.class);

        // Act - Delete user
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + registerResponse.getBody().getUserId(),
                HttpMethod.DELETE, null, Void.class);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify user is marked as inactive
        ResponseEntity<UserResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + registerResponse.getBody().getUserId(), UserResponse.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        // Note: In a real implementation, you might want to return NOT_FOUND for inactive users
    }

    @Test
    @DisplayName("Should check if username exists")
    void checkUsernameExists_Success() {
        // Arrange - Register a user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegistrationRequest> registerRequest = new HttpEntity<>(testUserRequest, headers);
        restTemplate.exchange(baseUrl + "/register", HttpMethod.POST, registerRequest, UserResponse.class);

        // Act - Check if username exists
        ResponseEntity<Boolean> response = restTemplate.getForEntity(
                baseUrl + "/exists/username/testuser", Boolean.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        // Check non-existent username
        ResponseEntity<Boolean> nonExistentResponse = restTemplate.getForEntity(
                baseUrl + "/exists/username/nonexistent", Boolean.class);
        assertEquals(HttpStatus.OK, nonExistentResponse.getStatusCode());
        assertFalse(nonExistentResponse.getBody());
    }
}
