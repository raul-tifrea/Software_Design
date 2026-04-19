package com.realestate.manager.property.export;

import com.realestate.manager.property.Property;

import java.util.List;

public interface PropertyExportStrategy {
    String export(List<Property> properties);
}
