package com.microservices.property_service.property.export;


import com.microservices.property_service.property.Property;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("csvStrategy")
public class CsvExportStrategy implements PropertyExportStrategy {
    @Override
    public String export(List<Property>  properties) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID, Title, Description, Price, Location, SellerID\n");

        for (Property property : properties) {
            csv.append(property.getId()).append(", ").append(property.getTitle()).append(", ").append(property.getDescription()).append(", ").append(property.getPrice()).append(", ").append(property.getLocation()).append(",").append(property.getSellerId()).append("\n");
        }

        return csv.toString();
    }
}
