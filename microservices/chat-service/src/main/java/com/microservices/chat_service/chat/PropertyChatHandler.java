package com.microservices.chat_service.chat;


import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PropertyChatHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;

    public PropertyChatHandler(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    private final ConcurrentHashMap<String, List<WebSocketSession>> propertySessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getPropertyId(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String propertyId = getPropertyId(session);
        propertySessions.computeIfAbsent(propertyId, k -> new CopyOnWriteArrayList<>()).add(session);
        System.out.println("Client " + session.getId() + " connected to property " + propertyId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String propertyId = getPropertyId(session);
        String payload = message.getPayload();

        ChatMessage chatMessage;
        try {
            chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        } catch (Exception e) {
            System.out.println("Could not parse message: " + payload);
            return;
        }

        if (chatMessage.getTimestamp() == null || chatMessage.getTimestamp().isBlank()) {
            chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        chatMessage.setPropertyId(propertyId);
        chatMessageRepository.save(chatMessage);

        String broadcastPayload = objectMapper.writeValueAsString(chatMessage);
        TextMessage broadcastMessage = new TextMessage(broadcastPayload);

        List<WebSocketSession> sessions = propertySessions.get(propertyId);
        if (sessions != null) {
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    s.sendMessage(broadcastMessage);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String propertyId = getPropertyId(session);
        List<WebSocketSession> sessions = propertySessions.get(propertyId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                propertySessions.remove(propertyId);
            }
        }
        System.out.println("Client " + session.getId() + " disconnected from property " + propertyId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.out.println("Transport error for session " + session.getId() + ": " + exception.getMessage());
        afterConnectionClosed(session, CloseStatus.SERVER_ERROR);
    }
}
