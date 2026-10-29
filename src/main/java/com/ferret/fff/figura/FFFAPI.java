package com.ferret.fff.figura;

import com.ferret.fff.CreateCompat;
import com.ferret.fff.SableCompat;
import com.ferret.fff.WatutCompat;
import com.ferret.fff.ZiplineCompat;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.entries.FiguraAPI;
import org.figuramc.figura.entries.annotations.FiguraAPIPlugin;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.RaycastAPI;
import org.figuramc.figura.lua.api.entity.EntityAPI;
import org.figuramc.figura.lua.api.world.BlockStateAPI;
import org.figuramc.figura.lua.api.world.WorldAPI;
import org.figuramc.figura.math.vector.FiguraVec3;
import org.figuramc.figura.utils.LuaUtils;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.figuramc.figura.lua.docs.LuaTypeDoc;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.LuaValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@LuaWhitelist
@FiguraAPIPlugin
@LuaTypeDoc(name = "FFF", value = "FFF")
public final class FFFAPI implements FiguraAPI {

    private final Avatar avatar;
    private final FFFState state;

    public FFFAPI() {
        this.avatar = null;
        this.state = null;
    }

    private FFFAPI(Avatar avatar) {
        this.avatar = avatar;
        this.state = FFFStates.get(avatar);
    }

    @Override
    public String getName() {
        return "FFF";
    }

    @Override
    public Collection<Class<?>> getWhitelistedClasses() {
        return List.of(FFFAPI.class);
    }

    @Override
    public Collection<Class<?>> getDocsClasses() {
        return List.of(FFFAPI.class);
    }

    @Override
    public FiguraAPI build(Avatar avatar) {
        return new FFFAPI(avatar);
    }
   @LuaWhitelist
   @LuaMethodDoc(value = "fff.getSuppQuiverVisible", overloads = @LuaMethodOverload(returnType = Boolean.class))
   public boolean getSuppQuiverVisible() {
      return this.state == null || this.state.isSuppQuiverVisible();
   }

   @LuaWhitelist
   @LuaMethodDoc(value = "fff.hasSuppQuiver", overloads = @LuaMethodOverload(returnType = Boolean.class))
   public boolean hasSuppQuiver() {
      return this.state != null && this.state.isNativeQuiverActive();
   }

