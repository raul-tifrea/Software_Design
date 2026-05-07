package com.microservices.property_service.property.cqrs.command;

import com.microservices.property_service.property.Property;
import com.microservices.property_service.property.PropertyRepository;

public class CreatePropertyCommand implements Command<Property> {
    private final Property property;
    private final Integer sellerId;
    private final PropertyRepository repository;

    public CreatePropertyCommand(Property property, Integer sellerId, PropertyRepository repository) {
        this.property = property;
        this.sellerId = sellerId;
        this.repository = repository;
    }

    @Override
    public Property execute() {
        property.setSellerId(sellerId);
        return repository.save(property);
    }
}