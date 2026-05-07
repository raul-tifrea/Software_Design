package com.microservices.property_service.property.cqrs.command;

import com.microservices.property_service.property.Property;
import com.microservices.property_service.property.PropertyRepository;
import com.microservices.property_service.property.UserDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PropertyCommandService {

    private final PropertyRepository repository;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;


    private static final String QUEUE_NAME = "myQueue";

    public PropertyCommandService(PropertyRepository repository, RestTemplate restTemplate, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    private UserDto fetchAndValidateUser(Integer sellerId) {
        // HTTP Call to the user-service!
        UserDto user = restTemplate.getForObject("http://localhost:8082/api/users/" + sellerId, UserDto.class);
        if (user == null) throw new RuntimeException("User not found");

        if (!user.getRoleName().equals("ADMIN") && !user.getRoleName().equals("SELLER_BUYER")) {
            throw new RuntimeException("Invalid role");
        }
        return user;
    }

    private void sendRabbitMQEvent(UserDto user, String action, String propertyTitle) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String emailBody = "Event: User " + user.getUsername() + " " + action + " property " + propertyTitle + "\n" + "Occured At: " + time;
        String eventData = user.getEmail() + "|" + emailBody;
        rabbitTemplate.convertAndSend(QUEUE_NAME, eventData);
    }

    public Property createProperty(Property property, Integer sellerId) {
        UserDto user = fetchAndValidateUser(sellerId);

        Command<Property> command = new CreatePropertyCommand(property, sellerId, repository);
        Property savedProperty = command.execute();

        sendRabbitMQEvent(user, "CREATED", savedProperty.getTitle());
        return savedProperty;
    }

    public Property updateProperty(Integer propertyId, Property property, Integer sellerId) {
        UserDto user = fetchAndValidateUser(sellerId);

        Command<Property> command = new UpdatePropertyCommand(propertyId, property, sellerId, repository);
        Property updatedProperty = command.execute();

        sendRabbitMQEvent(user, "UPDATED", updatedProperty.getTitle());
        return updatedProperty;
    }

    public void deleteProperty(Integer propertyId, Integer sellerId) {
        UserDto user = fetchAndValidateUser(sellerId);

        Property oldProperty = repository.findById(propertyId).orElseThrow();

        Command<Void> command = new DeletePropertyCommand(propertyId, sellerId, repository);
        command.execute();

        sendRabbitMQEvent(user, "DELETED", oldProperty.getTitle());
    }
}