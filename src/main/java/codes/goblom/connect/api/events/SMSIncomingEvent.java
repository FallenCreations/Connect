/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.events;

import codes.goblom.connect.api.PhoneNumber;
import codes.goblom.connect.api.SMSService;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Goblom
 */
public class SMSIncomingEvent extends SMSEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Map<String, String> rawData;
    
    @Getter @Setter
    private boolean cancelled;
    
    public SMSIncomingEvent(SMSService service, Map<String, String> rawData) {
        super(service, PhoneNumber.fromString(rawData.get("From")), rawData.get("Body"));
        
        this.rawData = rawData;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