   @LuaWhitelist
   @LuaMethodDoc(value = "fff.setSuppQuiverVisible", overloads = @LuaMethodOverload(argumentTypes = {Boolean.class}, returnType = FFFAPI.class))
   public FFFAPI setSuppQuiverVisible(boolean visible) {
      if (this.state != null) {
         this.state.setSuppQuiverVisible(visible);
      }

      return this;
   }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getAmmdLantern", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getAmmdLantern() {
        return this.state == null || this.state.isAmmdLantern();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.setAmmdLantern", overloads = @LuaMethodOverload(argumentTypes = {Boolean.class}, returnType = FFFAPI.class))
    public FFFAPI setAmmdLantern(boolean enabled) {
        if (this.state != null) {
            this.state.setAmmdLantern(enabled);
        }
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getAmmdTorch", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getAmmdTorch() {
        return this.state == null || this.state.isAmmdTorch();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.setAmmdTorch", overloads = @LuaMethodOverload(argumentTypes = {Boolean.class}, returnType = FFFAPI.class))
    public FFFAPI setAmmdTorch(boolean enabled) {
        if (this.state != null) {
            this.state.setAmmdTorch(enabled);
        }
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getAmmdCandleHolder", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getAmmdCandleHolder() {
        return this.state == null || this.state.isAmmdCandleHolder();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.setAmmdCandleHolder", overloads = @LuaMethodOverload(argumentTypes = {Boolean.class}, returnType = FFFAPI.class))
    public FFFAPI setAmmdCandleHolder(boolean enabled) {
        if (this.state != null) {
            this.state.setAmmdCandleHolder(enabled);
        }
        return this;
    }

    @Override
    public String toString() {
        return "FFF";
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getTryZip", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getTryZip() {
        return ZiplineCompat.isTryingToZip(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getZipping", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getZipping() {
        return ZiplineCompat.isZipping(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTIdleAnimation", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getWATUTIdleAnimation() {
        return this.state == null || this.state.isWatutIdleAnimation();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.isTakingPhoto", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean isTakingPhoto() {
        return com.ferret.fff.exposure.ExposureCompat.isTakingPhoto();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.setWATUTIdleAnimation", overloads = @LuaMethodOverload(argumentTypes = {Boolean.class}, returnType = FFFAPI.class))
    public FFFAPI setWATUTIdleAnimation(boolean play) {
        if (this.state != null) {
            this.state.setWatutIdleAnimation(play);
        }
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTIdleAnimationWouldPlay", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getWATUTIdleAnimationWouldPlay() {
        return WatutCompat.idleAnimationWouldPlay(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTTypingAnimation", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getWATUTTypingAnimation() {
        return this.state == null || this.state.isWatutTypingAnimation();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.setWATUTTypingAnimation", overloads = @LuaMethodOverload(argumentTypes = {Boolean.class}, returnType = FFFAPI.class))
    public FFFAPI setWATUTTypingAnimation(boolean play) {
        if (this.state != null) {
            this.state.setWatutTypingAnimation(play);
        }
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTChatOpen", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getWATUTChatOpen() {
        return WatutCompat.chatOpen(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTChatHasText", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getWATUTChatHasText() {
        return WatutCompat.chatHasText(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTChatTyping", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getWATUTChatTyping() {
        return WatutCompat.chatTyping(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTChatTypingFor", overloads = @LuaMethodOverload(argumentTypes = {String.class}, returnType = Boolean.class))
    public boolean getWATUTChatTypingFor(String uuid) {
        try {
            return WatutCompat.chatTyping(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTChatTypingSpeed", overloads = @LuaMethodOverload(returnType = Float.class))
    public float getWATUTChatTypingSpeed() {
        return WatutCompat.chatTypingSpeed(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTTypingTextBox", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getWATUTTypingTextBox() {
        return this.state == null || this.state.isWatutTypingTextBox();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.setWATUTTypingTextBox", overloads = @LuaMethodOverload(argumentTypes = {Boolean.class}, returnType = FFFAPI.class))
    public FFFAPI setWATUTTypingTextBox(boolean render) {
        if (this.state != null) {
            this.state.setWatutTypingTextBox(render);
        }
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getWATUTTypingTextBoxWouldRender", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getWATUTTypingTextBoxWouldRender() {
        return WatutCompat.typingTextBoxWouldRender(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getCreateSkyhookAnimation", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getCreateSkyhookAnimation() {
        return this.state == null || this.state.isCreateSkyhookAnimation();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.setCreateSkyhookAnimation", overloads = @LuaMethodOverload(argumentTypes = {Boolean.class}, returnType = FFFAPI.class))
    public FFFAPI setCreateSkyhookAnimation(boolean play) {
        if (this.state != null) {
            this.state.setCreateSkyhookAnimation(play);
        }
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getCreateCardboardBox", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getCreateCardboardBox() {
        return this.state == null || this.state.isCreateCardboardBox();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.setCreateCardboardBox", overloads = @LuaMethodOverload(argumentTypes = {Boolean.class}, returnType = FFFAPI.class))
    public FFFAPI setCreateCardboardBox(boolean render) {
        if (this.state != null) {
            this.state.setCreateCardboardBox(render);
        }
        return this;
    }

   @LuaWhitelist
   @LuaMethodDoc(value = "fff.setOffhandPlaceLock", overloads = @LuaMethodOverload(argumentTypes = {LuaTable.class}, returnType = FFFAPI.class))
   public FFFAPI setOffhandPlaceLock(LuaTable table) {
        if (this.state == null) {
            return this;
        }

        Set<String> itemIds = new HashSet<>();
        if (table != null) {
            for (LuaValue key : table.keys()) {
                LuaValue value = table.get(key);
                String raw = value.isstring() ? value.tojstring() : (key.isstring() ? key.tojstring() : null);
                if (raw == null) {
                    continue;
                }

                String id = raw.trim().toLowerCase(Locale.ROOT);
                if (id.isEmpty()) {
                    continue;
                }
                if (!id.contains(":")) {
                    id = "minecraft:" + id;
                }
                itemIds.add(id);
            }
        }

        this.state.setOffhandPlaceLock(itemIds);
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getCreateRidingChainConveyor", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getCreateRidingChainConveyor() {
        return CreateCompat.isRidingChainConveyor(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getCreateCardboardStealth", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean getCreateCardboardStealth() {
        return CreateCompat.isCardboardStealth(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getCreateCarriedVelocity", overloads = @LuaMethodOverload(returnType = FiguraVec3.class))
    public FiguraVec3 getCreateCarriedVelocity() {
        CreateCompat.Carried carried = CreateCompat.carriedFor(this.player());
        if (carried == null) {
            return null;
        }
        Vec3 motion = carried.motion();
        return FiguraVec3.of(motion.x, motion.y, motion.z);
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.isCreateContraptionCarrying", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean isCreateContraptionCarrying() {
        return CreateCompat.carriedFor(this.player()) != null;
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getCreateContraptionCount", overloads = @LuaMethodOverload(returnType = Integer.class))
    public int getCreateContraptionCount() {
        return CreateCompat.contraptionCount(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getCreateCarriedSource", overloads = @LuaMethodOverload(returnType = String.class))
    public String getCreateCarriedSource() {
        CreateCompat.Carried carried = CreateCompat.carriedFor(this.player());
        return carried == null ? "none" : carried.source();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.isOnSublevel", overloads = @LuaMethodOverload(returnType = Boolean.class))
    public boolean isOnSublevel() {
        return SableCompat.isOnSubLevel(this.player());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getSublevelRelativeVelocity", overloads = @LuaMethodOverload(returnType = FiguraVec3.class))
    public FiguraVec3 getSublevelRelativeVelocity() {
        Vec3 relative = SableCompat.relativeVelocity(this.player());
        return relative == null ? null : FiguraVec3.of(relative.x, relative.y, relative.z);
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.getRelativeVelocity", overloads = @LuaMethodOverload(returnType = FiguraVec3.class))
    public FiguraVec3 getRelativeVelocity() {
        Player player = this.player();
        if (player == null) {
            return null;
        }

        Vec3 relative = SableCompat.relativeVelocity(player);
        if (relative == null) {
            relative = CreateCompat.relativeVelocity(player);
        }
        if (relative != null) {
            return FiguraVec3.of(relative.x, relative.y, relative.z);
        }

        EntityAPI<?> vanilla = EntityAPI.wrap(player);
        return vanilla == null ? null : vanilla.getVelocity();
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.subLevelRaycast", overloads = @LuaMethodOverload(argumentTypes = {Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, String.class, String.class}, returnType = Object[].class))
    public Object[] subLevelRaycast(Object x, Object y, Object z, Object w, Object t, Object h, String blockCastType, String fluidCastType) {
        Pair<Pair<FiguraVec3, FiguraVec3>, Object[]> parseResult = LuaUtils.parse2Vec3(
                "subLevelRaycast", new Class[]{String.class, String.class},
                x, y, z, w, t, h, blockCastType, fluidCastType);
        FiguraVec3 start = parseResult.getFirst().getFirst();
        FiguraVec3 end = parseResult.getFirst().getSecond();
        blockCastType = (String) parseResult.getSecond()[0];
        fluidCastType = (String) parseResult.getSecond()[1];

        ClipContext.Block blockContext;
        try {
            blockContext = blockCastType != null ? ClipContext.Block.valueOf(blockCastType.toUpperCase(Locale.US)) : ClipContext.Block.COLLIDER;
        } catch (IllegalArgumentException e) {
            throw new LuaError("Invalid blockRaycastType provided");
        }

        ClipContext.Fluid fluidContext;
        try {
            fluidContext = fluidCastType != null ? ClipContext.Fluid.valueOf(fluidCastType.toUpperCase(Locale.US)) : ClipContext.Fluid.NONE;
        } catch (IllegalArgumentException e) {
            throw new LuaError("Invalid fluidRaycastType provided");
        }

        Player player = this.player();
        if (player != null) {
            SableCompat.SubLevelHit hit = SableCompat.subLevelRaycast(player, start.asVec3(), end.asVec3(), blockContext, fluidContext);
            if (hit != null) {
                BlockHitResult result = hit.result();
                return new Object[]{
                        new BlockStateAPI(hit.state(), result.getBlockPos()),
                        FiguraVec3.fromVec3(result.getLocation()),
                        result.getDirection().getName()
                };
            }
        }

        BlockHitResult result = WorldAPI.getCurrentWorld()
                .clip(new ClipContext(start.asVec3(), end.asVec3(), blockContext, fluidContext,
                        new Marker(EntityType.MARKER, WorldAPI.getCurrentWorld())));
        return LuaUtils.parseBlockHitResult(result);
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.subLevelEntityRaycast", overloads = @LuaMethodOverload(argumentTypes = {Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, LuaFunction.class}, returnType = Object[].class))
    public Object[] subLevelEntityRaycast(Object x, Object y, Object z, Object w, Object t, Double h, LuaFunction predicate) {
        Pair<Pair<FiguraVec3, FiguraVec3>, Object[]> parseResult = LuaUtils.parse2Vec3(
                "subLevelEntityRaycast", new Class[]{LuaFunction.class},
                x, y, z, w, t, h, predicate);
        FiguraVec3 start = parseResult.getFirst().getFirst();
        FiguraVec3 end = parseResult.getFirst().getSecond();
        LuaFunction fn = (LuaFunction) parseResult.getSecond()[0];

        final Avatar avatar = this.avatar;
        Predicate<Entity> entityPredicate = entity -> {
            if (fn == null) {
                return true;
            }
            LuaValue result = fn.invoke(avatar.luaRuntime.typeManager.javaToLua(EntityAPI.wrap(entity))).arg1();
            return (!result.isboolean() || result.checkboolean()) && !result.isnil();
        };

        Player player = this.player();
        if (player != null && SableCompat.isContained(player)) {
            EntityHitResult hit = SableCompat.subLevelEntityRaycast(player, start.asVec3(), end.asVec3(), entityPredicate, Double.MAX_VALUE);
            return hit != null
                    ? new Object[]{EntityAPI.wrap(hit.getEntity()), FiguraVec3.fromVec3(hit.getLocation())}
                    : null;
        }

        EntityHitResult result = ProjectileUtil.getEntityHitResult(
                new Marker(EntityType.MARKER, WorldAPI.getCurrentWorld()),
                start.asVec3(), end.asVec3(),
                new AABB(start.asVec3(), end.asVec3()),
                entityPredicate, Double.MAX_VALUE);
        return result != null
                ? new Object[]{EntityAPI.wrap(result.getEntity()), FiguraVec3.fromVec3(result.getLocation())}
                : null;
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "fff.subLevelAabbRaycast", overloads = @LuaMethodOverload(argumentTypes = {Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, LuaTable.class}, returnType = Object[].class))
    public Object[] subLevelAabbRaycast(Object x, Object y, Object z, Object w, Object t, Object h, LuaTable aabbs) {
        return new RaycastAPI(this.avatar).aabb(x, y, z, w, t, h, aabbs);
    }

    @LuaWhitelist
    public String yuri() {
        return "based...";
    }

    private Player player() {
        if (this.avatar == null) {
            return null;
        }
        var level = Minecraft.getInstance().level;
        return level == null ? null : level.getPlayerByUUID(this.avatar.owner);
    }
}
