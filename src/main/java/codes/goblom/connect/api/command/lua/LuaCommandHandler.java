/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.command.lua;

import codes.goblom.connect.ConnectPlugin;
import codes.goblom.connect.api.Contact;
import codes.goblom.connect.api.command.CommandHandler;
import java.io.File;
import java.util.Locale;

/**
 *
 * @author Goblom
 */
public class LuaCommandHandler extends CommandHandler<LuaCommand> {
    
    public LuaCommandHandler(ConnectPlugin plugin) {
        super(plugin);
    }

    @Override
    public void executeCommand(LuaCommand command, Contact contact, String[] args) {
        command.execute(contact, args);
    }

    @Override
    public void loadCommands() {
        for (File f : getDataFolder().listFiles((File pathname) -> pathname.getName().toLowerCase(Locale.ENGLISH).endsWith(".lua"))) {
            try {
                registerCommand(new LuaCommand(this, f));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
//        registerCommand(new LuaCommand(this, new File("example.lua")));
    }
    
}
