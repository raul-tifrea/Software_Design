package com.microservices.property_service.property.cqrs.query;

import com.microservices.property_service.property.Property;
import com.microservices.property_service.property.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyQueryService {

    private final PropertyRepository repository;

    public PropertyQueryService(PropertyRepository repository) {
        this.repository = repository;
    }

    public List<Property> getAllProperties(String searchType, String keyWord, String sortBy, String sortDir) {
        return repository.findAll();
    }

    public Property getPropertyById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
    }
}