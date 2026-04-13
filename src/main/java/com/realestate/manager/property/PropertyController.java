package com.realestate.manager.property;


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
    public ResponseEntity<?> getAllProperties(@RequestParam String searchType, @RequestParam String keyword, @RequestParam String sortBy, @RequestParam String sortDir){
        try{
            List<Property> properties = propertyService.getAllProperties(searchType, keyword, sortBy, sortDir);
            return ResponseEntity.ok(properties);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping
    public ResponseEntity<?> createProperty(@RequestBody Property property, @RequestParam Integer sellerId) {
        try{
            Property savedProperty = propertyService.saveProperty(property, sellerId);
            return  ResponseEntity.ok().body(savedProperty);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<?> updateProperty(@PathVariable Integer propertyId,@RequestBody Property property, @RequestParam Integer sellerId) {
        try{
            Property updatedProperty = propertyService.updateProperty(property, sellerId, propertyId);
            return  ResponseEntity.ok(updatedProperty);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable Integer propertyId,  @RequestParam Integer sellerId) {
        try{
            propertyService.deleteProperty(propertyId, sellerId);
            return  ResponseEntity.ok("Property has been deleted");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
