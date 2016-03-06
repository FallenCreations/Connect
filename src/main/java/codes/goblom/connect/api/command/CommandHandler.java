/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.command;

import codes.goblom.connect.api.PhoneNumber;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public abstract class CommandHandler<T extends CCommand> {
    
    private final JavaPlugin plugin;
    private final Map<String, T> registeredCommands = Maps.newHashMap();
    
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
    
    public boolean hasCommand(String name) {
        for (String command : registeredCommands.keySet()) {
            if (name.equalsIgnoreCase(command)) {
                return true;
            }
        }
        
        return false;
    }

    public T getCommand(String name) {
        for (String command : registeredCommands.keySet()) {
            if (name.equalsIgnoreCase(command)) {
                return registeredCommands.get(name);
            }
        }
        
        return null;
    }
    
    public final void registerCommand(T command) {
        for (String name : command.getNames()) {
            registeredCommands.put(name, command);
        }
    }
    
    public final List<String> getCommands() {
        List<String> names = Lists.newArrayList();
        
        for (CCommand cmd : registeredCommands.values()) {
            names.add(cmd.getName());
        }
        
        return names;
    }
    
    public abstract void loadCommands();
    
    public abstract void executeCommand(T command, PhoneNumber number, String[] args);
}
