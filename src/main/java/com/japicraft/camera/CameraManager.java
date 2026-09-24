package com.japicraft.camera;

import com.japicraft.Wayfare;
import com.japicraft.avatar.AvatarManager;
import com.japicraft.packet.EntityPacketManager;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.cube.SlimeMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;

import java.util.List;

public class CameraManager {
    public static final Pos SPAWN = AvatarManager.SPAWN.withPitch(90.0F);
    private final Player player;
    private final Entity camera = new Entity(EntityType.MANNEQUIN);
    private final Entity bounds = new Entity(EntityType.SLIME);
    private final Entity thirdPersonHint = new Entity(EntityType.TEXT_DISPLAY);
    private final Entity thirdPersonHintBackground = new Entity(EntityType.TEXT_DISPLAY);

    public CameraManager(Player player) {
        this.player = player;

        // spectator target and click detection
        camera.setInvisible(true);
        camera.setHasPhysics(false);
        camera.setNoGravity(true);
        spawn(camera);

        // adjustable camera distance
        bounds.setInvisible(true);
        bounds.setHasPhysics(false);
        bounds.setNoGravity(true);
        bounds.editEntityMeta(SlimeMeta.class, meta -> meta.setSize(10));
        spawn(bounds);

        // hint for player to exit F5
        thirdPersonHint.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setText(Component.text("Press ").append(Component.keybind("key.togglePerspective")).append(Component.text(" to return to the game!")));
            meta.setPosRotInterpolationDuration(20);
            meta.setBackgroundColor(0);
            meta.setShadow(true);
        });
        spawn(thirdPersonHint);

        // obscure screen when in F5
        thirdPersonHintBackground.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setText(Component.text("■").font(Wayfare.FONT));
            meta.setPosRotInterpolationDuration(20);
            meta.setBackgroundColor(0);
            meta.setScale(new Vec(10));
        });
        spawn(thirdPersonHintBackground);
    }

    public void bindToAvatar(int avatarId) {
        EntityPacketManager.addPassengers(player, avatarId, List.of(
            bounds.getEntityId()
        ));
        EntityPacketManager.addPassengers(player, bounds.getEntityId(), List.of(
            camera.getEntityId()
        ));
        EntityPacketManager.addPassengers(player, camera.getEntityId(), List.of(
            thirdPersonHintBackground.getEntityId(),
            thirdPersonHint.getEntityId(),
            player.getEntityId()
        ));
        EntityPacketManager.spectate(player, camera);
    }

    private void spawn(Entity entity) {
        EntityPacketManager.spawn(player, entity, CameraManager.SPAWN);
    }
}
