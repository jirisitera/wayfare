package com.japicraft.player;

import com.japicraft.server.InstanceRegistry;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

public class ConfigurationManager {
    public static final Pos INITIAL_SPAWN = new Pos(0, 0, 0, 0, 0);

    public static void register(InstanceRegistry instanceRegistry) {
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            player.setRespawnPoint(INITIAL_SPAWN);
            event.setSpawningInstance(instanceRegistry.getOrCreate(player.getUuid()).getInstance());
        });
    }
}
