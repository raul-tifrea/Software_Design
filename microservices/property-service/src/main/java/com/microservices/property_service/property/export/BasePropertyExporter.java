package com.microservices.property_service.property.export;

import com.microservices.property_service.property.Property;
import java.util.List;

public abstract class BasePropertyExporter {

    public final String executeExport(List<Property> properties) {
        List<Property> formattedData = transformData(properties);
        return writeOutput(formattedData);
    }

    protected List<Property> transformData(List<Property> properties) {
        return properties;
    }

    protected abstract String writeOutput(List<Property> formattedData);
}