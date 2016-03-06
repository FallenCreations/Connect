/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.services.twilio;

import codes.goblom.connect.ConnectPlugin;
import codes.goblom.connect.api.events.SMSIncomingEvent;
import codes.goblom.connect.libs.NanoHTTPD;
import codes.goblom.connect.libs.NanoHTTPD.Response.Status;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;

/**
 *
 * @author Goblom
 */
public class TwilioCallback extends NanoHTTPD {

    private final ConnectPlugin plugin;
    private final TwilioService service;
    private final int port;
    
    public TwilioCallback(ConnectPlugin plugin, TwilioService service, int port) {
        super(port);
        
        this.plugin = plugin;
        this.service = service;
        this.port = port;
    }
    
    protected boolean start0() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            plugin.getLogger().log(Level.INFO, "Started TwilioCallbackListener on port {0}", port);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Response serve(IHTTPSession session) {
        try { session.parseBody(new HashMap()); } catch (Exception e) { e.printStackTrace(); }
        Map<String, String> raw = session.getParms();
        
        if (raw == null || !raw.containsKey("SmsMessageSid")) {
            // Decide later if we should keep as it does cost money
            return newFixedLengthResponse(Status.OK, "application/xml", "<Response><Message>Error: Unable to parse message</Message></Response>");
        }
        
        SMSIncomingEvent event = new SMSIncomingEvent(service, raw);
        Bukkit.getPluginManager().callEvent(event);
        
        return newFixedLengthResponse(Status.OK, "application/xml", "<Response><Message>Recieved Message</Message></Response>");
    }
}
