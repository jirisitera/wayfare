package com.japicraft.server;

import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

import java.util.function.Function;

public final class TagRegistry {
    public static <M> M get(Player player, Tag<M> tag) {
        return player.getTag(tag);
    }

    private static <M> M create(Player player, Tag<M> tag, Function<Player, M> factory) {
        M manager = factory.apply(player);
        player.setTag(tag, manager);
        return manager;
    }

    public static <M> M getOrCreate(Player player, Tag<M> tag, Function<Player, M> factory) {
        M manager = TagRegistry.get(player, tag);
        if (manager != null) {
            return manager;
        }
        return TagRegistry.create(player, tag, factory);
    }
}
