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
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;

import java.util.List;

public class CameraManager {
    public static final Pos SPAWN = AvatarManager.SPAWN.withPitch(90.0F);
    private final Player player;
    private final Entity camera = new Entity(EntityType.MANNEQUIN);
    private final Entity bounds = new Entity(EntityType.INTERACTION);
    private final Entity thirdPersonHintTop = new Entity(EntityType.TEXT_DISPLAY);
    private final Entity thirdPersonHintBottom = new Entity(EntityType.TEXT_DISPLAY);
    private final Entity thirdPersonHintBackground = new Entity(EntityType.TEXT_DISPLAY);

    public CameraManager(Player player) {
        this.player = player;
        // spectator target and click detection
        camera.setInvisible(true);
        camera.setNoGravity(true);
        spawn(camera);
        EntityPacketManager.scale(player, camera.getEntityId(), 7.5);

        bounds.setInvisible(true);
        bounds.setNoGravity(true);
        bounds.editEntityMeta(InteractionMeta.class, meta -> meta.setHeight(10.0F));
        spawn(bounds);

        // hint for player to exit F5
        thirdPersonHintBottom.editEntityMeta(TextDisplayMeta.class, meta -> {
            setTextMeta(meta, Component.text("Press ").append(Component.keybind("key.togglePerspective")).append(Component.text(" to return to the game!")), new Vec(1.5));
            meta.setShadow(true);
        });
        spawn(thirdPersonHintBottom);
        // another hint for player to exit F5
        thirdPersonHintTop.editEntityMeta(TextDisplayMeta.class, meta -> {
            setTextMeta(meta, Component.text("Press ").append(Component.keybind("key.togglePerspective")).append(Component.text(" to return to the game!")), new Vec(1.5));
            meta.setShadow(true);
        });
        spawn(thirdPersonHintTop);
        // obscure screen when in F5
        thirdPersonHintBackground.editEntityMeta(TextDisplayMeta.class, meta -> setTextMeta(meta, Component.text("■").font(Wayfare.FONT), new Vec(10.0)));
        spawn(thirdPersonHintBackground);
        // build tower
        assemble();
    }

    private void setTextMeta(TextDisplayMeta meta, Component text, Vec scale) {
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        meta.setPosRotInterpolationDuration(20);
        meta.setBackgroundColor(0);
        meta.setText(text);
        meta.setScale(scale);
    }

    public void assemble() {
        int cameraId = camera.getEntityId();
        // create "avatar tower"
        EntityPacketManager.addPassengers(player, player.getEntityId(), List.of(cameraId, bounds.getEntityId()));
        EntityPacketManager.addPassengers(player, cameraId, List.of(thirdPersonHintBottom.getEntityId(), thirdPersonHintBackground.getEntityId(), thirdPersonHintTop.getEntityId()));
        // ensure players cannot move their camera
        EntityPacketManager.spectate(player, cameraId);
    }

    private void spawn(Entity entity) {
        EntityPacketManager.spawn(player, entity, CameraManager.SPAWN);
    }
}
