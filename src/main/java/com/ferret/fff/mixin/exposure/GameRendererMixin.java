package com.ferret.fff.mixin.exposure;

import com.ferret.fff.exposure.ExposurePhotoState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void onRenderLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
        try {
            Class<?> cls = Class.forName("io.github.mortuusars.exposure.client.capture.task.BackgroundScreenshotCaptureTask");
            Boolean capturing = (Boolean) cls.getMethod("isCapturing").invoke(null);
            if (Boolean.TRUE.equals(capturing)) {
                ExposurePhotoState.setCapturing(true);
            }
        } catch (Throwable ignored) {
        }
    }
}
