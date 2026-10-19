package com.ferret.fff;

import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.RendererAPI;
import org.figuramc.figura.math.vector.FiguraVec3;
import org.figuramc.figura.utils.RenderUtils;

public final class SableCompat {

    private static boolean resolved;
    private static boolean loaded;
    private static Boolean ughBetter3rdPersonIsAnnoying;
    private static final Set<UUID> subLevelCameraRotSet = new HashSet<>();

    private SableCompat() {}

    public static boolean isLoaded() {
        resolve();
        return loaded;
    }

    private static boolean resolve() {
        if (resolved) {
            return loaded;
        }
        resolved = true;

        final ModList modList = ModList.get();
        if (modList == null || !modList.isLoaded("sable")) {
            return false;
        }
        try {
            loaded = SableCompanion.INSTANCE != null;
        } catch (final Throwable t) {
            loaded = false;
        }
        return loaded;
    }

    public static boolean ughBetter3rdPersonIsAnnoying() {
        if (ughBetter3rdPersonIsAnnoying != null) {
            return ughBetter3rdPersonIsAnnoying;
        }
        final ModList modList = ModList.get();
        if (modList == null) {
            return false;
        }
        ughBetter3rdPersonIsAnnoying = modList.isLoaded("betterthirdperson");
        return ughBetter3rdPersonIsAnnoying;
    }

    public static SubLevelAccess subLevelFor(final Entity entity) {
        if (entity == null || !resolve()) {
            return null;
        }
        try {
            SubLevelAccess tracked = SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(entity);
            if (tracked != null) {
                return tracked;
            }
            return SableCompanion.INSTANCE.getContaining(entity.level(), entity.position());
        } catch (final Throwable t) {
            return null;
        }
    }

    public static boolean isOnSubLevel(final Entity entity) {
        return subLevelFor(entity) != null;
    }

    public static boolean isContained(final Entity entity) {
        if (entity == null || !resolve()) {
            return false;
        }
        try {
            return SableCompanion.INSTANCE.getContainingClient(entity) instanceof SubLevel;
        } catch (final Throwable t) {
            return false;
        }
    }

    public static Vec3 relativeVelocity(final Entity entity) {
        if (entity == null || !resolve()) {
            return null;
        }

        final SubLevelAccess subLevel = subLevelFor(entity);
        if (subLevel == null) {
            return null;
        }

        try {
            final Vec3 now = entity.position();
            final Vec3 old = new Vec3(entity.xo, entity.yo, entity.zo);
            final Pose3dc last = subLevel.lastPose();
            final Pose3dc logical = subLevel.logicalPose();
            final Vec3 localPrev = last.transformPositionInverse(old);
            final Vec3 projectedPrev = logical.transformPosition(localPrev);
            final Vec3 relative = now.subtract(projectedPrev);

            if (!Double.isFinite(relative.x) || !Double.isFinite(relative.y) || !Double.isFinite(relative.z)
                    || relative.lengthSqr() > 100.0) {
                return null;
            }
            return relative;
        } catch (final Throwable t) {
            return null;
        }
    }

    private static Vec3 subLevelEye(final Entity entity, final float partialTicks, final float eyeHeightOld, final float eyeHeight) {
        try {
            if (SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(entity) != null) {
                return SableCompanion.INSTANCE.getEyePositionInterpolated(entity, partialTicks);
            }

            final ClientSubLevelAccess containing = SableCompanion.INSTANCE.getContainingClient(entity);
            if (containing == null) {
                return null;
            }

            final Vec3 localEye = new Vec3(entity.getX(),
                    entity.getY() + Mth.lerp(partialTicks, eyeHeightOld, eyeHeight),
                    entity.getZ());
            final Vec3 worldEye = containing.renderPose(partialTicks).transformPosition(localEye);
            return worldEye != null && isFinite(worldEye) ? worldEye : null;
        } catch (final Throwable t) {
            return null;
        }
    }

    private static boolean isFinite(final Vec3 v) {
        return Double.isFinite(v.x) && Double.isFinite(v.y) && Double.isFinite(v.z);
    }

