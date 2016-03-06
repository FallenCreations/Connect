/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.events;

import codes.goblom.connect.api.PhoneNumber;
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
     * This is not the number you have specified in the config.
     * This is the number that it is sending to or receiving from;
     */
    @Getter
    private final PhoneNumber numberInvolved;
    
    public SMSEvent(SMSService service, PhoneNumber number, String messageBody) {
        super(service);
        
        this.messageBody = messageBody;
        this.numberInvolved = number;
    }
}
