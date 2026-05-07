package com.microservices.property_service.property.cqrs.command;

import com.microservices.property_service.property.Property;
import com.microservices.property_service.property.PropertyRepository;

public class DeletePropertyCommand implements Command<Void> {
    private final Integer propertyId;
    private final Integer sellerId;
    private final PropertyRepository repository;

    public DeletePropertyCommand(Integer propertyId, Integer sellerId, PropertyRepository repository) {
        this.propertyId = propertyId;
        this.sellerId = sellerId;
        this.repository = repository;
    }

    @Override
    public Void execute() {
        Property existingProperty = repository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!existingProperty.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Not authorized to delete this property");
        }

        repository.delete(existingProperty);
        return null;
    }
}