package com.realestate.manager.property;

import com.realestate.manager.event.RabbitMQConfig;
import com.realestate.manager.user.User;
import com.realestate.manager.user.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository, RabbitTemplate rabbitTemplate) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Property saveProperty(Property property, Integer sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        String rolename = seller.getRole().getRoleName();

        if(!rolename.equals("ADMIN") && !rolename.equals("SELLER_BUYER")) {
            throw new RuntimeException("Invalid role");
        }

        property.setSellerId(sellerId);
        Property savedProperty = propertyRepository.save(property);


        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String emailBody = "Event: User " + seller.getUsername() + " added property " + savedProperty.getTitle() + "\n" + "Type: CREATED\n" + "Occured At: " + time;
        String eventData = seller.getEmail() + emailBody;
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, eventData);

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


        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String emailBody = "Event: User " + seller.getUsername() + " updated property " + updatedProperty.getTitle() + "\n" + "Type: UPDATED\n" + "Occured At: " + time;
        String eventData = seller.getEmail() + emailBody;
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, eventData);

        return updatedProperty;
    }

    public void deleteProperty(Integer propertyId, Integer sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        Property oldProperty = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));
        String rolename = seller.getRole().getRoleName();

        if(rolename.equals("SELLER_BUYER") && !oldProperty.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Can only delete own property");
        }else if(!rolename.equals("ADMIN") && !rolename.equals("SELLER_BUYER")) {
            throw new RuntimeException("Invalid role");
        }

        propertyRepository.delete(oldProperty);


        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String emailBody = "Event: User " + seller.getUsername() + " deleted property " + oldProperty.getTitle() + "\n" + "Type: DELETED\n" + "Occured At: " + time;

        String eventData = seller.getEmail() + "|" + emailBody;
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, eventData);
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