/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api;

import lombok.Getter;

/**
 *
 * @author Goblom
 */
public abstract class Contact<T extends SMSService> {
    
    @Getter
    private final T service;
    
    protected Contact() {
        RequiredService rs = getClass().getAnnotation(RequiredService.class);
        
        if (rs == null || rs.value() == null) {
            throw new RuntimeException("Contact is missing required @RequiredService tag, please message the developer.");
        }
        
        if (ServiceProvider.isRegistered(rs.value())) {
            this.service = (T) ServiceProvider.getSMSServiceInstance(rs.value());
        } else {
            throw new RuntimeException("ServiceProvider [" + rs.value().getSimpleName() + "] is not registered");
        }
    }
    
    public final void reply(String message) {
        try {
            sendMessage(message);
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public final void reply(String... messages) {
        try {
            sendMessages(messages);
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public final void sendMessage(String messageBody) throws Exception {
        getService().sendMessage(this, messageBody);
    }
    
    public final void sendMessages(String... messages) throws Exception {
        getService().sendMessages(this, messages);
    }
    
    public abstract String parse();
}
