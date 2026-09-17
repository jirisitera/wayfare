package com.japicraft.player;

import com.japicraft.avatar.AvatarManager;
import com.japicraft.camera.CameraManager;
import com.japicraft.server.InstanceManager;
import com.japicraft.server.InstanceRegistry;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

public class SpawnManager {
    public static final Pos ORIGIN = new Pos(0, 0, 0, 0, 0);

    public void register(InstanceRegistry instanceRegistry) {
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            // prepare player spawn
            player.setRespawnPoint(SpawnManager.ORIGIN);
            event.setSpawningInstance(instanceRegistry.getOrCreate(player.getUuid()).getInstance());
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            // setup player
            player.setInvisible(true);
            player.setGameMode(GameMode.ADVENTURE);
            new PingManager().schedule(player);
            // create new instance for player
            InstanceManager instance = instanceRegistry.getOrCreate(player.getUuid());
            CameraManager cameraManager = instance.getCameraManager();
            AvatarManager avatarManager = instance.getAvatarManager();
            // setup camera
            player.spectate(cameraManager.getCamera());
            cameraManager.getAnchor().addPassenger(player);
            cameraManager.follow(player, avatarManager.getAvatar());
            // setup avatar
            avatarManager.initializeMovement(player);
            avatarManager.setSkin(player);
            // show tutorial
            player.sendMessage(Component.text("Welcome to Wayfare!"));
            player.sendMessage(Component.text("Use WASD to move around."));
            player.sendMessage(Component.text("Use mouse to move cursor."));
        });
    }
}
