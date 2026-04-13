package com.realestate.manager.property;


import com.realestate.manager.event.EventBroker;
import com.realestate.manager.user.User;
import com.realestate.manager.user.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final EventBroker eventBroker;

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository, EventBroker eventBroker) {
         this.propertyRepository = propertyRepository;
         this.userRepository = userRepository;
         this.eventBroker = eventBroker;
    }


    public Property saveProperty(Property property, Integer sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        String rolename = seller.getRole().getRoleName();

        if(!rolename.equals("ADMIN") && !rolename.equals("SELLER_BUYER")) {
            throw new RuntimeException("Invalid role");
        }

        property.setSellerId(sellerId);
        Property savedProperty = propertyRepository.save(property);

        String eventData = "User " + seller.getUsername() + " ID " + sellerId + " added property " + savedProperty.getTitle();
        eventBroker.publish("Property_Created", eventData);

        return savedProperty;

    }

    public Property updateProperty(Property property, Integer sellerId, Integer propertyId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        Property oldProperty = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));
        String rolename = seller.getRole().getRoleName();

        if(rolename.equals("SELLER_BUYER") && !oldProperty.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Can only edit own property");
        }else if(!rolename.equals("ADMIN") && !rolename.equals("SELLER_BUYER")) {
            throw new RuntimeException("Invalid role");
        }
        oldProperty.setTitle(property.getTitle());
        oldProperty.setDescription(property.getDescription());
        oldProperty.setPrice(property.getPrice());
        oldProperty.setLocation(property.getLocation());
        oldProperty.setImageUrl(property.getImageUrl());

        Property updatedProperty = propertyRepository.save(oldProperty);

        String eventData = "User " + seller.getUsername() + " ID " + sellerId + " updated property " + updatedProperty.getTitle();
        eventBroker.publish("Property_Updated", eventData);

        return updatedProperty;


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

        String eventData = "User " + seller.getUsername() + " ID " + sellerId + " deleted property " + oldProperty.getTitle();
        eventBroker.publish("Property_Deleted", eventData);
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
