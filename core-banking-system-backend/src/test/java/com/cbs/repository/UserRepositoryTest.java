package com.cbs.repository;

import com.cbs.model.entity.User;
import com.cbs.model.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPhoneNumber("1234567890");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setStatus(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find user by username")
    void findByUsername_Success() {
        // Arrange
        entityManager.persistAndFlush(testUser);

        // Act
        Optional<User> result = userRepository.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void findByUsername_NotFound() {
        // Act
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_Success() {
        // Arrange
        entityManager.persistAndFlush(testUser);

        // Act
        Optional<User> result = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void findByEmail_NotFound() {
        // Act
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should check if username exists")
    void existsByUsername_True() {
        // Arrange
        entityManager.persistAndFlush(testUser);

        // Act
        boolean result = userRepository.existsByUsername("testuser");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when username doesn't exist")
    void existsByUsername_False() {
        // Act
        boolean result = userRepository.existsByUsername("nonexistent");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail_True() {
        // Arrange
        entityManager.persistAndFlush(testUser);

        // Act
        boolean result = userRepository.existsByEmail("test@example.com");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when email doesn't exist")
    void existsByEmail_False() {
        // Act
        boolean result = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should find user by ID and status")
    void findByIdAndStatus_Success() {
        // Arrange
        entityManager.persistAndFlush(testUser);

        // Act
        Optional<User> result = userRepository.findByIdAndStatus(testUser.getUserId(), UserStatus.ACTIVE);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getUserId(), result.get().getUserId());
        assertEquals(UserStatus.ACTIVE, result.get().getStatus());
    }

    @Test
    @DisplayName("Should return empty when user ID exists but status doesn't match")
    void findByIdAndStatus_StatusMismatch() {
        // Arrange
        entityManager.persistAndFlush(testUser);

        // Act
        Optional<User> result = userRepository.findByIdAndStatus(testUser.getUserId(), UserStatus.INACTIVE);

        // Assert
        assertFalse(result.isPresent());
    }
}
