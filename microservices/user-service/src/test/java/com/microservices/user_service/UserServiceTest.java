package com.microservices.user_service;

import com.microservices.user_service.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_Success() {

        Role role = new Role("ADMIN");
        User user = new User("john", "hashedPass", "john@test.com", role);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPass", "hashedPass")).thenReturn(true);

        User result = userService.authenticate("john", "rawPass");

        assertNotNull(result);
        assertEquals("john", result.getUsername());
        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    void authenticate_WrongPassword_ThrowsException() {

        Role role = new Role("ADMIN");
        User user = new User("john", "hashedPass", "john@test.com", role);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hashedPass")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.authenticate("john", "wrongPass"));
        assertEquals("Error: Invalid password.", ex.getMessage());
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.authenticate("ghost", "anyPass"));
        assertEquals("Error: User not found.", ex.getMessage());
    }

    @Test
    void getAllUser_AsAdmin_ReturnsUserList() {

        Role adminRole = new Role("ADMIN");
        User admin = new User("admin", "hash", "admin@test.com", adminRole);
        User user2 = new User("bob", "hash2", "bob@test.com", adminRole);

        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        when(userRepository.findAll()).thenReturn(Arrays.asList(admin, user2));

        List<User> result = userService.getAllUser(1);

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUser_AsNonAdmin_ThrowsException() {

        Role userRole = new Role("SELLER_BUYER");
        User nonAdmin = new User("bob", "hash", "bob@test.com", userRole);

        when(userRepository.findById(2)).thenReturn(Optional.of(nonAdmin));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getAllUser(2));
        assertEquals("Only Admins can manage users", ex.getMessage());
    }

    @Test
    void register_Success() {

        Role role = new Role("SELLER_BUYER");
        User newUser = new User("alice", "plainPassword", "alice@test.com", role);

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("SELLER_BUYER")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(newUser);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPasswordHash());
        assertEquals("SELLER_BUYER", result.getRole().getRoleName());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void deleteUser_AsAdmin_Success() {

        Role adminRole = new Role("ADMIN");
        User admin = new User("admin", "hash", "admin@test.com", adminRole);

        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        doNothing().when(userRepository).deleteById(5);

        userService.deleteUser(5, 1);

        verify(userRepository, times(1)).deleteById(5);
    }

    @Test
    void deleteUser_AsNonAdmin_ThrowsException() {

        Role userRole = new Role("SELLER_BUYER");
        User nonAdmin = new User("bob", "hash", "bob@test.com", userRole);

        when(userRepository.findById(2)).thenReturn(Optional.of(nonAdmin));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(5, 2));
        assertEquals("Only Admins can manage users", ex.getMessage());
        verify(userRepository, never()).deleteById(any());
    }
}
