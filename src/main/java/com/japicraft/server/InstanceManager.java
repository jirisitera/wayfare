package com.japicraft.server;

import com.japicraft.Wayfare;
import com.japicraft.avatar.AvatarManager;
import com.japicraft.camera.CameraManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

public class InstanceManager {
    public static final int MAX_HEIGHT = 90;
    private final InstanceContainer instance;
    private final AvatarManager avatarManager;
    private final CameraManager cameraManager;

    public InstanceManager() {
        instance = MinecraftServer.getInstanceManager().createInstanceContainer(Wayfare.DIMENSION);
        configure();
        avatarManager = new AvatarManager(instance);
        cameraManager = new CameraManager(instance);
    }

    public InstanceContainer getInstance() {
        return instance;
    }

    public AvatarManager getAvatarManager() {
        return avatarManager;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    private void configure() {
        instance.setChunkSupplier(LightingChunk::new);
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, MAX_HEIGHT, Block.GRASS_BLOCK));
    }
}
