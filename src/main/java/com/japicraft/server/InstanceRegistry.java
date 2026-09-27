package com.japicraft.server;

import com.japicraft.Wayfare;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;

import java.util.UUID;

public class InstanceRegistry {
    public static final int MIN_HEIGHT = 0;
    public static final int MAX_HEIGHT = 10;
    private final InstanceContainer instanceContainer;

    public InstanceRegistry() {
        instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer(
            MinecraftServer.getDimensionTypeRegistry().register(Wayfare.NAMESPACE + Wayfare.NAMESPACE_SEPARATOR + UUID.randomUUID(), DimensionType.builder()
                .skybox(DimensionType.Skybox.NONE).build()
            )
        );
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(InstanceRegistry.MIN_HEIGHT, InstanceRegistry.MAX_HEIGHT, Block.GRASS_BLOCK));
    }

    public InstanceContainer get() {
        return instanceContainer;
    }
}
