package com.japicraft.game;

import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

public class PointManager {
    private static final Tag<Integer> STORAGE = Tag.Integer("points");

    private static void add(Player player, int amount) {
        player.setTag(PointManager.STORAGE, PointManager.get(player) + amount);
        player.sendMessage("+ " + amount + " points!");
    }

    private static void remove(Player player, int amount) {
        int current = PointManager.get(player);
        if (current <= 0) {
            return;
        }
        player.setTag(PointManager.STORAGE, current - amount);
        player.sendMessage("- " + amount + " points!");
    }

    private static int get(Player player) {
        return player.getTag(PointManager.STORAGE);
    }
}
