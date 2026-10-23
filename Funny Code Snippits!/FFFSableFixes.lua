if not client.isModLoaded("ferrets_figura_functions") then return end
-- If Ferrets Figura Functions is installed, use custom getVel and raycasts to properly account for Create
-- and Sable movements. Should just be drag-and-drop..?

--Vel
figuraMetatables.PlayerAPI.__index.getVelocity = function(_, ...) return FFF:getRelativeVelocity(...) end

--RaycastAPI Bullshiterry
figuraMetatables.RaycastAPI.__index.block = function(_, ...) return FFF:subLevelRaycast(...) end
figuraMetatables.RaycastAPI.__index.entity = function(_, ...) return FFF:subLevelEntityRaycast(...) end
figuraMetatables.RaycastAPI.__index.aabb = function(_, ...) return FFF:subLevelAabbRaycast(...) end