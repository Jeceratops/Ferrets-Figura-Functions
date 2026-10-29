package com.ferret.fff.exposure;

import net.neoforged.fml.ModList;

public final class ExposureCompat {
    private static Boolean loaded;

    private ExposureCompat() {}

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded("exposure");
        }
        return loaded;
    }

    public static boolean isTakingPhoto() {
        if (!isLoaded() || ExposurePhotoState.isCapturing()) {
            return ExposurePhotoState.isCapturing();
        }
        try {
            Class<?> cls = Class.forName("io.github.mortuusars.exposure.client.capture.task.BackgroundScreenshotCaptureTask");
            Object result = cls.getMethod("isCapturing").invoke(null);
            if (result instanceof Boolean capturing && capturing) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        return ExposurePhotoState.isCapturing();
    }
}
