/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.command.lua;

import codes.goblom.connect.ConnectPlugin;
import codes.goblom.connect.api.Contact;
import codes.goblom.connect.api.command.CCommand;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 *
 * @author Goblom
 */
public class LuaCommand extends CCommand {

    protected static final String VARIABLES = new File(JavaPlugin.getPlugin(ConnectPlugin.class).getDataFolder(), "variables.lua").getAbsolutePath();
    private final File luaFile;
    private final LuaCommandTable table;
    
    protected LinkedList<String> names;
    protected LuaFunction execute;
    
    public LuaCommand(LuaCommandHandler handler, File f) {
        super(handler);
        
        this.luaFile = f;
        this.table = new LuaCommandTable(this);
        this.names = new LinkedList();
        
        if (f.isFile() && f.getName().toLowerCase(Locale.ENGLISH).endsWith(".lua")) {
            Globals globals = JsePlatform.standardGlobals();
                    globals.loadfile(VARIABLES);
                    
            LuaTable val = (LuaTable) globals.load(table);
            
            Arrays.asList("connect").forEach((name) -> {
                globals.set(name, val);
                globals.get("package").get("loaded").set(name, val);
            });
            
            Arrays.asList(val.keys()).forEach((key) -> globals.set(key, val.get(key)));
            LuaValue chunk = null;
            
            try {
                chunk = globals.loadfile(f.getAbsolutePath());
            } catch (Exception e) {
                if (e instanceof LuaError) {
                    throw e;
                } else e.printStackTrace();
            }
            
            if (chunk == null) {
                throw new RuntimeException("Unable to load luaFile[" + f.getName() + "]");
            }
            
            chunk.call();
        }
        
    }

    @Override
    public void execute(Contact contact, String[] args) {
        LuaValue[] luaArgs = new LuaValue[args.length];
        
        for (int i = 0; i< args.length; i++) {
            luaArgs[i] = LuaValue.valueOf(args[i]);
        }
        
        execute.invoke(CoerceJavaToLua.coerce(contact), LuaTable.listOf(luaArgs));
//        execute.invoke(CoerceJavaToLua.coerce(number), CoerceJavaToLua.coerce(new ArgsTable(args)));
    }

    @Override
    public String getName() {
        return names.get(0);
    }
    @Override
    public String[] getNames() {
        return names.toArray(new String[names.size()]);
    }
    
    // Keep just in case of fucking bugs
    @RequiredArgsConstructor
    class ArgsTable {
        final String[] args;
        
        public boolean has(int i) {
            return length() <= i;
        }
        
        public String get(int i) {
            return args[i];
        }
        
        public String[] get() {
            return args;
        }
        
        public int length() {
            return args.length;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            
            for (String arg : args) {
                sb.append(arg + " ");
            }
            
            return sb.toString().trim();
        }
    }
}
