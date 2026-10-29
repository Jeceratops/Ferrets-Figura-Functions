package com.ferret.fff.mixin.exposure;

import com.ferret.fff.exposure.ExposurePhotoState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "io.github.mortuusars.exposure.client.capture.task.DirectScreenshotCaptureTask", remap = false)
public class DirectScreenshotCaptureTaskMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false)
    private void onExecuteStart(CallbackInfoReturnable<?> cir) { ExposurePhotoState.setCapturing(true); }

    @Inject(method = "execute", at = @At("RETURN"), remap = false)
    private void onExecuteEnd(CallbackInfoReturnable<?> cir) { ExposurePhotoState.setCapturing(false); }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;takeScreenshot(Lcom/mojang/blaze3d/pipeline/RenderTarget;)Lcom/mojang/blaze3d/platform/NativeImage;", remap = true), remap = false)
    private void onTakeScreenshot(CallbackInfo ci) { ExposurePhotoState.setCapturing(true); }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;takeScreenshot(Lcom/mojang/blaze3d/pipeline/RenderTarget;)Lcom/mojang/blaze3d/platform/NativeImage;", shift = At.Shift.AFTER, remap = true), remap = false)
    private void onAfterTakeScreenshot(CallbackInfo ci) { ExposurePhotoState.setCapturing(false); }
}
