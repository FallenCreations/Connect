/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.services.twilio;

import codes.goblom.connect.ConnectPlugin;
import codes.goblom.connect.api.PhoneNumber;
import com.twilio.sdk.TwilioRestClient;
import java.util.Map;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.json.simple.JSONObject;

/**
 *
 * @author Goblom
 */
public class TwilioConfig {
        
    private final String authToken, sid;
    private final ConfigurationSection twilioConfig;
    protected final PhoneNumber number;
    protected final int port;
    
    @Getter
    private final JSONObject otherInfo = new JSONObject();
    
    private boolean hasConnected = false;
    
    TwilioConfig(ConnectPlugin plugin) {        
        this.twilioConfig = plugin.getConfig().getConfigurationSection("Twilio");
        this.authToken = twilioConfig.getString("auth_token");
        this.sid = twilioConfig.getString("sid");
        this.number = PhoneNumber.fromString(twilioConfig.getString("send_from"));
        this.port = twilioConfig.getInt("incoming_callback_port", 10050);
        
        if (this.number == null) {
            throw new Error("Unable to parse given phone number");
        }
        
        twilioConfig.getValues(true).forEach((name, value) -> {
            if (!(name.equals("auth_token") || name.equals("sid"))) {
                if (value instanceof Map) {
                    try {
                        otherInfo.put(name, mapToJson((Map<String, Object>) value));
                    } catch (Exception e) { }
                } else otherInfo.put(name, value);
            }
        });
        
    }
    
    private JSONObject mapToJson(Map<String, Object> map) {
        JSONObject json = new JSONObject();
        
        map.forEach((name, value) -> {
            if (value instanceof Map) {
                try {
                    json.put(name, mapToJson((Map<String, Object>) value));
                } catch (Exception e) { }
            } else json.put(name, value);
        });
        
        return json;
    }
    
    public TwilioRestClient connect() {
        if (hasConnected) {
            throw new RuntimeException("Already made successful connection to twilio");
        }
        
        try {
            TwilioRestClient client = new TwilioRestClient(sid, authToken);
            
            if (client.getAccount().getType() != null && !client.getAccount().getType().equals("")) {
                hasConnected = true;
                return client;
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        
        return null;
    }
}
