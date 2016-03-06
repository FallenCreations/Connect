/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Goblom
 */
public class TextMessage {
    
    @Getter
    protected final PhoneNumber toNumber;
    
    @Getter
    @Setter
    protected String messageBody;
    
    public TextMessage(PhoneNumber to, String message) {
        this.toNumber = to;
        setMessageBody(message);
    }
}
