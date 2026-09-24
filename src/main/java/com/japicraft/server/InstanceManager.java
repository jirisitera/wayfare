package com.japicraft.server;

import com.japicraft.avatar.AvatarManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

public class InstanceManager {
    public static final int MIN_HEIGHT = 0;
    public static final int MAX_HEIGHT = 10;
    private final InstanceContainer instance;
    private final AvatarManager avatarManager;

    public InstanceManager(RegistryKey<DimensionType> dimensionType) {
        instance = MinecraftServer.getInstanceManager().createInstanceContainer(dimensionType);
        instance.setChunkSupplier(LightingChunk::new);
        instance.setGenerator(unit -> unit.modifier().fillHeight(InstanceManager.MIN_HEIGHT, InstanceManager.MAX_HEIGHT, Block.GRASS_BLOCK));
        avatarManager = new AvatarManager(instance);
    }

    public InstanceContainer getInstance() {
        return instance;
    }

    public AvatarManager getAvatarManager() {
        return avatarManager;
    }
}
