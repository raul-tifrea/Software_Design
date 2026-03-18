package com.realestate.manager.service;


import com.realestate.manager.model.entity.Role;
import com.realestate.manager.model.entity.User;
import com.realestate.manager.repository.RoleRepository;
import com.realestate.manager.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,  RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private void verifAdmin(Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found in the database"));
        if(!user.getRole().getRoleName().equals("Admin")){
            throw new RuntimeException("Only Admins can manage users");
        }
    }

    public User createUser(User user, Integer roleId){
        verifAdmin(roleId);
        Role role = roleRepository.findById(user.getRole().getId()).orElseThrow(() -> new RuntimeException("Role not found."));
        user.setRole(role);

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
        existingUser.setPasswordHash(updatedUser.getPasswordHash());
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
