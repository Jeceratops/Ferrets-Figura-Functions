package com.ferret.fff.exposure;

import net.neoforged.fml.ModList;

import java.lang.reflect.Method;

public final class ExposureCompat {
    public static final String SCREENSHOT_RENDER_MODE = "EXPOSURE_SCREENSHOT";

    private static Boolean loaded;
    private static boolean captureMethodResolved;
    private static Method captureMethod;

    private ExposureCompat() {}

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded("exposure");
        }
        return loaded;
    }

    public static String renderContext(String original) {
        return isTakingPhoto() ? SCREENSHOT_RENDER_MODE : original;
    }

    public static boolean isTakingPhoto() {
        if (!isLoaded()) {
            return false;
        }
        return ExposurePhotoState.isCapturing() || externalCapturing();
    }

    private static boolean externalCapturing() {
        if (!captureMethodResolved) {
            captureMethodResolved = true;
            try {
                Class<?> cls = Class.forName("io.github.mortuusars.exposure.client.capture.task.BackgroundScreenshotCaptureTask");
                captureMethod = cls.getMethod("isCapturing");
            } catch (Throwable ignored) {
                captureMethod = null;
            }
        }
        if (captureMethod == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(captureMethod.invoke(null));
        } catch (Throwable ignored) {
            return false;
        }
    }
}
