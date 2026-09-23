package com.japicraft.avatar;

import com.japicraft.camera.Coordinates;
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
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

public class AvatarManager {
    public static final Pos SPAWN = new Pos(0.0, InstanceManager.MAX_HEIGHT + 0.5, 0.0, 0.0F, 0.0F);
    public static final double SNEAK_SPEED = 5.0;
    public static final double MOVE_SPEED = 7.5;
    public static final double SPRINT_SPEED = 10.0;
    private final Entity avatar = new Entity(EntityType.MANNEQUIN);
    private final InstanceContainer instance;
    private boolean lastSneaking;
    private boolean lastSprinting;
    private long lastTaskTime;

    public AvatarManager(InstanceContainer instance) {
        this.instance = instance;
        avatar.setInstance(instance, AvatarManager.SPAWN);
    }

    public long nextTask() {
        return ++lastTaskTime;
    }

    public boolean isTask(long time) {
        return lastTaskTime == time;
    }

    public void setSkin(Player player) {
        PlayerSkin playerSkin = player.getSkin();
        if (playerSkin == null) {
            return;
        }
        avatar.editEntityMeta(MannequinMeta.class, meta -> meta.setProfile(new ResolvableProfile(playerSkin)));
    }

    public void shoot(float yaw, float pitch, int count) {
        Coordinates coordinates = Coordinates.fromRotation(yaw, pitch);
        Vec velocity = avatar.getVelocity();
        Pos position = avatar.getPosition();
        Scheduler scheduler = avatar.scheduler();

        BulletManager.spawn(instance, position, BulletManager.calculateVelocity(coordinates, velocity, 1.0));

        for (int i = 1; i < count; i++) {
            scheduler.scheduleTask(() -> BulletManager.spawn(instance, position, BulletManager.calculateVelocity(coordinates, velocity, 1.0)), TaskSchedule.tick(i * 2), TaskSchedule.stop());
        }
    }

    public void update(int x, int z, boolean sneaking, boolean sprinting) {
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
    }

    public void addPassenger(Entity entity) {
        avatar.addPassenger(entity);
    }
}
