package com.microservices.property_service.property;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository  extends JpaRepository<Property, Integer> {

    List<Property> findByLocationContainingIgnoreCase(String location, Sort sort);
    List<Property> findByTitleContainingIgnoreCase(String title, Sort sort);

}
