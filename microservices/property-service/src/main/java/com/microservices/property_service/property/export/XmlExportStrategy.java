package com.microservices.property_service.property.export;


import com.microservices.property_service.property.Property;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("xmlStrategy")
public class XmlExportStrategy implements PropertyExportStrategy{
    @Override
    public String export(List<Property> properties) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<properties>\n");

        for (Property p : properties) {
            xml.append("  <property>\n")
                    .append("    <id>").append(p.getId()).append("</id>\n")
                    .append("    <title>").append(p.getTitle()).append("</title>\n")
                    .append("    <price>").append(p.getPrice()).append("</price>\n")
                    .append("    <location>").append(p.getLocation()).append("</location>\n")
                    .append("    <sellerId>").append(p.getSellerId()).append("</sellerId>\n")
                    .append("  </property>\n");
        }
        xml.append("</properties>");
        return xml.toString();
    }
}
