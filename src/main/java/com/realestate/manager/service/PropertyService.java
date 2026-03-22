package com.realestate.manager.service;


import com.realestate.manager.model.entity.Property;
import com.realestate.manager.model.entity.User;
import com.realestate.manager.repository.PropertyRepository;
import com.realestate.manager.repository.RoleRepository;
import com.realestate.manager.repository.UserRepository;
import org.springframework.data.domain.Sort;
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

        if(!rolename.equals("ADMIN") && !rolename.equals("SELLER_BUYER")) {
            throw new RuntimeException("Invalid role");
        }

        property.setSellerId(sellerId);
        return propertyRepository.save(property);

    }

    public Property updateProperty(Property property, Integer sellerId, Integer propertyId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        Property oldProperty = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));
        String rolename = seller.getRole().getRoleName();

        if(rolename.equals("SELLER") && !oldProperty.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Can only edit own property");
        }else if(!rolename.equals("ADMIN") && !rolename.equals("SELLER_BUYER")) {
            throw new RuntimeException("Invalid role");
        }
        oldProperty.setTitle(property.getTitle());
        oldProperty.setDescription(property.getDescription());
        oldProperty.setPrice(property.getPrice());
        oldProperty.setLocation(property.getLocation());
        oldProperty.setImageUrl(property.getImageUrl());
        return propertyRepository.save(oldProperty);


    }

    public void  deleteProperty(Integer propertyId, Integer sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        Property oldProperty = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));
        String rolename = seller.getRole().getRoleName();

        if(rolename.equals("SELLER_BUYER") && !oldProperty.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Can only delete own property");
        }else if(!rolename.equals("ADMIN") && !rolename.equals("SELLER_BUYER")) {
            throw new RuntimeException("Invalid role");
        }

        propertyRepository.delete(oldProperty);
    }


    public List<Property> getAllProperties(String searchType, String keyword, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        if(keyword == null || keyword.equals("")) {
            return propertyRepository.findAll(sort);
        }

        if("title".equalsIgnoreCase(searchType)) {
            return propertyRepository.findByTitleContainingIgnoreCase(keyword, sort);
        }else{
            return propertyRepository.findByLocationContainingIgnoreCase(keyword, sort);
        }
    }

}
