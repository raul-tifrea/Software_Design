package com.microservices.property_service.property.export;

import com.microservices.property_service.property.Property;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Document(collection = "json_export_archives")
public class ExportArchive {

    @Id
    private String id;
    private String timestamp;
    private List<Property> exportedData;

    public ExportArchive(List<Property> exportedData) {
        this.timestamp = LocalDateTime.now().toString();
        this.exportedData = exportedData;
    }

    public String getId() { return id; }
    public String getTimestamp() { return timestamp; }
    public List<Property> getExportedData() { return exportedData; }
}