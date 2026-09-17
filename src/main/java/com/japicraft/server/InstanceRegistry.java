package com.japicraft.server;

import com.japicraft.Wayfare;
import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InstanceRegistry {
    private final Map<UUID, InstanceManager> instances = new HashMap<>();
    public RegistryKey<DimensionType> dimensionType;

    public InstanceRegistry() {
        dimensionType = MinecraftServer.getDimensionTypeRegistry().register(Wayfare.NAMESPACE + ":private_instance", DimensionType.builder().build());
    }

    public InstanceManager getOrCreate(UUID uuid) {
        InstanceManager instance = instances.get(uuid);
        if (instance == null) {
            instance = new InstanceManager(dimensionType);
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
