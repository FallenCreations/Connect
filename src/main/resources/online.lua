--[[
    Example command for Connect

    Phone texts phone number associated with server with "/online"
        Server recieves message then replies with given command
        function in "onCommand" or "onExecute"
]]
Bukkit = luajava.bindClass("org.bukkit.Bukkit")
Server = Bukkit:getServer()

setCommand("online")
addAlias("o")

--[[
    onCommand(function(number, args))
    onExecute(function(number, args))
    execute(function(number, args))
]]
onCommand(
    function (phoneNumber, args)
--[[
	size = Bukkit:getServer():getOnlinePlayers():size()

	if (size > 0)
            players = Bukkit:getServer():getOnlinePlayers()
            message = "Online (" .. size .. "): "

            for i = 0, size - 1 do
                message = message .. tostring(players:get(i).getName()) .. ", "
            end

            print(message)
            phoneNumber:reply(message)
	else
]]
            phoneNumber:reply("There are " .. tostring(Bukkit:getServer():getOnlinePlayers():size()) .. " player(s) online.")
--	end
    end
)