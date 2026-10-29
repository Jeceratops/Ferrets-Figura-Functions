-- Returns the nearest player within range whose WATUT typing amplifier is above zero.
-- Usage: if not client.isModLoaded("ferrets_figura_functions") then return end
--        local WhoIsTyping = require("lib.FFFWhoIsTyping")
--        local player = WhoIsTyping(5)

if not client.isModLoaded("ferrets_figura_functions") then return end

return function(radius)
  radius = radius or 5
  if radius < 0 then
    return nil
  end

  local origin = player:getPos()
  local nearest
  local nearestDistance = radius

  for _, candidate in pairs(world.getPlayers()) do
    if candidate ~= player then
      local distance = (candidate:getPos() - origin):length()
      if distance <= nearestDistance and FFF:getWATUTChatTypingFor(candidate:getUUID()) then
        nearest = candidate
        nearestDistance = distance
      end
    end
  end

  return nearest
end
