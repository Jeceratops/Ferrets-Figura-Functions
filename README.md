# Ferret's Figura Functions

A Stupid mod made because Ammedments and Supplementaries are stupid, and I wanted this fixed for the Wayfarer modpack. ~~Might expand as needed down the road!~~
We now support
- Ammendments
- Supplumentaries
- Ziplines: Rezipped!
- Create
- Sable
And still more to come, probably.

## Usage

```lua
if client.isModLoaded("ferrets_figura_functions") then
    FFF:setAmmdLantern(false) -- disable Amendments lantern so we can load our own model (with Figura's Item_Render)
end
```

## API
Functions that exist, hopefully useful for someone. ~~I know that TECHNICALLY /figura docs exists, but I honestly cant be arsed.~~
I could be arsed! `/Figura Docs Globals FFF FFF` exists now!
##
## ---=== General ===---
#### `FFF:setOffhandPlaceLock(items)`
`items` is a table/set of item ids (`FFF:setOffhandPlaceLock({ "minecraft:lantern" })`;
ids without an indetifier thingy get `minecraft:` prepended). While the avatar is loaded, it blocks
placeing/using those items in the offhand.
##
### ---=== SUPPLEMENTARIES ===---
### `FFF:getSuppQuiverVisible()` -> boolean
Whether the Supplementaries quiver still renders for this avatar.

### `FFF:hasSuppQuiver()` -> boolean
`true` while the avatar's player is wearing a Supplementaries quiver

### `FFF:setSuppQuiverVisible(visible)`
`false` disables the Supplementaries `QuiverLayer`
##
### ---=== AMMENDMENTS ===---
### `FFF:getAmmdLantern()` -> boolean
Whether the Amendments lantern renderer is enabled.

### `FFF:setAmmdLantern(enabled)`
`false` disables the Amendments lantern in first and third person.

### `FFF:getAmmdTorch()` -> boolean
Whether the Amendments torch renderer is enabled.

### `FFF:setAmmdTorch(enabled)`
`false` disables the Amendments torch in first and third person.

### `FFF:getAmmdCandleHolder()` -> boolean
Whether the Amendments candle holder renderer is enabled.

### `FFF:setAmmdCandleHolder(enabled)`
`false` disables the Amendments candle holder in first and third person.
##
## ---=== Zipline: Rezipped! ===---
### `FFF:getTryZip()` -> boolean
`true` while the avatar's player is trying to zipline when holding a
zipline tool (an item in the `zipline:attachment` tag, pickaxes by default).

### `FFF:getZipping()` -> boolean
`true` while the avatar's player is actually riding a zipline.
##
## ---=== What Are They Up To (WATUP) ===---
Just a bunch of toggles and checks for if it *would* be rendering. Might be useful for some, as they'd simplify some Figura checks.

#### `FFF:getWATUTIdleAnimation()` -> boolean
Whether WATUT's idle (AFK) head animation is enabled for this avatar (default `true`).

#### `FFF:setWATUTIdleAnimation(play)`
`false` stops WATUT from rotating this avatar's head while idle

#### `FFF:getWATUTIdleAnimationWouldPlay()` -> boolean
`true` while the player is idle.

#### `FFF:getWATUTTypingAnimation()` -> boolean
Whether WATUT's typing arm animation is enabled for this avatar (default `true`).

#### `FFF:setWATUTTypingAnimation(play)`
`false` stops WATUT from animating this avatar's arms while typing.

#### `FFF:getWATUTTypingAnimationWouldPlay()` -> boolean
`true` while the player is typing in chat.

#### `FFF:getWATUTTypingTextBox()` -> boolean
Whether WATUT's typing chat box is enabled for this avatar (default `true`).

#### `FFF:setWATUTTypingTextBox(render)`
`false` stops WATUT from drawing the typing chat box

#### `FFF:getWATUTTypingTextBoxWouldRender()` -> boolean
`true` while the player has chat open.
##
## ---=== Create ===---
Much of the Create integration is borrowed from [JigCompats](https://github.com/soup587/JigCompats)
by **jess (soup587)**. She gave her permission for me to use it, so I did!

#### `FFF:getCreateSkyhookAnimation()` -> boolean
Whether Create's chain hanging pose still plays for this avatar (default `true`).

#### `FFF:setCreateSkyhookAnimation(play)`
`false` skips Create's chain hanging animation for this avatar's player.

#### `FFF:getCreateRidingChainConveyor()` -> boolean
`true` while the avatar's player is riding a Create chain.

#### `FFF:getCreateCardboardBox()` -> boolean
Whether Create's cardboard hiding-as-a-box render is enabled for this avatar (default `true`).

#### `FFF:setCreateCardboardBox(render)`
`false` stops Create from rendering this avatar's player as a box while sneaking in
cardboard armor, and hides the matching screen blur overlay.

#### `FFF:getCreateCardboardStealth()` -> boolean
`true` while the avatar's player is hiding as a box (cardboard armor crouch).

#### `FFF:getCreateCarriedVelocity()` -> vector3 | nil
The contraption velocity at the avatar's player's position, or `nil` when
the player isn't standing on a contraption.

#### `FFF:isCreateContraptionCarrying()` -> boolean
`true` while a Create contraption is carrying the player.

#### `FFF:getCreateContraptionCount()` -> int
Number of Create contraption entities near the player.

#### `FFF:getCreateCarriedSource()` -> string
Which Create method produced the last carried velocity (`getContactPointMotion`,
`getMotionOfPoint`, `getContraptionMotion`, or `getDeltaMovement`), or `"none"`.
Debug helper.
##
## ---=== Sable ===---
Just a bunch of functions to make Sable not a PITA with Figura.
- Fixed BP3 Camera with Sable generically
- Fixed Figura Camera with Sable
- OH! also fixes the camera when sitting automatically.
Code based on snippit from NikoSolstice on the Figura Discord

#### `FFF:isOnSublevel()` -> boolean
`true` while the avatar's player is standing on or riding a Sable sub-level.

#### `FFF:getSublevelRelativeVelocity()` -> vector3 | nil
The player's velocity relative to the Sable sublevel it is on, in world space, or `nil`
when it isn't on one.

#### `FFF:getRelativeVelocity()` -> vector3
The velocity relative to the Sable sub-level the player is on,
else relative to the Create contraption it is on, else the plain vanilla velocity.

#### `FFF:subLevelRaycast(x, y, z, x2, y2, z2, blockRaycastType, fluidRaycastType)`
`raycast.block`, but for Sublevels. Works exactly like `raycast.block`
when not on a sublevel.

#### `FFF:subLevelEntityRaycast(x, y, z, x2, y2, z2, predicate)`
`raycast.entity` but for Sublevels. Works exactly like `raycast.entity`
when not on a sublevel

#### `FFF:subLevelAabbRaycast(x, y, z, x2, y2, z2, aabbs)`
`raycast.aabb` but for Sublevels. Works exactly like `raycast.aabb`
when not on a sublevel
