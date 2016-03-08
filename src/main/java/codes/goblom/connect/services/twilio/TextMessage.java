/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.services.twilio;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Goblom
 */
public class TextMessage {
    
    @Getter
    protected final PhoneNumber to;
    
    @Getter
    @Setter
    protected String messageBody;
    
    public TextMessage(PhoneNumber to, String message) {
        this.to = to;
        setMessageBody(message);
    }
}
