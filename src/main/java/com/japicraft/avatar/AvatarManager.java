package com.japicraft.avatar;

import com.japicraft.ability.AttackManager;
import com.japicraft.ability.BulletManager;
import com.japicraft.camera.Coordinates;
import com.japicraft.server.InstanceRegistry;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.avatar.MannequinMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.ResolvableProfile;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

public class AvatarManager {
    public static final Pos SPAWN = new Pos(0.0, InstanceRegistry.MAX_HEIGHT + 0.5, 0.0, 0.0F, 0.0F);
    public static final Tag<AvatarManager> MANAGER_TAG = Tag.Transient("wayfare:avatarManager");
    private final LivingEntity avatar = new LivingEntity(EntityType.MANNEQUIN);
    private final Player player;

    public AvatarManager(Player player) {
        this.player = player;
        // setup avatar
        PlayerSkin playerSkin = player.getSkin();
        if (playerSkin != null) {
            avatar.editEntityMeta(MannequinMeta.class, meta -> meta.setProfile(new ResolvableProfile(playerSkin)));
        }
        avatar.setItemInMainHand(ItemStack.of(Material.DIAMOND_SWORD));
        // spawn avatar
        avatar.setInstance(player.getInstance(), AvatarManager.SPAWN);
        // assemble passengers
        avatar.addPassenger(player);
    }

    public static float calculateAvatarYaw(float yaw, float pitch) {
        double x = 0.5 - yaw;
        double z = 0.5 - pitch;
        if (x == 0.0 && z == 0.0) {
            return 0.0F;
        }
        double angle = Math.atan2(z, x);
        return (float) (Math.toDegrees(angle) - 90.0);
    }

    public Entity getAvatar() {
        return avatar;
    }

    public void remove() {
        avatar.remove();
    }

    public void shoot(float yaw, float pitch, int count) {
        Coordinates coordinates = Coordinates.fromRotation(yaw, pitch);
        Vec velocity = avatar.getVelocity();

        BulletManager.spawn(player, player.getUsername(), 0, 0, BulletManager.calculateVelocity(coordinates, velocity, 1.0));

        Scheduler scheduler = avatar.scheduler();
        for (int i = 1; i < count; i++) {
            scheduler.scheduleTask(() -> BulletManager.spawn(player, player.getUsername(), 0, 0, BulletManager.calculateVelocity(coordinates, velocity, 1.0)), TaskSchedule.tick(i * 2), TaskSchedule.stop());
        }
    }

    public void attack(float yaw, float pitch) {
        float avatarYaw = calculateAvatarYaw(yaw, pitch);
        avatar.setView(avatarYaw, 0);
        avatar.swingMainHand();
        AttackManager.show(player, avatarYaw);
        AttackManager.damage(player, avatarYaw);
    }
}
