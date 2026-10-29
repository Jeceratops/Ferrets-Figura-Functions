-- Hides selected model parts while Exposure is taking a photo.
-- Usage: if not client.isModLoaded("ferrets_figura_functions") then return end
--        local PhotoHide = require("lib.FFFCameraHider")
--        PhotoHide.setup({ modelparts = { MothRoot }, hideShadow = true })

if not client.isModLoaded("ferrets_figura_functions") then return end

local PhotoHide = {}

function PhotoHide.setup(config)
  config = config or {}
  local hideList = config.hide or config.modelparts or {}
  local showList = config.show or {}
  local hideShadow = config.hideShadow
  if hideShadow == nil then hideShadow = true end

  local photoActive = false
  local prevHide = {}
  local prevShow = {}
  local shadowPrevVis = nil

  function events.render()
    local isPhoto = FFF:isTakingPhoto() == true

    if isPhoto and not photoActive then
      if hideShadow and vanilla_model and vanilla_model.PLAYER then
        local shadow = vanilla_model.PLAYER.SHADOW
        if shadow and type(shadow.getVisible) == "function" then
          shadowPrevVis = shadow:getVisible()
          shadow:setVisible(false)
        end
      end

      if type(hideList) == "table" then
        for i = 1, #hideList do
          local part = hideList[i]
          if part ~= nil and type(part.getVisible) == "function" then
            prevHide[part] = part:getVisible()
            part:setVisible(false)
          end
        end
      end

      if type(showList) == "table" then
        for i = 1, #showList do
          local part = showList[i]
          if part ~= nil and type(part.getVisible) == "function" then
            prevShow[part] = part:getVisible()
            part:setVisible(true)
          end
        end
      end

      photoActive = true
    elseif not isPhoto and photoActive then
      if hideShadow and shadowPrevVis ~= nil and vanilla_model and vanilla_model.PLAYER then
        local shadow = vanilla_model.PLAYER.SHADOW
        if shadow and type(shadow.setVisible) == "function" then
          shadow:setVisible(shadowPrevVis)
        end
      end
      shadowPrevVis = nil

      for part, visible in pairs(prevHide) do
        if part ~= nil and type(part.setVisible) == "function" then
          part:setVisible(visible)
        end
      end
      for part, visible in pairs(prevShow) do
        if part ~= nil and type(part.setVisible) == "function" then
          part:setVisible(visible)
        end
      end

      prevHide = {}
      prevShow = {}
      photoActive = false
    end
  end
end

return PhotoHide
