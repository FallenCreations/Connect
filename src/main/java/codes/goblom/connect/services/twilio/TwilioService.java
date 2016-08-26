/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.services.twilio;

import codes.goblom.connect.ConnectPlugin;
import codes.goblom.connect.api.Contact;
import codes.goblom.connect.api.RequiredService;
import codes.goblom.connect.api.ConnectService;
import codes.goblom.connect.api.ServiceName;
import codes.goblom.connect.api.events.MessageFinishedOutgoingEvent;
import codes.goblom.connect.api.events.MessageStartedOutgoingEvent;
import com.google.common.collect.Lists;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.instance.Message;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bukkit.Bukkit;

/**
 *
 * @author Goblom
 */
@ServiceName( "Twilio" )
public class TwilioService implements ConnectService, Contact.StringToContact {

    private static boolean STARTED = false;
    
    private TwilioRestClient twilio;
    private TwilioCallback callback;
    private TwilioConfig config;
    private TwilioMessageQueue queue;
    private PhoneNumber myNumber;
    
    @Override
    public void connect(ConnectPlugin plugin) {
        if (STARTED) return; // TODO: Otherwise it loops. Need to find loop point
        this.config = new TwilioConfig(plugin);
        this.callback = new TwilioCallback(plugin, this, config.port);
        this.queue = new TwilioMessageQueue(this);
        
        if (callback.start0()) {
            this.twilio = config.connect();
        } else throw new Error("Unable to load TwilioService. Please check all configurations.");
        
        this.myNumber = config.number;
        queue.runTaskTimerAsynchronously(plugin, 0, 20 * config.getMessageQueueOption("interval", 60));
        Contact.registerConverter(this);
        STARTED = true;
    }

    private void validateContact(Contact to) {
        RequiredService rs = to.getClass().getAnnotation(RequiredService.class);
        if (rs == null || rs.value() == null) {
            throw new RuntimeException("Contact is missing required @RequiredService tag, please message the developer.");
        }

        if (rs.value() != getClass()) {
            throw new RuntimeException("This contact is not compatible with this service. Please try another server or use Contact#sendMessage(String)");
        }
    }

    @Override
    public void sendMessage(Contact to, String messageBody) throws Exception {
        validateContact(to);
        sendMessage(false, (PhoneNumber) to, messageBody);
    }    

    @Override
    public void sendMessages(Contact to, String[] messageBodies) throws Exception {
        validateContact(to);
        Arrays.asList(messageBodies).forEach((message) -> {
            try {
                sendMessage(to, message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    protected void sendMessage(boolean bypass, PhoneNumber contact, String messageBody) throws Exception {
        if (!bypass) {
            if (config.getMessageQueueOption("enabled", true)) {
               queue.add(new TextMessage(contact, messageBody));
               return;
            }
        }
        
        MessageStartedOutgoingEvent msoe = new MessageStartedOutgoingEvent(this, contact, messageBody);
        Bukkit.getPluginManager().callEvent(msoe);
        
        if (msoe.isCancelled()) return;
        
        List<NameValuePair> list = Lists.newArrayList();
        list.add(new BasicNameValuePair("To", contact.parse()));
        list.add(new BasicNameValuePair("From", myNumber.toNumberString()));
        
        try {
            new URL(msoe.getMessageBody());
            list.add(new BasicNameValuePair("MediaURL", msoe.getMessageBody()));
        } catch (Exception e) { 
            list.add(new BasicNameValuePair("Body", msoe.getMessageBody()));
        }
        
        Message message = twilio.getAccount().getMessageFactory().create(list);
        Bukkit.getPluginManager().callEvent(new MessageFinishedOutgoingEvent(this, contact, messageBody, message));
    }
    
    @Override
    public void close() {
        if (config.getMessageQueueOption("send_remaining_on_disable", false)) {
            queue.sendRemaining();
        }
        
        this.queue.cancel();
        this.config = null;
        this.myNumber = null;
        this.twilio = null;
        this.callback.stop();
        this.callback = null;
        this.queue = null;
    }

    @Override
    public boolean isType(String str) {
        return str.startsWith("+");
    }

    @Override
    public Contact convert(String str) {
        return PhoneNumber.fromString(str);
    }
}
