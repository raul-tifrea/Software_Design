package com.microservices.property_service.property.cqrs.command;

public interface Command<T> {
    T execute();
}