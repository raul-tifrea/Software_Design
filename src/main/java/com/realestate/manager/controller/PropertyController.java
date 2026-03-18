package com.realestate.manager.controller;


import com.realestate.manager.model.entity.Property;
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

    @PostMapping
    public ResponseEntity<?> createProperty(@RequestBody Property property, @RequestParam Integer id) {
        try{
            Property savedProperty = propertyService.saveProperty(property, id);
            return  ResponseEntity.ok().body(savedProperty);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProperty(@RequestBody Property property, @RequestParam Integer id) {
        try{
            Property updatedProperty = propertyService.saveProperty(property, id);
            return  ResponseEntity.ok(updatedProperty);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable Integer id, @RequestParam Integer propertyId) {
        try{
            propertyService.deleteProperty(propertyId, id);
            return  ResponseEntity.ok("Property has been deleted");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
