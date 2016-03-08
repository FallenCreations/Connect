/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.events;

import codes.goblom.connect.api.Contact;
import codes.goblom.connect.api.SMSService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Goblom
 */
public abstract class SMSIncomingEvent extends SMSEvent implements Cancellable {
    
    @Getter
    @Setter
    private boolean cancelled;

    public SMSIncomingEvent(SMSService service, Contact contact, String messageBody) {
        super(service, contact, messageBody);
    }
    
    @Override
    public abstract HandlerList getHandlers();
}
