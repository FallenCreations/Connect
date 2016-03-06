/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.command;

import codes.goblom.connect.api.CommandHandlers;
import codes.goblom.connect.api.PhoneNumber;
import lombok.Getter;

/**
 *
 * @author Goblom
 */
public abstract class CCommand {
    
    @Getter
    private final CommandHandler commandHandler;
    
    public CCommand(CommandHandler handler) {
        if (CommandHandlers.getCommandHandler(handler.getClass()) == null) {
            throw new Error(String.format("CommandHandler '%s' is not registered, so unable to register command", handler.getClass().getSimpleName()));
        }
        
        this.commandHandler = handler;
    }
    
    public abstract String getName();
    
    public abstract String[] getNames();
    
    public abstract void execute(PhoneNumber number, String[] args);
}
