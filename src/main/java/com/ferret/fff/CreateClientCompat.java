package com.ferret.fff;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionHandler;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class CreateClientCompat {

    private static final double BASE_PROBE = 0.6;
    private static final double MAX_PROBE = 2.0;
    private static final double FOOTPRINT_INSET = 0.25;

    private static final Object LOADED_CONTRAPTIONS;
    private static final Method WORLD_ATTACHED_GET;

    static {
        Object attached = null;
        Method method = null;
        try {
            Field field = ContraptionHandler.class.getField("loadedContraptions");
            attached = field.get(null);
            method = attached.getClass().getMethod("get", LevelAccessor.class);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
            attached = null;
            method = null;
        }
        LOADED_CONTRAPTIONS = attached;
        WORLD_ATTACHED_GET = method;
    }

    private CreateClientCompat() {}

    static CreateCompat.Carrier findStandingCarrier(Level level, Entity player) {
        AABB playerBox = player.getBoundingBox();
        if (playerBox == null) {
            return null;
        }

        List<AbstractContraptionEntity> contraptions = contraptions(level, playerBox);
        if (contraptions.isEmpty()) {
            return null;
        }

        double scale = playerScale(player);
        double probe = Math.max(0.05, Math.min(MAX_PROBE, BASE_PROBE * scale));
        double startY = playerBox.minY + Math.max(1.0E-4, 0.01 * scale);
        double maxStandY = playerBox.minY + Math.max(1.0E-4, 0.05 * scale);
        double minHitY = startY - probe;

        AABB footprint = playerBox.deflate(
                playerBox.getXsize() * FOOTPRINT_INSET,
                0.0,
                playerBox.getZsize() * FOOTPRINT_INSET);

        double centerX = footprint.getCenter().x;
        double centerZ = footprint.getCenter().z;
        double[] xs = {footprint.minX, centerX, footprint.maxX};
        double[] zs = {footprint.minZ, centerZ, footprint.maxZ};

        CreateCompat.Carrier best = null;
        double bestHitY = -Double.MAX_VALUE;
        double bestDistanceSq = Double.MAX_VALUE;

        for (AbstractContraptionEntity contraptionEntity : contraptions) {
            if (contraptionEntity == null || !contraptionEntity.isAlive()) {
                continue;
            }

            Contraption contraption = contraptionEntity.getContraption();
            if (contraption == null || contraption.bounds == null) {
                continue;
            }

            Vec3 feet = new Vec3(centerX, playerBox.minY, centerZ);
            Vec3 localFeet = contraptionEntity.toLocalVector(feet, 1.0F);
            if (!contraption.bounds.inflate(1.0).contains(localFeet)) {
                continue;
            }

            for (double x : xs) {
                for (double z : zs) {
                    Vec3 start = new Vec3(x, startY, z);
                    Vec3 end = new Vec3(x, minHitY, z);

                    BlockHitResult hit = ContraptionHandlerClient.rayTraceContraption(start, end, contraptionEntity);
                    if (hit == null) {
                        continue;
                    }

                    Vec3 worldHit = contraptionEntity.toGlobalVector(hit.getLocation(), 1.0F);
                    if (worldHit.y > maxStandY || worldHit.y < minHitY) {
                        continue;
                    }

                    double distanceSq = worldHit.distanceToSqr(start);

                    BlockHitResult worldBlock = level.clip(new ClipContext(
                            start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                    if (worldBlock.getType() == HitResult.Type.BLOCK
                            && worldBlock.getLocation().distanceToSqr(start) <= distanceSq + 1.0E-4) {
                        continue;
                    }

                    boolean higher = worldHit.y > bestHitY + 1.0E-6;
                    boolean sameHeightCloser = Math.abs(worldHit.y - bestHitY) <= 1.0E-6 && distanceSq < bestDistanceSq;
                    if (higher || sameHeightCloser) {
                        bestHitY = worldHit.y;
                        bestDistanceSq = distanceSq;
                        best = new CreateCompat.Carrier(contraptionEntity, worldHit);
                    }
                }
            }
        }

        return best;
    }

    private static List<AbstractContraptionEntity> contraptions(Level level, AABB playerBox) {
        if (LOADED_CONTRAPTIONS != null && WORLD_ATTACHED_GET != null) {
            try {
                Object value = WORLD_ATTACHED_GET.invoke(LOADED_CONTRAPTIONS, level);
                if (value instanceof Map<?, ?> map) {
                    List<AbstractContraptionEntity> result = new ArrayList<>(map.size());
                    for (Object entry : map.values()) {
                        if (entry instanceof WeakReference<?> reference
                                && reference.get() instanceof AbstractContraptionEntity contraption) {
                            result.add(contraption);
                        }
                    }
                    return result;
                }
            } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
            }
        }
        return level.getEntitiesOfClass(AbstractContraptionEntity.class, playerBox.inflate(16.0, 8.0, 16.0));
    }

    private static double playerScale(Entity player) {
        double baseWidth = player.getType().getDimensions().width();
        if (!(baseWidth > 0.0)) {
            return 1.0;
        }
        double scale = player.getBbWidth() / baseWidth;
        return Double.isFinite(scale) && scale > 0.0 ? Math.min(scale, 10.0) : 1.0;
    }
}
