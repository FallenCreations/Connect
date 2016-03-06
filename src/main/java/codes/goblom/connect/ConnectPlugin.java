/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect;

import codes.goblom.connect.api.ServiceProvider;
import codes.goblom.connect.api.SMSService;
import codes.goblom.connect.api.PhoneNumber;
import codes.goblom.connect.api.TextMessage;
import codes.goblom.connect.api.events.SMSIncomingEvent;
import codes.goblom.connect.api.CommandHandlers;
import codes.goblom.connect.api.command.CCommand;
import codes.goblom.connect.api.command.lua.LuaCommandHandler;
import codes.goblom.connect.libs.ServiceConnectTask;
import codes.goblom.connect.services.twilio.TwilioService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Goblom
 */
public class ConnectPlugin extends JavaPlugin implements SMSService {
    
    @Getter
    private SMSService service;
    private TextMessageQueue messageQueue;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("variables.lua", false);
        
        ServiceProvider.registerService(TwilioService.class);
                
        try {
            connect(this);
        } catch (Exception e) {
            throw new Error("If there is an error something really fucked up", e);
        }
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("message_queue.send_remaining_on_disable")) {
            messageQueue.sendRemaining();
        }
    }
    
    @Override
    public void connect(ConnectPlugin plugin) throws Exception {
        if (this.service != null) {
            throw new Error("Already connected to message service");
        }
        
//        String smsService = getConfig().getString("service");
//        
//        if (ServiceProvider.isRegistered(smsService)) {
//            Class<? extends SMSService> clazz = ServiceProvider.getSMSService(smsService);
//            this.service = clazz.newInstance();
//            this.service.connect(plugin);
//        }
        
//        this.service = new TwilioService();
//        this.service.connect(plugin);
        
        ConnectTask task = new ConnectTask();
        new BukkitRunnable() {

            @Override
            public void run() {
                if (service != null) { // Saftey check
                    cancel();
                    return;
                }
                
                int tries = 10;
                Exception error = null;
                
                while (service == null && tries-- > 0) {
                    try {
                        service = task.run();
                    } catch (Exception e) { 
                        error = e;
                    }
                }
                
                if (service != null) {
                    try {
                        service.connect(ConnectPlugin.this);
                        task.onComplete();
                    } catch (Exception e) {
                        error = e;
                    }
                }
                
                if (error != null) {
                    error.printStackTrace();
                }
                
                cancel();
                
                if (service == null) {
                    getLogger().severe("We were unable to load the SMSService. Plugin disabling.");
                    Bukkit.getPluginManager().disablePlugin(ConnectPlugin.this);
                }
            }
        }.runTaskTimer(this, 10, 10);
    }

    @Override
    public void sendTextMessage(PhoneNumber to, String messageBody) throws Exception {
        if (getConfig().getBoolean("message_queue.enabled")) {
            messageQueue.add(new TextMessage(to, messageBody));
        } else service.sendTextMessage(to, messageBody);
    }
    
    @Override
    public void sendTextMessages(PhoneNumber to, String[] messages) throws Exception {
//        if (getConfig().getBoolean("message_queue.enabled")) {
//            for (String message : messages) {
//                messageQueue.add(new TextMessage(to, message));
//            }
//        } else {
//            service.sendTextMessages(to, messages);
//        }
        
        for (String message : messages) {
            sendTextMessage(to, message);
        }
    }
    
    @Override
    public void close() {
        service.close();
    }
    
    @EventHandler
    protected void onTextMessageIncoming(SMSIncomingEvent event) {
        getLogger().severe("Incoming Message: " + event.getMessageBody());
        if (event.isCancelled()) return;
        
        if (event.getMessageBody().startsWith("/")) {
            String command = event.getMessageBody();
            
            CCommand cmd = CommandHandlers.getCommand(command.replace("/", ""));
            
            cmd.execute(event.getNumberInvolved(), event.getMessageBody().substring(command.length()).split(" "));
        }
    }
    
    class ConnectTask implements ServiceConnectTask {

        @Override
        public SMSService run() throws Exception {
            String smsService = getConfig().getString("service");
        
            if (ServiceProvider.isRegistered(smsService)) {
                Class<? extends SMSService> clazz = ServiceProvider.getSMSService(smsService);
                
                return clazz.newInstance();
            }
            
            return null;
        }

        @Override
        public void onComplete() {
            CommandHandlers.registerCommandHandler(LuaCommandHandler.class, new LuaCommandHandler(ConnectPlugin.this));
            ConnectPlugin.this.messageQueue = new TextMessageQueue(ConnectPlugin.this.service);
            Bukkit.getScheduler().runTaskTimerAsynchronously(ConnectPlugin.this, messageQueue, 20 * getConfig().getInt("message_queue.interval", 60), 0);
        }
        
    }
}
