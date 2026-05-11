package com.microservices.property_service.property;

import com.microservices.property_service.property.cqrs.command.PropertyCommandService;
import com.microservices.property_service.property.cqrs.query.PropertyQueryService;
import com.microservices.property_service.property.export.PropertyExportStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    private final PropertyCommandService propertyCommandService;
    private final PropertyQueryService propertyQueryService;
    private final Map<String, PropertyExportStrategy> exportStrategies;

    public PropertyController(PropertyCommandService propertyCommandService,
                              PropertyQueryService propertyQueryService,
                              Map<String, PropertyExportStrategy> exportStrategies) {
        this.propertyCommandService = propertyCommandService;
        this.propertyQueryService = propertyQueryService;
        this.exportStrategies = exportStrategies;
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportProperties(@RequestParam String format, @RequestParam(required = false) String searchType, @RequestParam(required = false) String searchName, @RequestParam(required = false) String keyWord, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        List<Property> properties = propertyQueryService.getAllProperties(searchType, keyWord, sortBy, sortDir);
        PropertyExportStrategy propertyExportStrategy = exportStrategies.get(format.toLowerCase() + "Strategy");
        if(propertyExportStrategy == null){
            return ResponseEntity.badRequest().body("Invalid format requested");
        }
        return ResponseEntity.ok(propertyExportStrategy.export(properties));
    }

    @GetMapping
    public ResponseEntity<?> getAllProperties(@RequestParam(required = false) String searchType, @RequestParam(required = false) String searchName, @RequestParam(required = false) String keyWord, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            List<Property> properties = propertyQueryService.getAllProperties(searchType, keyWord, sortBy, sortDir);
            return ResponseEntity.ok(properties);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createProperty(@RequestBody Property property, @RequestParam Integer sellerId) {
        try {
            Property savedProperty = propertyCommandService.createProperty(property, sellerId);
            return ResponseEntity.ok().body(savedProperty);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<?> updateProperty(@PathVariable Integer propertyId, @RequestBody Property property, @RequestParam Integer sellerId) {
        try {
            Property updatedProperty = propertyCommandService.updateProperty(propertyId, property, sellerId);
            return ResponseEntity.ok(updatedProperty);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable Integer propertyId, @RequestParam Integer sellerId) {
        try {
            propertyCommandService.deleteProperty(propertyId, sellerId);
            return ResponseEntity.ok("Property has been deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<?> getPropertyById(@PathVariable Integer propertyId) {
        try {
            Property property = propertyQueryService.getPropertyById(propertyId);
            return ResponseEntity.ok(property);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}