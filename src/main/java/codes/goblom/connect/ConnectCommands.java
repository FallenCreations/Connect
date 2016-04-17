/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect;

import codes.goblom.connect.api.Contact;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.fallencreations.borklibs.executor.Command;
import net.fallencreations.borklibs.executor.CommandHandler;
import net.fallencreations.borklibs.executor.CommandListener;
import org.bukkit.ChatColor;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public class ConnectCommands implements CommandListener {
    
    private final ConnectPlugin plugin;
    
    @Command (
            alias = { "m", "sendmessage", "sm" },
            description = "Send message with the service from the contact",
            usage = "[contact] [message]",
            permissions = "messages.send", 
            minArgs = 2,
            allowConsole = true,
            help = {
                "[contact] -- The contact to send a message to '+[number]'",
                "[message] -- The message to send to the [contact]"
            }
    )
    public void message(final CommandHandler handler) {
        String strContact = handler.getArg(0);
        String message = handler.combine(1, ' ');
        
        Contact contact = Contact.convertFromString(strContact);
        
        if (contact == null) {
            handler.reply(ChatColor.RED + "Error: No service is registered to handle that contact info.");
        } else {
            try {
                contact.sendMessage(message);
            } catch (Exception e) {
                handler.reply(ChatColor.RED + "Error: %s", e.getMessage());
            }
        }
    }
    
    @Command (
        alias = "vl",
        description = "View most recent messages sent & recieved",
        usage = "(view back how far)",
        permissions = "messages.view",
        minArgs = 0,
        allowConsole = true
    )
    public void viewLog(final CommandHandler handler) {
        int amount = 0; 
        try {
            amount = Integer.valueOf(handler.getArg(0));
        } catch (Exception e) { }
        
        List<String> log = plugin.messageLogger.genMostRecent(amount);
        handler.setPrefix("[Connect] [Log]");
        
        if (log == null || log.isEmpty()) {
            handler.reply(ChatColor.RED + "No logs to report");
        } else {
            log.forEach((string) -> handler.reply(string));
        }
    }
}
