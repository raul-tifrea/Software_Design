package com.realestate.manager.repository;

import com.realestate.manager.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository  extends JpaRepository<Property, Integer> {

    List<Property> findByLocationContainingIgnoreCase(String location);
    List<Property> findByTitleContainingIgnoreCase(String location);

}
