package com.realestate.manager.notification;

import com.realestate.manager.event.EventBroker;
import com.realestate.manager.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements EventListener {
    public NotificationService(EventBroker eventBroker) {
        eventBroker.subscribe("Property_Created", this);
        eventBroker.subscribe("Property_Updated", this);
        eventBroker.subscribe("Property_Deleted", this);
    }

    @Override
    public void handleEvent(String eventType, Object data) {
        System.out.println("Notification_Service; Notif Sent!");
        System.out.println("Event type: " + eventType);
        System.out.println("Event data: " + data.toString());
    }
}
