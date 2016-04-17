/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect;

import codes.goblom.connect.api.Contact;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;

/**
 *
 * @author Goblom
 */
class ConnectLog {
    
    private LinkedList<MessageLog> log = new LinkedList();
    
    void append(String serviceName, Contact contact, String message, boolean incoming) {
        log.addFirst(new MessageLog(serviceName, contact, message, incoming));
    }
    
    public List<String> genMostRecent(int max) {
        List<String> l = new ArrayList();
        max = max == 0 ? log.size() : max;
        
        for (int i = 0; i <= max; i++) {
            MessageLog ml = log.get(i);
            l.add(ml.toString());
        }
        
        return l;
    }
    
    @AllArgsConstructor
    class MessageLog {
        String service;
        Contact contact;
        String message;
        boolean incoming;
        
        public String toString() {
            StringBuilder sb = new StringBuilder();
            
            sb.append("{").append(service).append("} ");
            sb.append(contact.parse()).append(incoming ? " -> " : " <- ");
            sb.append(message);
            
            return sb.toString();
        }
    }
}