    public static Vec3 composedCameraPosition(final Entity cameraEntity, final float partialTicks, final float eyeHeightOld, final float eyeHeight) {
        if (cameraEntity == null || !resolve()) {
            return null;
        }
        try {
            final boolean onSubLevel = SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(cameraEntity) != null || SableCompanion.INSTANCE.getContainingClient(cameraEntity) != null;
            if (!onSubLevel) {
                return null;
            }

            final Avatar avatar = AvatarManager.getAvatar(cameraEntity);
            if (!RenderUtils.vanillaModelAndScript(avatar)) {
                return null;
            }

            final FiguraVec3 pivot = avatar.luaRuntime.renderer.cameraPivot;
            final FiguraVec3 offset = avatar.luaRuntime.renderer.cameraOffsetPivot;
            final boolean hasPivot = pivot != null && pivot.notNaN();
            final boolean hasOffset = offset != null && offset.notNaN();
            if (!hasPivot && !hasOffset) {
                return null;
            }

            final double x;
            final double y;
            final double z;
            if (hasPivot) {
                x = pivot.x;
                y = pivot.y;
                z = pivot.z;
            } else {
                final Vec3 base = subLevelEye(cameraEntity, partialTicks, eyeHeightOld, eyeHeight);
                if (base == null) {
                    return null;
                }
                x = base.x;
                y = base.y;
                z = base.z;
            }

            final Vec3 composed = hasOffset ? new Vec3(x + offset.x, y + offset.y, z + offset.z) : new Vec3(x, y, z);
            return isFinite(composed) ? composed : null;
        } catch (final Throwable t) {
            return null;
        }
    }

    public static void applySubLevelCameraRotation(final Entity cameraEntity, final float partialTicks) {
        if (cameraEntity == null || !resolve()) {
            return;
        }
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || cameraEntity != mc.player) {
            return;
        }

