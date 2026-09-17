package com.japicraft.avatar;

import com.japicraft.server.InstanceManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.avatar.MannequinMeta;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.player.ResolvableProfile;
import net.minestom.server.timer.TaskSchedule;

public class AvatarManager {
    public static final Pos SPAWN = new Pos(0.0, InstanceManager.MAX_HEIGHT + 1.0, 0.0, 0.0F, 0.0F);
    public static final double SNEAK_SPEED = 3.5;
    public static final double MOVE_SPEED = 5.0;
    public static final double SPRINT_SPEED = 7.5;
    private final Entity avatar = new Entity(EntityType.MANNEQUIN);
    private boolean lastSneaking;
    private boolean lastSprinting;

    public AvatarManager(InstanceContainer instance) {
        avatar.setInstance(instance, SPAWN);
    }

    public Entity getAvatar() {
        return avatar;
    }

    public void setSkin(Player player) {
        PlayerSkin playerSkin = player.getSkin();
        if (playerSkin == null) {
            return;
        }
        avatar.editEntityMeta(MannequinMeta.class, meta -> meta.setProfile(new ResolvableProfile(playerSkin)));
    }

    public void initializeMovement(Player player) {
        player.scheduler().submitTask(() -> {
            if (!player.isOnline()) {
                return TaskSchedule.stop();
            }
            int x = player.getTag(MovementManager.INPUT_X);
            int z = player.getTag(MovementManager.INPUT_Z);
            boolean sneaking = player.getTag(MovementManager.INPUT_SNEAK);
            boolean sprinting = player.getTag(MovementManager.INPUT_SPRINT) && !sneaking;
            // only update when movement is detected
            if (x != 0 || z != 0) {
                Vec direction = new Vec(x, 0, z).normalize();
                avatar.lookAt(avatar.getPosition().add(0, avatar.getEyeHeight(), 0).add(direction));
                avatar.setVelocity(sprinting ? direction.mul(SPRINT_SPEED) : sneaking ? direction.mul(SNEAK_SPEED) : direction.mul(MOVE_SPEED));
            }
            if (sneaking != lastSneaking) {
                avatar.setSneaking(sneaking);
                lastSneaking = sneaking;
            }
            if (sprinting != lastSprinting) {
                avatar.setSprinting(sprinting);
                lastSprinting = sprinting;
            }
            return TaskSchedule.tick(1);
        });
    }
}
