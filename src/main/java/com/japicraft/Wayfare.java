package com.japicraft;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.client.play.ClientInputPacket;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

public class Wayfare {
    private static final ComponentLogger LOGGER = ComponentLogger.logger(Wayfare.class);

    void main() {
        Runtime.getRuntime().addShutdownHook(new Thread(MinecraftServer::stopCleanly, "Minestom-Shutdown-Hook"));

        MinecraftServer server = MinecraftServer.init(new Auth.Online());
        MinecraftServer.setBrandName("Wayfare");

        RegistryKey<DimensionType> baseDimension = MinecraftServer.getDimensionTypeRegistry()
            .register("wayfare:welcome", DimensionType.builder().build());

        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer(baseDimension);

        instance.setChunkSupplier(LightingChunk::new);
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 90, Block.GRASS_BLOCK));

        Pos spawn = new Pos(0, 100, 0, 0, 90);

        Entity camera = new Entity(EntityType.ITEM_DISPLAY);
        camera.setNoGravity(true);
        camera.setInstance(instance, spawn);

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            event.setSpawningInstance(instance);
            player.setRespawnPoint(spawn);
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            player.setGameMode(GameMode.SPECTATOR);

            player.spectate(camera);
            player.sendMessage(Component.text("Welcome!"));
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, event -> {
            if (event.getPacket() instanceof ClientInputPacket input) {
                Player player = event.getPlayer();
                if (input.forward()) {
                    player.sendMessage(Component.text("Forward!"));
                }
            }
        });

        server.start("0.0.0.0", 25565);
        LOGGER.atInfo().log(Component.text("Server loaded!").color(NamedTextColor.RED));
    }
}
