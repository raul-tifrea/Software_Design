package com.realestate.manager.user;


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


    public User register(User user){
        Role role = roleRepository.findByRoleName("SELLER_BUYER")
            .orElseThrow(() -> new RuntimeException("Error: Default role not found."));
        user.setRole(role);
user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
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
        Role role = roleRepository.findById(user.getRole().getId())
                .orElseThrow(() -> new RuntimeException("Role not found."));
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

        if(updatedUser.getRole() != null && updatedUser.getRole().getId() != null){
            Role  role = roleRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Role not found."));
            existingUser.setRole(role);
        }

        return userRepository.save(existingUser);
    }


    public void deleteUser(Integer userId, Integer requestId){
        verifAdmin(requestId);
        userRepository.deleteById(userId);
    }

}
