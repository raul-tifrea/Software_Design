package com.realestate.manager.property.export;


import com.realestate.manager.property.Property;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component("jsonStrategy")
public class JsonExportStrategy implements PropertyExportStrategy {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String export(List<Property> properties) {
        try{
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(properties);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
