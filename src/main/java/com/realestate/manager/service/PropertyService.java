package com.realestate.manager.service;


import com.realestate.manager.model.entity.Property;
import com.realestate.manager.model.entity.User;
import com.realestate.manager.repository.PropertyRepository;
import com.realestate.manager.repository.RoleRepository;
import com.realestate.manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository) {
         this.propertyRepository = propertyRepository;
         this.userRepository = userRepository;
    }


    public Property saveProperty(Property property, Integer sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        String rolename = seller.getRole().getRoleName();

        if(!rolename.equals("Admin") && !rolename.equals("Seller")) {
            throw new RuntimeException("Invalid role");
        }

        property.setSellerId(sellerId);
        return propertyRepository.save(property);

    }

    public Property updateProperty(Property property, Integer sellerId, Integer propertyId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        Property oldProperty = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));
        String rolename = seller.getRole().getRoleName();

        if(rolename.equals("Seller") && !oldProperty.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Can only edit own property");
        }else if(!rolename.equals("Admin") && !rolename.equals("Seller")) {
            throw new RuntimeException("Invalid role");
        }
        oldProperty.setTitle(property.getTitle());
        oldProperty.setDescription(property.getDescription());
        oldProperty.setPrice(property.getPrice());
        oldProperty.setLocation(property.getLocation());

        return propertyRepository.save(oldProperty);


    }

    public void  deleteProperty(Integer propertyId, Integer sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        Property oldProperty = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));
        String rolename = seller.getRole().getRoleName();

        if(rolename.equals("Seller") && !oldProperty.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Can only delete own property");
        }else if(!rolename.equals("Admin") && !rolename.equals("Seller")) {
            throw new RuntimeException("Invalid role");
        }

        propertyRepository.delete(oldProperty);
    }


    public List<Property> getAllProperties(){
        return propertyRepository.findAll();
    }

    public Optional<Property> getPropertyById(Integer id){
        return propertyRepository.findById(id);
    }

    public List<Property> searchPropertyByLocation(String location){
        return propertyRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Property> searchPropertyByTitle(String title){
        return propertyRepository.findByTitleContainingIgnoreCase(title);
    }

}
