package com.realestate.manager.event;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventBroker {
    private Map<String, List<EventListener>> listeners =  new HashMap<>();

    public void subscribe(String eventType, EventListener eventListener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(eventListener);
    }

    public void publish(String eventType, Object data) {
        if(listeners.containsKey(eventType)) {
            for(EventListener eventListener : listeners.get(eventType)) {
                eventListener.handleEvent(eventType, data);
            }
        }
    }
}
