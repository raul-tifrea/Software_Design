package com.microservices.user_service.user;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Integer>{
    Optional<Role> findByRoleName(String name);
}
