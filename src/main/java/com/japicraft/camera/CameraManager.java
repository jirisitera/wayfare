package com.japicraft.camera;

import com.japicraft.avatar.AvatarManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.timer.TaskSchedule;

public class CameraManager {
    public static final double MAX_HEIGHT = AvatarManager.SPAWN.y() + 9.0;
    private static final Pos ANCHOR_SPAWN = AvatarManager.SPAWN.withY(MAX_HEIGHT);
    private static final Pos CAMERA_SPAWN = ANCHOR_SPAWN.withPitch(90.0F);
    private final Entity camera = new Entity(EntityType.ITEM_DISPLAY);
    private final Entity anchor = new Entity(EntityType.ITEM_DISPLAY);
    private final Entity bounds = new Entity(EntityType.INTERACTION);

    public CameraManager(InstanceContainer instance) {
        // provides spectator target
        addDisplayMeta(camera);
        camera.setInstance(instance, CAMERA_SPAWN);
        // provides correct starting yaw and pitch
        addDisplayMeta(anchor);
        anchor.setInstance(instance, ANCHOR_SPAWN);
        // provides right click detection
        bounds.setNoGravity(true);
        bounds.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setWidth(5);
            meta.setHeight(5);
            meta.setResponse(true);
        });
        bounds.setInstance(instance, CAMERA_SPAWN);
    }

    public Entity getCamera() {
        return camera;
    }

    public Entity getAnchor() {
        return anchor;
    }

    public void follow(Player player, Entity entity) {
        player.scheduler().submitTask(() -> {
            if (!player.isOnline()) {
                return TaskSchedule.stop();
            }
            Pos position = entity.getPosition();
            Coordinates coordinates = new Coordinates(position.x(), position.z());
            move(camera, coordinates);
            move(anchor, coordinates);
            move(bounds, coordinates);
            return TaskSchedule.tick(1);
        });
    }

    private void move(Entity entity, Coordinates coordinates) {
        entity.teleport(entity.getPosition().withCoord(coordinates.x(), MAX_HEIGHT, coordinates.y()));
    }

    private void addDisplayMeta(Entity entity) {
        entity.setNoGravity(true);
        entity.editEntityMeta(ItemDisplayMeta.class, meta -> meta.setPosRotInterpolationDuration(2));
    }
}
