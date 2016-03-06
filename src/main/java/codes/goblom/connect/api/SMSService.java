/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api;

import codes.goblom.connect.ConnectPlugin;

/**
 *
 * @author Goblom
 */
public interface SMSService {
    
    public void connect(ConnectPlugin plugin) throws Exception;
    
    public void sendTextMessage(PhoneNumber to, String messageBody) throws Exception;
    
    public void sendTextMessages(PhoneNumber to, String[] messageBodies) throws Exception;
    
    public void close();
    
    public default SMSService getService() {
        return this;
    }
}
