package com.japicraft.camera;

public record Coordinates(double x, double y) {
    private static final float SCREEN_X = 360.0F;
    private static final float SCREEN_Y = 180.0F;
    private static final float MIN_YAW = 0.0F - SCREEN_X / 2.0F;
    private static final float MIN_PITCH = 0.0F - SCREEN_Y / 2.0F;

    public static Coordinates fromRotation(float yaw, float pitch) {
        float x = (yaw - MIN_YAW) / SCREEN_X;
        float y = (pitch - MIN_PITCH) / SCREEN_Y;
        // wrap cursor around edges
        x = (x % 1.0F + 1.0F) % 1.0F;
        return new Coordinates(x, y);
    }
}
