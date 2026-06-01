package com.microservices.chat_service.chat;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;

    public ChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping("/api/chat/{propertyId}")
    public List<ChatMessage> getChatHistory(@PathVariable String propertyId) {
        return chatMessageRepository.findByPropertyIdOrderByTimestampAsc(propertyId);
    }
}
