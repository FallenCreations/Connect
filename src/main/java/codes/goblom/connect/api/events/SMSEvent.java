/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.events;

import codes.goblom.connect.api.Contact;
import codes.goblom.connect.services.twilio.PhoneNumber;
import codes.goblom.connect.api.SMSService;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Goblom
 */
public abstract class SMSEvent extends ConnectEvent {
    
    @Getter
    @Setter
    private String messageBody;
    
    /**
     * This is not the contact you have specified in the config.
     * This is the contact that it is sending to or receiving from;
     */
    @Getter
    private final Contact contactInvolved;
    
    public SMSEvent(SMSService service, Contact contact, String messageBody) {
        super(service);
        
        this.messageBody = messageBody;
        this.contactInvolved = contact;
    }
}
