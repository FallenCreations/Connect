/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect;

import codes.goblom.connect.api.SMSService;
import codes.goblom.connect.api.TextMessage;
import java.util.LinkedList;

/**
 *
 * @author Goblom
 */
public class TextMessageQueue implements Runnable {
    
    private final SMSService service;
    private final LinkedList<TextMessage> queue = new LinkedList();
    
    protected TextMessageQueue(SMSService service) {
        this.service = service;
    }
    
    public void add(TextMessage message) {
        queue.addLast(message);
    }
    
    @Override
    public void run() {
        TextMessage txt = queue.pollFirst();
        
        if (txt == null) return;
        
        try {
            service.sendTextMessage(txt.getToNumber(), txt.getMessageBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendRemaining() {
        while (!queue.isEmpty()) {
            run();
        }
    }
}
