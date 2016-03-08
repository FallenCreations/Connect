/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect;

import codes.goblom.connect.api.ServiceProvider;
import codes.goblom.connect.api.SMSService;
import codes.goblom.connect.api.events.SMSIncomingEvent;
import codes.goblom.connect.api.CommandHandlers;
import codes.goblom.connect.api.Contact;
import codes.goblom.connect.api.command.CCommand;
import codes.goblom.connect.api.command.lua.LuaCommandHandler;
import codes.goblom.connect.libs.ServiceConnectTask;
import codes.goblom.connect.services.twilio.TwilioService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Goblom
 */
public class ConnectPlugin extends JavaPlugin implements SMSService, Listener {
    
    @Getter
    private SMSService service;
    
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
        ServiceProvider.getServices().forEach((service) -> service.close());
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
    public void sendMessage(Contact to, String messageBody) throws Exception {
        service.sendMessage(to, messageBody);
    }
    
    @Override
    public void sendMessages(Contact to, String[] messages) throws Exception {        
        for (String message : messages) {
            sendMessage(to, message);
        }
    }
    
    @Override
    public void close() {
        service.close();
    }
    
    @EventHandler
    protected void onMessageIncoming(SMSIncomingEvent event) {
        getLogger().severe("Incoming Message: " + event.getMessageBody());
        if (event.isCancelled()) return;
        
        if (event.getMessageBody().startsWith("/")) {
            String command = event.getMessageBody().split(" ")[0].replace("/", "");
            
            CCommand cmd = CommandHandlers.getCommand(command);
            
            if (cmd == null) return;
            cmd.execute(event.getContactInvolved(), event.getMessageBody().substring(command.length() + 1).trim().split(" "));
        }
    }
    
    class ConnectTask implements ServiceConnectTask {

        @Override
        public SMSService run() throws Exception {
            String smsService = getConfig().getString("default-service");
        
            if (ServiceProvider.isRegistered(smsService)) {
                Class<? extends SMSService> clazz = ServiceProvider.getSMSServiceClass(smsService);
                
                return clazz.newInstance();
            }
            
            return null;
        }

        @Override
        public void onComplete() {
            ConnectPlugin.this.service.done();
            CommandHandlers.registerCommandHandler(LuaCommandHandler.class, new LuaCommandHandler(ConnectPlugin.this));
            Bukkit.getPluginManager().registerEvents(ConnectPlugin.this, ConnectPlugin.this);
        }
        
    }
}
