package com.japicraft.camera;

import com.japicraft.avatar.AvatarManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.cube.SlimeMeta;
import net.minestom.server.instance.InstanceContainer;

public class CameraManager {
    public static final Pos SPAWN = AvatarManager.SPAWN.withPitch(90.0F);
    private final CameraEntity camera = new CameraEntity(EntityType.MANNEQUIN, 0.0F, 90.0F);
    private final Entity bounds = new Entity(EntityType.SLIME);

    public CameraManager(InstanceContainer instance) {
        // provides spectator target
        camera.setInvisible(true);
        camera.setHasPhysics(false);
        camera.setNoGravity(true);
        camera.setInstance(instance, CameraManager.SPAWN);
        // provides right click detection
        bounds.setInvisible(true);
        bounds.setHasPhysics(false);
        bounds.setNoGravity(true);
        bounds.editEntityMeta(SlimeMeta.class, meta -> meta.setSize(20));
        bounds.setInstance(instance, CameraManager.SPAWN);
    }

    public Entity getCamera() {
        return camera;
    }

    public Entity getBounds() {
        return bounds;
    }
}
