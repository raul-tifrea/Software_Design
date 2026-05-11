package com.microservices.notification_service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private JavaMailSender mailSender;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        notificationService = new NotificationService(mailSender);
    }

    @Test
    void handlePropertyEvent_ValidMessage_SendsEmail() {
        String message = "test@example.com|User created property Vila";

        notificationService.handlePropertyEvent(message);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentEmail = captor.getValue();
        assertEquals("test@example.com", sentEmail.getTo()[0]);
        assertEquals("Real Estate Manager - Activity Alert", sentEmail.getSubject());
        assertEquals("User created property Vila", sentEmail.getText());
    }

    @Test
    void handlePropertyEvent_InvalidMessage_DoesNotSendEmail() {
        String message = "invalid_message_without_pipe";
        notificationService.handlePropertyEvent(message);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
    
    @Test
    void handlePropertyEvent_MailSenderThrowsException_CatchesException() {
        String message = "test@example.com|User created property Vila";
        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(SimpleMailMessage.class));
        assertDoesNotThrow(() -> notificationService.handlePropertyEvent(message));
    }
}
