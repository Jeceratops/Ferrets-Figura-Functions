package com.ferret.fff.mixin.sable;

import com.ferret.fff.SableCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public abstract class CameraSetupCompatMixin {

    @Shadow
    private float eyeHeightOld;

    @Shadow
    private float eyeHeight;

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @WrapOperation(
            method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"
            )
    )
    private void fff$composeCameraWithSubLevel(final Camera instance, final double x, final double y, final double z,
                                               final Operation<Void> original,
                                               @Local(argsOnly = true) final Entity entity,
                                               @Local(argsOnly = true) final float partialTicks) {
        final Vec3 composed = SableCompat.composedCameraPosition(entity, partialTicks, this.eyeHeightOld, this.eyeHeight);

        if (composed != null) {
            this.setPosition(composed.x, composed.y, composed.z);
        } else {
            original.call(instance, x, y, z);
        }
    }

    @org.spongepowered.asm.mixin.injection.Inject(
            method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
            at = @At("HEAD")
    )
    private void fff$applySubLevelCameraRot(final net.minecraft.world.level.BlockGetter level, final Entity entity,
                                            final boolean thirdPerson, final boolean mirrored,
                                            final float partialTicks,
                                            final org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        SableCompat.applySubLevelCameraRotation(entity, partialTicks);
    }
}
