/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.libs;

import codes.goblom.connect.api.SMSService;

/**
 *
 * @author Goblom
 */
public interface ServiceConnectTask {
    
    public SMSService run() throws Exception;
    
    public void onComplete();
}
