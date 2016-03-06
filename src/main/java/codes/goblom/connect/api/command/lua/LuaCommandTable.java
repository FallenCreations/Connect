/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.api.command.lua;

import java.util.Arrays;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

/**
 *
 * @author Goblom
 */
public class LuaCommandTable extends TwoArgFunction {
    
    private final LuaCommand command;
    
    protected LuaCommandTable(LuaCommand cmd) {
        this.command = cmd;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable table = new LuaTable();
                 
                 Arrays.asList("setName", "setCommand").forEach((str) -> table.set(str, new setCommand()));
                 Arrays.asList("addAlias", "addAliases").forEach((str) -> table.set(str, new addAlias()));
                 Arrays.asList("execute", "onCommand", "onExecute").forEach((str) -> table.set(str, new onCommand()));
                 
        return table;
    }
    
    class setCommand extends OneArgFunction {
        
        @Override
        public LuaValue call(LuaValue val) {
            String name = val.checkjstring();
            
            command.names.addFirst(name);
            
            return LuaCommandTable.this;
        }
    }
    
    class addAlias extends VarArgFunction {
        
        @Override
        public LuaValue call() {
            return LuaCommandTable.this;
        }
        
        @Override
        public LuaValue call(LuaValue val) {
            String alias = val.checkjstring();
            command.names.addLast(alias);
            
            return LuaCommandTable.this;
        }
        
        @Override
        public LuaValue invoke(Varargs args) {
            int a = args.narg();
            
            for (int i = 1; i <= a; i++) {
                LuaValue val = args.arg(i);

                if (val == LuaValue.NIL || val == null) {
                    break;
                }

                call(val);
            }
            
            return LuaCommandTable.this;
        }
    }
    
    class onCommand extends OneArgFunction {
        
        @Override
        public LuaValue call(LuaValue val) {
            LuaFunction function = (LuaFunction) val;
            
            command.execute = function;
            
            return LuaCommandTable.this;
        }
    }
}
