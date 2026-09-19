package com.japicraft.camera;

import com.japicraft.Wayfare;
import com.japicraft.avatar.AvatarManager;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.cube.SlimeMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.InstanceContainer;

public class CameraManager {
    public static final Pos SPAWN = AvatarManager.SPAWN.withPitch(90.0F);
    private final CameraEntity camera = new CameraEntity(EntityType.MANNEQUIN, 0.0F, 90.0F);
    private final Entity ThirdPersonHintBackground = new Entity(EntityType.TEXT_DISPLAY);
    private final Entity ThirdPersonHint = new Entity(EntityType.TEXT_DISPLAY);
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
        // provides F5 hint background
        ThirdPersonHintBackground.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setText(Component.text("■").font(Wayfare.FONT));
            meta.setPosRotInterpolationDuration(20);
            meta.setBackgroundColor(0);
            meta.setScale(new Vec(10));
        });
        ThirdPersonHintBackground.setInstance(instance, CameraManager.SPAWN);
        // provides F5 hint
        ThirdPersonHint.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setText(Component.text("Press ").append(Component.keybind("key.togglePerspective")).append(Component.text(" to return to the game!")));
            meta.setPosRotInterpolationDuration(20);
            meta.setBackgroundColor(0);
        });
        ThirdPersonHint.setInstance(instance, CameraManager.SPAWN);
    }

    public void addPassenger(Player player) {
        bounds.addPassenger(camera);
        camera.addPassenger(ThirdPersonHintBackground);
        camera.addPassenger(ThirdPersonHint);
        player.spectate(camera);
        // for mouse input
        camera.addPassenger(player);
    }

    public Entity getBounds() {
        return bounds;
    }
}
