--[[
    Achievements Lua Addon
    
    Only modify this class if you know what you are doing
]]

Bukkit = luajava.bindClass("org.bukkit.Bukkit")
Server = Bukkit:getServer()
ChatColor = luajava.bindClass("org.bukkit.ChatColor")
Material = luajava.bindClass("org.bukkit.Material")
Action = luajava.bindClass("org.bukkit.event.block.Action")

function toString(object) 
    return object:toString()
end

--[[
function getClassName(object)
    return object:getClass():getSimpleName()
end
]]

function tableFromList(list)
    local table = {}

    for i = 0, list:size() - 1 do
        table[i] = list:get(i)
    end
    
    return table
end

function tableFromArray(array)
    local table = {}

    for i = 0, array.length, 1 do
        table[i] = array[i]
    end

    return table
end