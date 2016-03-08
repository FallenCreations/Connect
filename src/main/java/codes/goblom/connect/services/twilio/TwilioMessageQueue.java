/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.services.twilio;

import java.util.LinkedList;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public class TwilioMessageQueue implements Runnable {
    
    private final LinkedList<TextMessage> queue = new LinkedList();
    private final TwilioService service;
    
    public void add(TextMessage message) {
        queue.addLast(message);
    }
    
    @Override
    public void run() {
        TextMessage txt = queue.pollFirst();
        
        if (txt == null) return;
        
        try {
            service.sendMessage(true, txt.getTo(), txt.getMessageBody());
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
