package com.ferret.fff;

import com.ferret.fff.mixin.create.PlayerSkyhookRendererAccessor;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.equipment.armor.CardboardArmorHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// Core based on JigCompats by jess (soup587) - https://github.com/soup587/JigCompats
// Some is my own bullshit, though
public final class CreateCompat {

    private static boolean resolved;
    private static boolean loaded;

    private CreateCompat() {}

    public static boolean isLoaded() {
        resolve();
        return loaded;
    }

    private static boolean resolve() {
        if (resolved) {
            return loaded;
        }
        resolved = true;
        ModList modList = ModList.get();
        loaded = modList != null && modList.isLoaded("create");
        return loaded;
    }

    public static boolean isCardboardStealth(Entity entity) {
        if (entity == null || !resolve()) {
            return false;
        }
        try {
            return CardboardArmorHandler.testForStealth(entity);
        } catch (RuntimeException | LinkageError e) {
            return false;
        }
    }

    public static boolean isRidingChainConveyor(Entity entity) {
        if (entity == null || !resolve()) {
            return false;
        }
        try {
            Set<UUID> hanging = PlayerSkyhookRendererAccessor.getHangingPlayers();
            return hanging != null && hanging.contains(entity.getUUID());
        } catch (RuntimeException | LinkageError e) {
            return false;
        }
    }

    public static Carried carriedFor(Entity player) {
        if (player == null || !resolve()) {
            return null;
        }
        AbstractContraptionEntity contraption = findCarrier(player.level(), player);
        if (contraption == null) {
            return null;
        }
        return new Carried(contraption.getContactPointMotion(player.position()), "getContactPointMotion");
    }

    public static int contraptionCount(Entity player) {
        if (player == null || !resolve()) {
            return 0;
        }
        Level level = player.level();
        AABB search = player.getBoundingBox().inflate(8.0, 6.0, 8.0);
        return level.getEntitiesOfClass(AbstractContraptionEntity.class, search).size();
    }

    public static boolean isContraption(Entity entity) {
        return entity instanceof AbstractContraptionEntity;
    }

    public static AbstractContraptionEntity findCarrier(Level level, Entity player) {
        if (level == null || player == null) {
            return null;
        }
        AABB search = player.getBoundingBox().inflate(8.0, 6.0, 8.0);
        List<AbstractContraptionEntity> contraptions =
                level.getEntitiesOfClass(AbstractContraptionEntity.class, search);
        AbstractContraptionEntity best = null;
        double bestDistSq = Double.MAX_VALUE;
        Vec3 playerPos = player.position();
        for (AbstractContraptionEntity e : contraptions) {
            if (!carries(e, player)) {
                continue;
            }
            double distSq = e.getBoundingBox().getCenter().distanceToSqr(playerPos);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                best = e;
            }
        }
        return best;
    }

    private static boolean carries(AbstractContraptionEntity contraption, Entity entity) {
        if (contraption.collidingEntities.containsKey(entity)) {
            return true;
        }
        return contraption.getBoundingBox().inflate(1.0).intersects(entity.getBoundingBox());
    }

    public static Carried getCarriedVelocity(AbstractContraptionEntity contraption, Vec3 point) {
        if (contraption == null || point == null) {
            return null;
        }
        return new Carried(contraption.getContactPointMotion(point), "getContactPointMotion");
    }

    public static Vec3 relativeVelocity(Entity player) {
        if (player == null || !resolve()) {
            return null;
        }
        AbstractContraptionEntity contraption = findCarrier(player.level(), player);
        if (contraption == null) {
            return null;
        }
        Vec3 contactMotion = contraption.getContactPointMotion(player.position());
        Vec3 worldMove = player.position().subtract(new Vec3(player.xo, player.yo, player.zo));
        Vec3 relative = worldMove.subtract(contactMotion);

        if (!isFinite(relative) || relative.lengthSqr() > 100.0) {
            return null;
        }
        return relative;
    }

    private static boolean isFinite(Vec3 vec) {
        return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }

    public record Carried(Vec3 motion, String source) {
    }
}
