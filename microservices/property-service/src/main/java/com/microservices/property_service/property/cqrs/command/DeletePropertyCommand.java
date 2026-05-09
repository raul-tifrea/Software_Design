package com.microservices.property_service.property.cqrs.command;

import com.microservices.property_service.property.Property;
import com.microservices.property_service.property.PropertyRepository;

import com.microservices.property_service.property.UserDto;

public class DeletePropertyCommand implements Command<Property> {
    private final Integer propertyId;
    private final UserDto user;
    private final PropertyRepository repository;

    public DeletePropertyCommand(Integer propertyId, UserDto user, PropertyRepository repository) {
        this.propertyId = propertyId;
        this.user = user;
        this.repository = repository;
    }

    @Override
    public Property execute() {
        Property existingProperty = repository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        boolean isAdmin = "ADMIN".equals(user.getRoleName());
        if (!isAdmin && !existingProperty.getSellerId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this property");
        }

        repository.delete(existingProperty);

        return existingProperty;
    }
}