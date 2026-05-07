package com.microservices.property_service.property.cqrs.command;

import com.microservices.property_service.property.Property;
import com.microservices.property_service.property.PropertyRepository;

// 1. Change Command<Void> to Command<Property>
public class DeletePropertyCommand implements Command<Property> {
    private final Integer propertyId;
    private final Integer sellerId;
    private final PropertyRepository repository;

    public DeletePropertyCommand(Integer propertyId, Integer sellerId, PropertyRepository repository) {
        this.propertyId = propertyId;
        this.sellerId = sellerId;
        this.repository = repository;
    }

    @Override
    // 2. Change Void to Property
    public Property execute() {
        Property existingProperty = repository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!existingProperty.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Not authorized to delete this property");
        }

        repository.delete(existingProperty);

        // 3. Return the property we just deleted instead of null!
        return existingProperty;
    }
}