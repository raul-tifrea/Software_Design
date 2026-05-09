package com.microservices.property_service.property.cqrs.command;

import com.microservices.property_service.property.Property;
import com.microservices.property_service.property.PropertyRepository;
import com.microservices.property_service.property.UserDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PropertyCommandService {

    private final PropertyRepository repository;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.notification.queue}")
    private String queueName;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public PropertyCommandService(PropertyRepository repository, RestTemplate restTemplate,
            RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    private UserDto fetchAndValidateUser(Integer sellerId) {

        UserDto user = restTemplate.getForObject(userServiceUrl + "/api/users/" + sellerId, UserDto.class);
        if (user == null)
            throw new RuntimeException("User not found");

        user.setId(sellerId);

        if (!user.getRoleName().equals("ADMIN") && !user.getRoleName().equals("SELLER_BUYER")) {
            throw new RuntimeException("Invalid role");
        }
        return user;
    }

    private void sendRabbitMQEvent(UserDto user, String action, String propertyTitle) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String emailBody = "Event: User " + user.getUsername() + " " + action + " property " + propertyTitle + "\n"
                + "Occured At: " + time;
        String eventData = user.getEmail() + "|" + emailBody;
        rabbitTemplate.convertAndSend(queueName, eventData);
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

        Command<Property> command = new UpdatePropertyCommand(propertyId, property, user, repository);
        Property updatedProperty = command.execute();

        sendRabbitMQEvent(user, "UPDATED", updatedProperty.getTitle());
        return updatedProperty;
    }

    public void deleteProperty(Integer propertyId, Integer sellerId) {
        UserDto user = fetchAndValidateUser(sellerId);

        Command<Property> command = new DeletePropertyCommand(propertyId, user, repository);

        Property deletedProperty = command.execute();
        sendRabbitMQEvent(user, "DELETED", deletedProperty.getTitle());
    }
}