--[[
    Name: commands
    Alias: listCommands, listCmd, lc

    Action: Replies back with the list of available commands
]]

CommandHandlers = luajava.bindClass("codes.goblom.connect.api.CommandHandlers")

setCommand("commands")
addAliases("listCommands", "listCmd", "lc")

onExecute(
    function (number, ...)
        local commands = "Commands: "
        local amount = 0
        local handlers = CommandHandlers.getCommandHandlers()
        
        for a = 0, handlers:size() - 1 do
            local cmds = handlers:get(i):getCommands()
            
            for b = 0, cmds:size() - 1 do 
                if amount not 0
                    commands = commands .. " , "
                end

                commands = comands .. cmds:get(i)
                amount = amount + 1
            end
        end

        number:reply(commands)
    end
)
