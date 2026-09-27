package com.japicraft.ability;

import com.japicraft.Wayfare;
import com.japicraft.camera.Coordinates;
import com.japicraft.packet.ParticlePacketManager;
import com.japicraft.server.InstanceRegistry;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.particle.Particle;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class BulletManager {
    public static final Tag<String> SOURCE = Tag.String("source");
    private static final double HEIGHT = InstanceRegistry.MAX_HEIGHT + 0.5;
    private static final double SPEED = 20.0;
    private static final double DRIFT = 1.5;
    private static final double SPREAD = 10.0;

    public static Vec calculateVelocity(Coordinates coordinates, Vec velocity, double speedMultiplier) {
        double x = 0.5 - coordinates.x();
        double z = 0.5 - coordinates.y();
        // prevent division by zero
        if (x == 0.0 && z == 0.0) {
            z = 1.0;
        }
        double angle = Math.atan2(z, x) + (ThreadLocalRandom.current().nextDouble() * 2.0 - 1.0) * Math.toRadians(BulletManager.SPREAD);

        double speed = BulletManager.SPEED * speedMultiplier;
        Vec direction = new Vec(Math.cos(angle) * speed, 0.0, Math.sin(angle) * speed);

        return direction.add(velocity.x() * BulletManager.DRIFT, 0.0, velocity.z() * BulletManager.DRIFT);
    }

    public static void scheduleSpawning(Player player) {
        player.scheduler().submitTask(() -> {
            if (!player.isOnline()) {
                return TaskSchedule.stop();
            }
            BulletManager.spawnIncoming(player, 20.0, 15.0, 30.0);
            return TaskSchedule.tick(200);
        });
    }

    public static void spawnIncoming(Player target, double radius, double speed, double maxSpread) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        double angle = random.nextDouble(2 * Math.PI);
        double offsetX = Math.cos(angle) * radius;
        double offsetZ = Math.sin(angle) * radius;

        double flightAngle = angle + Math.PI + (random.nextDouble() * 2.0 - 1.0) * Math.toRadians(maxSpread);

        Vec velocity = new Vec(Math.cos(flightAngle) * speed, 0, Math.sin(flightAngle) * speed);

        BulletManager.spawn(target, "environment", offsetX, offsetZ, velocity);
    }

    public static void spawn(Player player, String source, double offsetX, double offsetZ, Vec velocity) {
        Entity bullet = new Entity(EntityType.ITEM_DISPLAY);
        // set entity properties
        bullet.setNoGravity(true);
        bullet.setHasPhysics(false);
        bullet.setTag(BulletManager.SOURCE, source);
        bullet.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setItemStack(ItemStack.of(Material.ECHO_SHARD).builder().itemModel(Wayfare.NAMESPACE + Wayfare.NAMESPACE_SEPARATOR + "bullet").build());
            meta.setTransformationInterpolationDuration(2);
            meta.setPosRotInterpolationDuration(4);
            meta.setWidth(2.0F);
            meta.setHeight(2.0F);
            meta.setScale(new Vec(0, 0, 0));
        });
        bullet.setBoundingBox(2, 2, 2);
        // spawn entity
        Pos position = player.getPosition();
        bullet.setInstance(player.getInstance(), new Pos(position.x() + offsetX, BulletManager.HEIGHT, position.z() + offsetZ));
        bullet.setVelocity(velocity);

        Scheduler scheduler = bullet.scheduler();
        scheduler.scheduleTask(() -> {
            // entity spawn animation
            bullet.editEntityMeta(ItemDisplayMeta.class, meta -> {
                meta.setTransformationInterpolationStartDelta(0);
                meta.setScale(new Vec(1, 1, 1));
            });
        }, TaskSchedule.nextTick(), TaskSchedule.stop());

        scheduler.scheduleTask(() -> {
            // entity remove animation
            bullet.editEntityMeta(ItemDisplayMeta.class, meta -> {
                meta.setTransformationInterpolationStartDelta(0);
                meta.setScale(new Vec(0, 0, 0));
            });
        }, TaskSchedule.tick(55), TaskSchedule.stop());

        scheduler.scheduleTask(() -> ParticlePacketManager.show(player, Particle.INSTANT_EFFECT, bullet.getPosition(), ParticlePacketManager.NO_OFFSET, 0.0F, 1), TaskSchedule.tick(1), TaskSchedule.tick(1));

        // remove entity
        bullet.scheduleRemove(Duration.ofSeconds(3));
    }
}
