package com.japicraft.server;

import net.minestom.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InstanceRegistry {
    private final Map<UUID, InstanceManager> instances = new HashMap<>();

    public InstanceManager getOrCreate(UUID uuid) {
        InstanceManager instance = instances.get(uuid);
        if (instance == null) {
            instance = new InstanceManager();
            instances.put(uuid, instance);
        }
        return instance;
    }

    public void remove(UUID uuid) {
        InstanceManager instance = instances.remove(uuid);
        if (instance == null) {
            return;
        }
        MinecraftServer.getInstanceManager().unregisterInstance(instance.getInstance());
    }
}
