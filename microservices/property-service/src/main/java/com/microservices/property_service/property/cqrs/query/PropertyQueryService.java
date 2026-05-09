package com.microservices.property_service.property.cqrs.query;

import com.microservices.property_service.property.Property;
import com.microservices.property_service.property.PropertyRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyQueryService {

    private final PropertyRepository repository;

    public PropertyQueryService(PropertyRepository repository) {
        this.repository = repository;
    }

    public List<Property> getAllProperties(String searchType, String keyWord, String sortBy, String sortDir) {
        Sort sort = buildSort(sortBy, sortDir);

        if (searchType != null && keyWord != null && !keyWord.isBlank()) {
            return switch (searchType.toLowerCase()) {
                case "location" -> repository.findByLocationContainingIgnoreCase(keyWord, sort);
                case "title"    -> repository.findByTitleContainingIgnoreCase(keyWord, sort);
                default         -> repository.findAll(sort);
            };
        }

        return repository.findAll(sort);
    }

    public Property getPropertyById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}