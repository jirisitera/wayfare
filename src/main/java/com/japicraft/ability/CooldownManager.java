package com.japicraft.ability;

import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

public class CooldownManager {
    private static final Tag<Long> LAST_ATTACK_TIME = Tag.Long("lastAttackTime").defaultValue(0L);
    private static final long ATTACK_COOLDOWN = 200L;

    public static boolean checkAndApply(Player player) {
        long current = System.currentTimeMillis();
        if (current - player.getTag(CooldownManager.LAST_ATTACK_TIME) < CooldownManager.ATTACK_COOLDOWN) {
            return true;
        }
        player.setTag(CooldownManager.LAST_ATTACK_TIME, current);
        return false;
    }
}
