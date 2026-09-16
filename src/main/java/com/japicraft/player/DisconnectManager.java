package com.japicraft.player;

import com.japicraft.server.InstanceRegistry;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerDisconnectEvent;

public class DisconnectManager {
    public DisconnectManager(InstanceRegistry instanceRegistry) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, event -> {
            Player player = event.getPlayer();
            instanceRegistry.remove(player.getUuid());
        });
    }
}
