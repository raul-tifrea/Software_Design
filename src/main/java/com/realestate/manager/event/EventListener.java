package com.realestate.manager.event;

import org.springframework.security.core.userdetails.UserDetails;

public interface EventListener {
    void handleEvent(String eventType, Object data);
}
