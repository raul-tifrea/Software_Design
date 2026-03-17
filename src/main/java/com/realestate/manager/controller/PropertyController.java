package com.realestate.manager.controller;


import com.realestate.manager.model.entity.Property;
import com.realestate.manager.repository.PropertyRepository;
import com.realestate.manager.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    private final PropertyService propertyService;
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public List<Property> getAllProperties(){
        return propertyService.getAllProperties();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Integer id){
        return propertyService.getPropertyById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Property createProperty(@RequestBody Property property){
        return propertyService.saveProperty(property);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deleteProperty(@PathVariable Property property){
        propertyService.deletePropertyById(property.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/location")
    public List<Property> searchPropertyByLocation(@RequestParam String location){
        return propertyService.searchPropertyByLocation(location);
    }

    @GetMapping("/search/title")
    public List<Property> searchPropertyByTitle(@RequestParam String title){
        return propertyService.searchPropertyByTitle(title);
    }

}
