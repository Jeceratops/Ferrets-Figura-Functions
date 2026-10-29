package com.ferret.fff.exposure;

public final class ExposurePhotoState {
    private static final ThreadLocal<Boolean> CAPTURING = ThreadLocal.withInitial(() -> false);

    private ExposurePhotoState() {}

    public static boolean isCapturing() {
        return Boolean.TRUE.equals(CAPTURING.get());
    }

    public static void setCapturing(boolean capturing) {
        if (capturing) {
            CAPTURING.set(true);
        } else {
            CAPTURING.remove();
        }
    }
}
