/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api;

import codes.goblom.connect.ConnectPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Goblom
 */
public interface SMSService {
    
    /**
     * Do NOT override this method
     */
    default void done() {
        if (!ServiceProvider.INSTANCE.containsKey(getClass())) {
            ServiceProvider.INSTANCE.put(getClass(), this);
        }
    }
    
    public void connect(ConnectPlugin plugin) throws Exception;
    
    public void sendMessage(Contact contact, String messageBody) throws Exception;
    
    public void sendMessages(Contact contact, String[] messageBodies) throws Exception;
    
    public void close();
    
    public default SMSService getService() {
        return this;
    }
    
//    public SMSIncomingEvent createIncomingEvent(Object rawData);
    
    /**
     * So that all the SMSService config values all show up in Connects config.yml for easy management 
     */
    public default <T> T getConfigOption(String key, T def) {
        JavaPlugin plugin = JavaPlugin.getPlugin(ConnectPlugin.class);
        FileConfiguration config = plugin.getConfig();
        
        if (!config.contains(key)) {
            config.set(key, def);
            plugin.saveConfig();
            return def;
        }
        
        return (T) config.get(key);
    }
}
