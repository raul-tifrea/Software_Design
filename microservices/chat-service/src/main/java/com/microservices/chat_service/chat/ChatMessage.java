package com.microservices.chat_service.chat;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String propertyId;
    private String sender;
    private String content;
    private String timestamp;
    private String type; // CHAT or JOIN

    public ChatMessage() {}

    public ChatMessage(String propertyId, String sender, String content, String timestamp, String type) {
        this.propertyId = propertyId;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getPropertyId() {
        return propertyId;
    }
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
