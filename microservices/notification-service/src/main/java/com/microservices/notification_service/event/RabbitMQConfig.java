package com.microservices.notification_service.event;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    public static final String QUEUE_NAME = "property.events.queue";

    @Bean
    public Queue propertyEventsQueue() {

        return new Queue(QUEUE_NAME, true);
    }
}