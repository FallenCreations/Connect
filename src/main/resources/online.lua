--[[
    Example command for Connect

    Phone texts phone number associated with server with "/online"
        Server recieves message then replies with given command
        function in "onCommand" or "onExecute"
]]


setCommand("online")
addAlias("o")

--[[
    onCommand(function(number, args))
    onExecute(function(number, args))
    execute(function(number, args))
]]
onCommand(
    function (phoneNumber, ...)
        phoneNumber:reply("There are " .. Bukkit.getOnlinePlayers():size() .. " online.")
    end
)