package com.realestate.manager.property.export;


import com.realestate.manager.property.Property;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component("jsonStrategy")
public class JsonExportStrategy implements PropertyExportStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExportArchiveRepo archiveRepository;

    public JsonExportStrategy(ExportArchiveRepo archiveRepository) {
        this.archiveRepository = archiveRepository;
    }

    @Override
    public String export(List<Property> properties) {
        try {

            ExportArchive archive = new ExportArchive(properties);
            archiveRepository.save(archive);
            System.out.println("[MongoDB] Successfully saved JSON export archive!");


            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(properties);

        } catch (RuntimeException e) {
            throw new RuntimeException("Error formatting JSON", e);
        }
    }
}