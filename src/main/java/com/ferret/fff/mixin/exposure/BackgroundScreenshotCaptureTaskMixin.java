package com.ferret.fff.mixin.exposure;

import com.ferret.fff.exposure.ExposurePhotoState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "io.github.mortuusars.exposure.client.capture.task.BackgroundScreenshotCaptureTask", remap = false)
public class BackgroundScreenshotCaptureTaskMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false)
    private void onExecuteStart(CallbackInfoReturnable<?> cir) { ExposurePhotoState.setCapturing(true); }

    @Inject(method = "execute", at = @At("RETURN"), remap = false)
    private void onExecuteEnd(CallbackInfoReturnable<?> cir) { ExposurePhotoState.setCapturing(false); }
}
