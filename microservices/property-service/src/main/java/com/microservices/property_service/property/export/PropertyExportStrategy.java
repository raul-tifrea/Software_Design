package com.microservices.property_service.property.export;

import com.microservices.property_service.property.Property;

import java.util.List;

public interface PropertyExportStrategy {
    String export(List<Property> properties);
}
