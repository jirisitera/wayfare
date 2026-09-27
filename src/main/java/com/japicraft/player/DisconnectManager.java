package com.japicraft.player;

import com.japicraft.avatar.AvatarManager;
import com.japicraft.avatar.MountManager;
import com.japicraft.server.TagRegistry;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.trait.PlayerEvent;

public class DisconnectManager {
    public void register(EventNode<PlayerEvent> eventNode) {
        eventNode.addListener(PlayerDisconnectEvent.class, event -> {
            Player player = event.getPlayer();
            TagRegistry.get(player, AvatarManager.MANAGER_TAG).remove();
            TagRegistry.get(player, MountManager.MANAGER_TAG).remove();
        });
    }
}
