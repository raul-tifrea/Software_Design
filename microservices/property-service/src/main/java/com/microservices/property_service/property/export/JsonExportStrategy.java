package com.microservices.property_service.property.export;

import com.microservices.property_service.property.Property;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component("jsonStrategy")
public class JsonExportStrategy extends BasePropertyExporter implements PropertyExportStrategy {

    @Override
    protected String writeOutput(List<Property> formattedData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Convert to a JSON String instead of bytes
            return mapper.writeValueAsString(formattedData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export JSON", e);
        }
    }

    @Override
    public String export(List<Property> properties) {
        // Trigger the template method
        return super.executeExport(properties);
    }
}