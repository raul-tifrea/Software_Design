package com.realestate.manager.repository;


import com.realestate.manager.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByRoleName(String name);
}
