package com.microservices.property_service.property.cqrs.command;

import com.microservices.property_service.property.Property;
import com.microservices.property_service.property.PropertyRepository;

public class UpdatePropertyCommand implements Command<Property> {
    private final Integer propertyId;
    private final Property updatedProperty;
    private final Integer sellerId;
    private final PropertyRepository repository;

    public UpdatePropertyCommand(Integer propertyId, Property updatedProperty, Integer sellerId, PropertyRepository repository) {
        this.propertyId = propertyId;
        this.updatedProperty = updatedProperty;
        this.sellerId = sellerId;
        this.repository = repository;
    }

    @Override
    public Property execute() {
        Property existingProperty = repository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!existingProperty.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Not authorized to update this property");
        }

        existingProperty.setTitle(updatedProperty.getTitle());
        existingProperty.setDescription(updatedProperty.getDescription());
        existingProperty.setPrice(updatedProperty.getPrice());
        existingProperty.setLocation(updatedProperty.getLocation());

        return repository.save(existingProperty);
    }
}