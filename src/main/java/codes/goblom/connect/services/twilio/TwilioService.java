/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.services.twilio;

import codes.goblom.connect.ConnectPlugin;
import codes.goblom.connect.api.SMSService;
import codes.goblom.connect.api.PhoneNumber;
import codes.goblom.connect.api.ServiceName;
import codes.goblom.connect.api.events.SMSOutgoingEvent;
import com.google.common.collect.Lists;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.instance.Message;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bukkit.Bukkit;

/**
 *
 * @author Goblom
 */
@ServiceName( "Twilio" )
public class TwilioService implements SMSService {

    private static boolean STARTED = false;
    
    @Getter
    private String name = "Twilio";
    
    private TwilioRestClient twilio;
    private TwilioCallback callback;
    private TwilioConfig config;
    private PhoneNumber myNumber;
    
    @Override
    public void connect(ConnectPlugin plugin) {
        if (STARTED) return; // TODO: Otherwise it loops. Need to find loop point
        this.config = new TwilioConfig(plugin);
        this.callback = new TwilioCallback(plugin, this, config.port);
        
        if (callback.start0()) {
            this.twilio = config.connect();
        } else throw new Error("Unable to load TwilioService. Please check all configurations.");
        
        this.myNumber = config.number;
        STARTED = true;
    }
    
    @Override
    public void sendTextMessage(PhoneNumber to, String messageBody) throws Exception {
        List<NameValuePair> list = Lists.newArrayList();
        list.add(new BasicNameValuePair("To", to.toNumberString()));
        list.add(new BasicNameValuePair("From", myNumber.toNumberString()));
        
        try {
            new URL(messageBody);
            list.add(new BasicNameValuePair("MediaURL", messageBody));
        } catch (Exception e) { 
            list.add(new BasicNameValuePair("Body", messageBody));
        }
        
        Message message = twilio.getAccount().getMessageFactory().create(list);
        Bukkit.getPluginManager().callEvent(new SMSOutgoingEvent(this, to, messageBody, message));
    }    

    @Override
    public void sendTextMessages(PhoneNumber to, String[] messageBodies) throws Exception {
        Arrays.asList(messageBodies).forEach((message) -> {
            try {
                sendTextMessage(to, message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    @Override
    public void close() {
        this.config = null;
        this.myNumber = null;
        this.twilio = null;
        this.callback.stop();
        this.callback = null;
    }
}
