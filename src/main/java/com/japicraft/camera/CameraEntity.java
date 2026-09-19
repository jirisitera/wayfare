package com.japicraft.camera;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class CameraEntity extends Entity {
    private final float yaw;
    private final float pitch;

    public CameraEntity(EntityType type, float yaw, float pitch) {
        super(type);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    private Pos getLockedRotation(Pos pos) {
        return pos.withView(yaw, pitch);
    }

    @Override
    protected void setPositionInternal(@NonNull Pos pos, float headRotation) {
        super.setPositionInternal(getLockedRotation(pos), yaw);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void refreshPosition(@NotNull Pos pos) {
        super.refreshPosition(getLockedRotation(pos));
    }
}
