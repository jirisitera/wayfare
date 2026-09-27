package com.japicraft.avatar;

import com.japicraft.ability.BulletManager;
import com.japicraft.game.PointManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.TaskSchedule;

import java.util.Collection;

public class MountManager {
    public static final Tag<MountManager> MANAGER_TAG = Tag.Transient("wayfare:mountManager");
    public static final double SNEAK_SPEED = 5.0;
    public static final double MOVE_SPEED = 7.5;
    public static final double SPRINT_SPEED = 10.0;
    private static final Tag<String> MOUNT_TAG = Tag.String("mount");
    private final MountEntity mount;
    private boolean lastSneaking;
    private boolean lastSprinting;
    private long lastTaskTime;

    public MountManager(Player player) {
        String mountName = player.getTag(MOUNT_TAG);

        mount = new MountEntity(Mount.valueOf(mountName == null ? Mount.Horse.name() : mountName).getType());

        mount.setInstance(player.getInstance(), AvatarManager.SPAWN);

        scheduleHitChecking(player);
    }

    public long nextTask() {
        return ++lastTaskTime;
    }

    public boolean isTask(long time) {
        return lastTaskTime == time;
    }

    public void addPassenger(Entity entity) {
        mount.addPassenger(entity);
    }

    public void remove() {
        mount.remove();
    }

    public void update(int x, int z, boolean sneaking, boolean sprinting) {
        if (x != 0 || z != 0) {
            Vec direction = new Vec(x, 0, z).normalize();
            Pos lookTarget = mount.getPosition().add(direction).add(0, mount.getEyeHeight(), 0);
            mount.lookAt(lookTarget);
            mount.setVelocity(sprinting ? direction.mul(SPRINT_SPEED) : sneaking ? direction.mul(SNEAK_SPEED) : direction.mul(MOVE_SPEED));
        }
        if (sneaking != lastSneaking) {
            mount.setSneaking(sneaking);
            lastSneaking = sneaking;
        }
        if (sprinting != lastSprinting) {
            mount.setSprinting(sprinting);
            lastSprinting = sprinting;
        }
    }

    public void scheduleHitChecking(Player player) {
        mount.scheduler().submitTask(() -> {
            if (!player.isOnline()) {
                return TaskSchedule.stop();
            }
            Collection<Entity> entities = player.getInstance().getNearbyEntities(mount.getPosition().add(0, 0.5, 0), 0.5);
            for (Entity entity : entities) {
                String source = entity.getTag(BulletManager.SOURCE);
                if (source == null || source.equals(player.getUsername())) {
                    continue;
                }
                player.sendMessage("You got hit by: " + source + "!");
                PointManager.remove(player, 1);
            }
            return TaskSchedule.tick(2);
        });
    }

    public enum Mount {
        Horse(EntityType.HORSE),
        Strider(EntityType.STRIDER);
        private final EntityType type;

        Mount(EntityType type) {
            this.type = type;
        }

        public EntityType getType() {
            return type;
        }
    }
}
