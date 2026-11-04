package com.ferret.fff.mixin.exposure;

import com.ferret.fff.exposure.ExposurePhotoState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(targets = "io.github.mortuusars.exposure.client.capture.task.DirectScreenshotCaptureTask", remap = false)
public abstract class DirectScreenshotCaptureTaskMixin {
    @Shadow
    protected CompletableFuture<?> future;

    @Inject(method = "execute", at = @At("HEAD"), remap = false)
    private void onExecuteStart(CallbackInfoReturnable<?> cir) {
        ExposurePhotoState.setCapturing(true);
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void onTickStart(CallbackInfo ci) {
        ExposurePhotoState.setCapturing(future != null && !future.isDone());
    }

    @Inject(method = "tick", at = @At("RETURN"), remap = false)
    private void onTickEnd(CallbackInfo ci) {
        if (future == null || future.isDone()) {
            ExposurePhotoState.setCapturing(false);
        }
    }
}
