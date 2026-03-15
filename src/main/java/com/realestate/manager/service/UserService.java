package com.realestate.manager.service;


import com.realestate.manager.model.entity.User;
import com.realestate.manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUserName(String userName){
        return userRepository.findByUsername(userName);
    }
}
