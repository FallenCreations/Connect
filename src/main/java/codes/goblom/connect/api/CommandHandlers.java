/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api;

import codes.goblom.connect.api.command.CommandHandler;
import codes.goblom.connect.api.command.CCommand;
import codes.goblom.connect.api.command.lua.LuaCommandHandler;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Goblom
 */
public class CommandHandlers {
    private static final Map<Class<? extends CommandHandler>, CommandHandler> commandHandlers = Maps.newHashMap();
    
    public static LuaCommandHandler getLuaCommandHandler() {
        return getCommandHandler(LuaCommandHandler.class);
    }
    
    public static <T extends CommandHandler> T getCommandHandler(Class<? extends T> handlerClass) {
        return (T) commandHandlers.get(handlerClass);
    }
    
    public static void registerCommandHandler(@NonNull Class<? extends CommandHandler> commandHandler) {
        try {
            registerCommandHandler(commandHandler, commandHandler.getConstructor(JavaPlugin.class).newInstance(JavaPlugin.getProvidingPlugin(commandHandler)));
        } catch (Exception e) {
            throw new Error(String.format("Unable to register CommandHandler '%s' with error: ", commandHandler.toString()), e);
        }
    }
    
    public static void registerCommandHandler(@NonNull Class<? extends CommandHandler> commandHandlerClass, @NonNull CommandHandler handler) {
        if (commandHandlers.containsKey(commandHandlerClass)) {
            throw new Error(String.format("CommandHandler '%s has' already been registered", commandHandlerClass.toString()));
        }
        
        commandHandlers.put(commandHandlerClass, handler);
        handler.loadCommands();
    }
    
    public static CCommand getCommand(String name) {
        for (CommandHandler handlers : commandHandlers.values()) {
            if (handlers.hasCommand(name)) {
                return handlers.getCommand(name);
            }
        }
        
        return null;
    }
    
    public static List<CommandHandler> getCommandHandlers() {
        Collection<CommandHandler> handlers = commandHandlers.values();
        return Arrays.asList(handlers.toArray(new CommandHandler[handlers.size()]));
    }
}
