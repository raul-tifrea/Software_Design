package com.realestate.manager.user;


import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByRoleName(String roleName){
        return roleRepository.findByRoleName(roleName);
    }

}
