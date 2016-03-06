/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.events;

import codes.goblom.connect.api.SMSService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;

/**
 *
 * @author Goblom
 */
public abstract class ConnectEvent extends Event {
    
    @Getter
    private final SMSService service;
    
    public ConnectEvent(SMSService service) {
        this.service = service;
    }
}
