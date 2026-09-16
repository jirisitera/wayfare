package com.japicraft.camera;

public record Coordinates(double x, double y) {
    private static final double SCREEN_X = 360.0;
    private static final double SCREEN_Y = 180.0;
    private static final double MIN_YAW = 0.0 - SCREEN_X / 2.0;
    private static final double MIN_PITCH = 0.0 - SCREEN_Y / 2.0;

    public static Coordinates fromRotation(float yaw, float pitch) {
        double x = (yaw - MIN_YAW) / SCREEN_X;
        double y = (pitch - MIN_PITCH) / SCREEN_Y;
        // wrap cursor around edges
        x = (x % 1.0 + 1.0) % 1.0;
        return new Coordinates(x, y);
    }
}
