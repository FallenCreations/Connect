/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.services.twilio;

import codes.goblom.connect.api.events.MessageIncomingEvent;
import java.util.Map;
import lombok.Getter;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Goblom
 */
public class TwilioIncomingEvent extends MessageIncomingEvent {
    
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Getter
    private final Map<String, String> rawData;
    
    protected TwilioIncomingEvent(TwilioService service, Map<String, String> rawData) {
        super(service, PhoneNumber.fromString(rawData.get("From")), rawData.get("Body"));
        
        this.rawData = rawData;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
