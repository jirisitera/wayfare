package com.japicraft.ability;

import com.japicraft.avatar.AvatarManager;
import com.japicraft.packet.ParticlePacketManager;
import com.japicraft.server.TagRegistry;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.DamageEventPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AttackManager {
    private static final double RADIUS = 1.25;
    private static final double HEIGHT = 1.0;
    private static final int ITERATIONS = 8;
    private static final double ARC_DEGREES = 90.0;
    private static final double HALF_ARC_DEGREES = ARC_DEGREES / 2.0;
    private static final double QUERY_RADIUS = RADIUS + 1.0;

    public static void showParticles(Player player, float yaw) {
        AtomicInteger counter = new AtomicInteger();
        player.scheduler().submitTask(() -> {
            int iteration = counter.getAndAdd(2);
            int next = iteration + 1;
            if (!player.isOnline() || next >= AttackManager.ITERATIONS) {
                return TaskSchedule.stop();
            }
            Pos position = player.getPosition();
            AttackManager.sendParticles(player, position, yaw, iteration);
            AttackManager.sendParticles(player, position, yaw, next);
            return TaskSchedule.nextTick();
        });
    }

    public static void swing(Player player, float yaw) {
        showParticles(player, yaw);

        Set<Player> hitTargets = new HashSet<>();
        AtomicInteger ticks = new AtomicInteger();
        player.scheduler().submitTask(() -> {
            if (!player.isOnline() || ticks.getAndIncrement() >= 4) {
                return TaskSchedule.stop();
            }
            AttackManager.checkTargets(player, yaw, hitTargets);
            return TaskSchedule.nextTick();
        });
    }

    private static void checkTargets(Player attacker, float attackYaw, Set<Player> hitTargets) {
        Pos attackerPosition = attacker.getPosition();
        for (Entity entity : attacker.getInstance().getNearbyEntities(attackerPosition, QUERY_RADIUS)) {
            if (entity == attacker || !(entity instanceof Player target) || hitTargets.contains(target)) {
                continue;
            }
            Pos targetPosition = target.getPosition();
            double targetWidth = target.getBoundingBox().width();
            double targetHeight = target.getBoundingBox().height();
            double horizontalDistance = Math.hypot(targetPosition.x() - attackerPosition.x(), targetPosition.z() - attackerPosition.z());
            boolean verticalOverlap = targetPosition.y() < attackerPosition.y() + HEIGHT && targetPosition.y() + targetHeight > attackerPosition.y();
            double reach = AttackManager.RADIUS + targetWidth / 2.0;
            if (horizontalDistance > reach || !verticalOverlap) {
                continue;
            }
            if (AttackManager.getDifference(attackerPosition, targetPosition, attackYaw) <= HALF_ARC_DEGREES) {
                hitTargets.add(target);
                attacker.sendMessage("Hit " + target);
                target.sendMessage("Hit by " + attacker);
                AttackManager.applyDamage(target);
            }
        }
    }

    private static void applyDamage(Player player) {
        int entityId = TagRegistry.get(player, AvatarManager.MANAGER_TAG).getAvatar().getEntityId();
        player.sendPacketToViewersAndSelf(new DamageEventPacket(entityId, 1, 0, 0, null));
    }

    private static double getDifference(Pos attacker, Pos target, float attackYaw) {
        double targetYaw = normalizeYaw(Math.toDegrees(Math.atan2(target.z() - attacker.z(), target.x() - attacker.x())) - 90.0);
        double difference = targetYaw - normalizeYaw(attackYaw);
        if (difference > 180.0) difference -= 360.0;
        if (difference < -180.0) difference += 360.0;
        return Math.abs(difference);
    }

    private static double normalizeYaw(double yaw) {
        double normalized = yaw % 360.0;
        return normalized < 0.0 ? normalized + 360.0 : normalized;
    }

    private static void sendParticles(Player player, Pos position, float yaw, int iteration) {
        double radians = Math.toRadians(yaw - HALF_ARC_DEGREES + (ARC_DEGREES * iteration / AttackManager.ITERATIONS));
        double x = position.x() - (Math.sin(radians) * AttackManager.RADIUS);
        double y = position.y() + AttackManager.HEIGHT;
        double z = position.z() + (Math.cos(radians) * AttackManager.RADIUS);
        ParticlePacketManager.show(player, Particle.CRIT, new Pos(x, y, z), ParticlePacketManager.NO_OFFSET, 0, 1);
    }
}
