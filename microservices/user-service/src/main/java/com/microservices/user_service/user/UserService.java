package com.microservices.user_service.user;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,  RoleRepository roleRepository,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("Error: Invalid password.");
        }
        return user;
    }


    public User register(User user) {

        String rawPassword = user.getPasswordHash();
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        Role dbRole = roleRepository.findByRoleName(user.getRole().getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(dbRole);

        return userRepository.save(user);
    }


    private void verifAdmin(Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found in the database"));
        if(!user.getRole().getRoleName().equals("ADMIN")){
            throw new RuntimeException("Only Admins can manage users");
        }
    }

    public User createUser(User user, Integer roleId){
        verifAdmin(roleId);
        Role role;
        if (user.getRole().getRoleName() != null) {
            role = roleRepository.findByRoleName(user.getRole().getRoleName())
                    .orElseThrow(() -> new RuntimeException("Role not found by name: " + user.getRole().getRoleName()));
        } else if (user.getRole().getId() != null) {
            Integer fallbackId = user.getRole().getId();
            if (fallbackId == 3) fallbackId = 2; // Map cached "3" to actual "2" (SELLER_BUYER)
            role = roleRepository.findById(fallbackId)
                    .orElseThrow(() -> new RuntimeException("Role not found by ID: " + user.getRole().getId()));
        } else {
            throw new RuntimeException("Role information is missing");
        }
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    public List<User> getAllUser(Integer roleId){
        verifAdmin(roleId);
        return userRepository.findAll();
    }

    public User updateUser(User updatedUser, Integer requestId, Integer userId){
        verifAdmin(requestId);
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found in the database"));
        existingUser.setUsername(updatedUser.getUsername());
        if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
        }
        existingUser.setEmail(updatedUser.getEmail());

        if(updatedUser.getRole() != null) {
            Role role;
            if (updatedUser.getRole().getRoleName() != null) {
                role = roleRepository.findByRoleName(updatedUser.getRole().getRoleName())
                        .orElseThrow(() -> new RuntimeException("Role not found by name: " + updatedUser.getRole().getRoleName()));
            } else if (updatedUser.getRole().getId() != null) {
                Integer fallbackId = updatedUser.getRole().getId();
                if (fallbackId == 3) fallbackId = 2;
                role = roleRepository.findById(fallbackId)
                        .orElseThrow(() -> new RuntimeException("Role not found by ID: " + updatedUser.getRole().getId()));
            } else {
                throw new RuntimeException("Role information is missing");
            }
            existingUser.setRole(role);
        }

        return userRepository.save(existingUser);
    }


    public void deleteUser(Integer userId, Integer requestId){
        verifAdmin(requestId);
        userRepository.deleteById(userId);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public java.util.Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
