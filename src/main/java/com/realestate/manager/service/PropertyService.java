package com.realestate.manager.service;


import com.realestate.manager.model.entity.Property;
import com.realestate.manager.repository.PropertyRepository;
import com.realestate.manager.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
         this.propertyRepository = propertyRepository;
    }

    public Property saveProperty(Property property){
        if(property.getPrice() <= 0){
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties(){
        return propertyRepository.findAll();
    }

    public Optional<Property> getPropertyById(Integer id){
        return propertyRepository.findById(id);
    }


    public void deletePropertyById(Integer id){
        propertyRepository.deleteById(id);
    }

    public List<Property> searchPropertyByLocation(String location){
        return propertyRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Property> searchPropertyByTitle(String title){
        return propertyRepository.findByTitleContainingIgnoreCase(title);
    }

}
