/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect;

import codes.goblom.connect.api.ServiceProvider;
import codes.goblom.connect.api.ConnectService;
import codes.goblom.connect.api.events.MessageIncomingEvent;
import codes.goblom.connect.api.CommandHandlers;
import codes.goblom.connect.api.Contact;
import codes.goblom.connect.api.command.CCommand;
import codes.goblom.connect.api.command.lua.LuaCommandHandler;
import codes.goblom.connect.api.events.MessageFinishedOutgoingEvent;
import codes.goblom.connect.libs.ServiceConnectTask;
import codes.goblom.connect.services.twilio.TwilioService;
import java.util.logging.Level;
import lombok.Getter;
import net.fallencreations.borklibs.executor.Executor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

/**
 *
 * @author Goblom
 */
public class ConnectPlugin extends JavaPlugin implements ConnectService, Listener {
    
    @Getter
    private ConnectService service;
    
    protected ConnectLog messageLogger = new ConnectLog();
    private Executor commandExecutor;
    private boolean debugMessages = false;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("variables.lua", false);
        
        ServiceProvider.registerService(TwilioService.class);
        this.debugMessages = getConfig().getBoolean("enabled-debug-messages", false);
        
        try {
            connect(this);
        } catch (Exception e) {
            throw new Error("If there is an error something really fucked up", e);
        }
        
        commandExecutor = new Executor("[Connect]") {

            @Override
            public boolean hasPermission(CommandSender sender, String[] permissions) {
                boolean has = sender.isOp() || sender.hasPermission("connect.admin");
                
                for (String perm : permissions) {
                     if (has) break;
                     
                     has = sender.hasPermission("connect." + perm);
                }
                
                return has;
            }
        };
        
        commandExecutor.addListener(new ConnectCommands(this));
        commandExecutor.setPrintErrors(true);
        PluginCommand command = getCommand("connect");
                      command.setExecutor(commandExecutor);
                      command.setDescription("The main Connect command");
                      command.setUsage("/connect [cmd] [args]");
    }

    @Override
    public void onDisable() {
        ServiceProvider.getServices().forEach((service) -> service.close());
    }
    
    @Override
    // Connect to Default Service
    public void connect(ConnectPlugin plugin) throws Exception {
        if (this.service != null) {
            throw new Error("Already connected to message service");
        }
        
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
                    getLogger().severe("We were unable to load the default SMSService. Plugin disabling.");
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
        
        getLogger().warning("Default service has been disabled. There may be problems unless server is restarted.");
    }
    
    @EventHandler ( ignoreCancelled = true )
    protected void onMessageOutgoing(MessageFinishedOutgoingEvent event) {
        if (debugMessages) {
        JSONObject debug = new JSONObject();
                   debug.put("Type", "OUTGOING");
                   debug.put("Message", event.getMessageBody());
                   debug.put("To", event.getContactInvolved().parse());
        getLogger().log(Level.INFO, "Debug: {0}", debug);
        }
        messageLogger.append(ServiceProvider.getServiceName(event.getService().getClass()),event.getContactInvolved(), event.getMessageBody(), false);
    }
    
    @EventHandler( priority = EventPriority.LOWEST, ignoreCancelled = true )
    protected void onMessageIncoming(MessageIncomingEvent event) {
        if (debugMessages){
            JSONObject debug = new JSONObject();
                       debug.put("Type", "INCOMING");
                       debug.put("Message", event.getMessageBody());
                       debug.put("From", event.getContactInvolved().parse());
            getLogger().log(Level.INFO, "Debug: {0}", debug);
        }
        messageLogger.append(ServiceProvider.getServiceName(event.getService().getClass()), event.getContactInvolved(), event.getMessageBody(), true);
        
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
        public ConnectService run() throws Exception {
            String smsService = getConfig().getString("default-service");
        
            if (ServiceProvider.isRegistered(smsService)) {
                Class<? extends ConnectService> clazz = ServiceProvider.getSMSServiceClass(smsService);
                
                return clazz.newInstance();
            }
            
            return null;
        }

        @Override
        public void onComplete() {
            ConnectPlugin.this.service.done();
            CommandHandlers.registerCommandHandler(LuaCommandHandler.class, new LuaCommandHandler(ConnectPlugin.this));
            Bukkit.getPluginManager().registerEvents(ConnectPlugin.this, ConnectPlugin.this);
            
            ServiceProvider.finishLoading(ConnectPlugin.this);
        }
        
    }
}
