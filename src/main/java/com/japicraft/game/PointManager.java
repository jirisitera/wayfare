package com.japicraft.game;

import com.japicraft.server.TagRegistry;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.TaskSchedule;

public class PointManager {
    public static final Tag<Integer> STORAGE = Tag.Integer("points");

    public static void add(Player player, int amount) {
        player.setTag(PointManager.STORAGE, PointManager.get(player) + amount);
        TagRegistry.get(player, SidebarManager.MANAGER_TAG).update();
    }

    public static void remove(Player player, int amount) {
        int current = PointManager.get(player);
        if (current <= 0) {
            return;
        }
        player.setTag(PointManager.STORAGE, current - amount);
        TagRegistry.get(player, SidebarManager.MANAGER_TAG).update();
    }

    public static int get(Player player) {
        if (!player.hasTag(PointManager.STORAGE)) {
            return 0;
        }
        return player.getTag(PointManager.STORAGE);
    }

    public static void schedule(Player player) {
        player.scheduler().submitTask(() -> {
            if (!player.isOnline()) {
                return TaskSchedule.stop();
            }
            PointManager.add(player, 1);
            return TaskSchedule.seconds(1L);
        });
    }
}
