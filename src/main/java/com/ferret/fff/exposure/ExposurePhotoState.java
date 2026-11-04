package com.ferret.fff.exposure;

public final class ExposurePhotoState {
    private static volatile boolean capturing;

    private ExposurePhotoState() {}

    public static boolean isCapturing() {
        return capturing;
    }

    public static void setCapturing(boolean value) {
        capturing = value;
    }
}
