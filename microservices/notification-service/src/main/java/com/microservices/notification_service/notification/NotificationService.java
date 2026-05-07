package com.microservices.notification_service.notification;

import com.realestate.manager.event.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handlePropertyEvent(String message) {
        try {

            String[] parts = message.split("\\|", 2);


            if (parts.length < 2) {
                System.out.println("Invalid message format received: " + message);
                return;
            }

            String userEmail = parts[0];
            String emailBody = parts[1];


            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(userEmail);
            mail.setSubject("Real Estate Manager - Activity Alert");
            mail.setText(emailBody);


            mailSender.send(mail);

            System.out.println("Email delivered to: " + userEmail);

        } catch (Exception e) {
            System.out.println("Could not send email: " + e.getMessage());
        }
    }
}