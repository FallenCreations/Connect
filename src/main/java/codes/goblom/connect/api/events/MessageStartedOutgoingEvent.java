/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.events;

import codes.goblom.connect.api.Contact;
import codes.goblom.connect.api.SMSService;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Goblom
 */
public class MessageStartedOutgoingEvent extends MessageEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Getter
    private final boolean mediaMessage;
    
    @Getter
    @Setter
    private boolean cancelled;
    
    public MessageStartedOutgoingEvent(SMSService service, Contact contact, String messageBody) {
        super(service, contact, messageBody);
        
        URL url = null;
        try {
            url = new URL(messageBody);
        } catch (Exception e) { }
        
        this.mediaMessage = url != null;
    }
}
