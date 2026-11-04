package com.ferret.fff;

import com.ferret.fff.mixin.create.PlayerSkyhookRendererAccessor;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.equipment.armor.CardboardArmorHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
        try {
            Class.forName("com.simibubi.create.Create");
            loaded = true;
        } catch (final Throwable t) {
            loaded = false;
        }
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
        Carrier carrier = findCarrierWithContact(player.level(), player);
        if (carrier == null) {
            return null;
        }
        return new Carried(carrier.entity().getContactPointMotion(carrier.contactPoint()), "getContactPointMotion");
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
        Carrier carrier = findCarrierWithContact(level, player);
        return carrier == null ? null : carrier.entity();
    }

    public static Carrier findCarrierWithContact(Level level, Entity player) {
        if (level == null || player == null) {
            return null;
        }

        Carrier riding = ridingCarrier(player);
        if (riding != null) {
            return riding;
        }

        if (level.isClientSide) {
            try {
                return CreateClientCompat.findStandingCarrier(level, player);
            } catch (RuntimeException | LinkageError ignored) {
            }
        }

        AbstractContraptionEntity fallback = findCarrierFallback(level, player);
        return fallback == null ? null : new Carrier(fallback, player.position());
    }

    private static Carrier ridingCarrier(Entity player) {
        Entity vehicle = player.getVehicle();
        int depth = 0;
        while (vehicle != null && depth++ < 8) {
            if (vehicle instanceof AbstractContraptionEntity contraption) {
                return new Carrier(contraption, player.position());
            }
            vehicle = vehicle.getVehicle();
        }
        return null;
    }

    private static AbstractContraptionEntity findCarrierFallback(Level level, Entity player) {
        AABB search = player.getBoundingBox().inflate(8.0, 6.0, 8.0);
        List<AbstractContraptionEntity> contraptions = level.getEntitiesOfClass(AbstractContraptionEntity.class, search);
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
        Carrier carrier = findCarrierWithContact(player.level(), player);
        if (carrier == null) {
            return null;
        }
        Vec3 contactMotion = carrier.entity().getContactPointMotion(carrier.contactPoint());
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

    public record Carrier(AbstractContraptionEntity entity, Vec3 contactPoint) {}

    public record Carried(Vec3 motion, String source) {}
}