        try {
            final Avatar avatar = AvatarManager.getAvatar(cameraEntity);
            if (!RenderUtils.vanillaModelAndScript(avatar)) {
                return;
            }

            final Entity vehicle = cameraEntity.getVehicle();
            if (ughBetter3rdPersonIsAnnoying() && (vehicle == null || vehicle instanceof Horse || vehicle instanceof Pig)) {
                if (subLevelCameraRotSet.remove(cameraEntity.getUUID())) {avatar.luaRuntime.renderer.cameraRot = null;}
                return;
            }

            final RendererAPI renderer = avatar.luaRuntime.renderer;
            final boolean onSubLevel = SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(cameraEntity) != null || SableCompanion.INSTANCE.getContainingClient(cameraEntity) != null;

            if (onSubLevel) {
                final boolean backwards = renderer.isCameraBackwards();
                final float pitch = Mth.lerp(partialTicks, cameraEntity.xRotO, cameraEntity.getXRot());
                final float yaw = Mth.lerp(partialTicks, cameraEntity.yRotO, cameraEntity.getYRot());
                final double rx = backwards ? -pitch : pitch;
                final double ry = backwards ? yaw + 180.0 : yaw;
                renderer.cameraRot = FiguraVec3.of(rx, ry, 0.0);
                subLevelCameraRotSet.add(cameraEntity.getUUID());
            } else if (subLevelCameraRotSet.remove(cameraEntity.getUUID())) {
                renderer.cameraRot = null;
            }
        } catch (final Throwable t) {
            // ignore
        }
    }

    public record SubLevelHit(BlockHitResult result, BlockState state) {}

    public static SubLevelHit subLevelRaycast(final Entity owner, final Vec3 start, final Vec3 end, final ClipContext.Block blockType, final ClipContext.Fluid fluidType) {
        if (owner == null || start == null || end == null || !resolve()) {
            return null;
        }
        try {
            final Level level = owner.level();
            final ClientSubLevelAccess contained = SableCompanion.INSTANCE.getContainingClient(owner);

            if (contained instanceof final SubLevel subLevel) {
                final Level plot = subLevel.getLevel();
                if (plot == null) {
                    return null;
                }
                final ClipContext context = new ClipContext(start, end, blockType, fluidType, new Marker(EntityType.MARKER, plot));
                final BlockHitResult hit = clipIn(plot, context);
                return new SubLevelHit(hit, plot.getBlockState(hit.getBlockPos()));
            }

            final ClipContext context = new ClipContext(start, end, blockType, fluidType, new Marker(EntityType.MARKER, level));
            final BlockHitResult hit = level.clip(context);
            return new SubLevelHit(projectHitToWorld(level, hit), level.getBlockState(hit.getBlockPos()));
        } catch (final Throwable t) {
            return null;
        }
    }

    public static EntityHitResult subLevelEntityRaycast(final Entity owner, final Vec3 start, final Vec3 end, final Predicate<Entity> predicate, final double maxDistance) {
        if (owner == null || start == null || end == null || !resolve()) {
            return null;
        }
        try {
            final ClientSubLevelAccess contained = SableCompanion.INSTANCE.getContainingClient(owner);
            if (!(contained instanceof final SubLevel subLevel)) {
                return null;
            }

            final Level plot = subLevel.getLevel();
            if (plot == null) {
                return null;
            }

            final Marker source = new Marker(EntityType.MARKER, plot);
            final AABB box = new AABB(start, end);

            Entity hitEntity = null;
            Vec3 hitLocation = null;
            double closest = maxDistance;

            for (final Entity candidate : plot.getEntities(source, box, predicate)) {
                if (SableCompanion.INSTANCE.getContainingClient(candidate) != subLevel) {
                    continue;
                }

                final AABB candidateBox = candidate.getBoundingBox().inflate(candidate.getPickRadius());
                final Optional<Vec3> clip = candidateBox.clip(start, end);
                if (candidateBox.contains(start)) {
                    if (closest >= 0.0) {
                        hitEntity = candidate;
                        hitLocation = clip.orElse(start);
                        closest = 0.0;
                    }
                } else if (clip.isPresent()) {
                    final Vec3 location = clip.get();
                    final double distance = start.distanceToSqr(location);
                    if (distance < closest || closest == 0.0) {
                        hitEntity = candidate;
                        hitLocation = location;
                        closest = distance;
                    }
                }
            }

            return hitEntity == null ? null : new EntityHitResult(hitEntity, hitLocation);
        } catch (final Throwable t) {
            return null;
        }
    }

    private static BlockHitResult projectHitToWorld(final Level level, final BlockHitResult hit) {
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return hit;
        }
        if (SableCompanion.INSTANCE.getContaining(level, hit.getBlockPos()) == null) {
            return hit;
        }
        final Vec3 worldLoc = SableCompanion.INSTANCE.projectOutOfSubLevel(level, hit.getLocation());
        final Vec3 localNormal = Vec3.atLowerCornerOf(hit.getDirection().getNormal());
        final Vec3 worldNormal = SableCompanion.INSTANCE
                .projectOutOfSubLevel(level, hit.getLocation().add(localNormal.scale(0.01)))
                .subtract(worldLoc);
        final Direction worldDir = worldNormal.lengthSqr() < 1.0E-12
                ? hit.getDirection()
                : Direction.getNearest(worldNormal.x, worldNormal.y, worldNormal.z);
        return new BlockHitResult(worldLoc, worldDir, BlockPos.containing(worldLoc), hit.isInside());
    }

    private static BlockHitResult clipIn(final BlockGetter level, final ClipContext context) {
        return BlockGetter.traverseBlocks(context.getFrom(), context.getTo(), context,
                (ctx, pos) -> {
                    final BlockState state = level.getBlockState(pos);
                    final FluidState fluid = level.getFluidState(pos);
                    final Vec3 from = ctx.getFrom();
                    final Vec3 to = ctx.getTo();

                    final VoxelShape blockShape = ctx.getBlockShape(state, level, pos);
                    final BlockHitResult blockHit = level.clipWithInteractionOverride(from, to, pos, blockShape, state);
                    final VoxelShape fluidShape = ctx.getFluidShape(fluid, level, pos);
                    final BlockHitResult fluidHit = fluidShape.clip(from, to, pos);

                    final double blockDist = blockHit == null ? Double.MAX_VALUE : from.distanceToSqr(blockHit.getLocation());
                    final double fluidDist = fluidHit == null ? Double.MAX_VALUE : from.distanceToSqr(fluidHit.getLocation());
                    return blockDist <= fluidDist ? blockHit : fluidHit;
                },
                ctx -> {
                    final Vec3 diff = ctx.getFrom().subtract(ctx.getTo());
                    return BlockHitResult.miss(ctx.getTo(), Direction.getNearest(diff.x, diff.y, diff.z), BlockPos.containing(ctx.getTo()));
                });
    }
}
