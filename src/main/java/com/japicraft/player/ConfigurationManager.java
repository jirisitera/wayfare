package com.japicraft.player;

import com.japicraft.server.InstanceRegistry;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.trait.PlayerEvent;

public class ConfigurationManager {
    public void register(EventNode<PlayerEvent> eventNode) {
        InstanceRegistry instanceRegistry = new InstanceRegistry();
        eventNode.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            // prepare player spawn
            event.getPlayer().setRespawnPoint(SpawnManager.ORIGIN);
            event.setSpawningInstance(instanceRegistry.get());
        });
    }
}
