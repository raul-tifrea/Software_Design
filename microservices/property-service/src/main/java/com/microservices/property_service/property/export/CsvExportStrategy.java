package com.microservices.property_service.property.export;


import com.microservices.property_service.property.Property;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("csvStrategy")
public class CsvExportStrategy extends BasePropertyExporter implements PropertyExportStrategy {

    @Override
    protected String writeOutput(List<Property> formattedData) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID, Title, Description, Price, Location, SellerID\n");

        for (Property property : formattedData) {
            csv.append(property.getId()).append(", ")
               .append(property.getTitle()).append(", ")
               .append(property.getDescription()).append(", ")
               .append(property.getPrice()).append(", ")
               .append(property.getLocation()).append(",")
               .append(property.getSellerId()).append("\n");
        }

        return csv.toString();
    }

    @Override
    public String export(List<Property> properties) {
        // Trigger the template method
        return super.executeExport(properties);
    }
}
