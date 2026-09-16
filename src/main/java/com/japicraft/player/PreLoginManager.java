package com.japicraft.player;

import com.japicraft.Wayfare;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;

public class PreLoginManager {
    public static void register() {
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerPreLoginEvent.class, event -> {
            if (MinecraftServer.getConnectionManager().getOnlinePlayerCount() >= Wayfare.MAX_PLAYERS) {
                event.getConnection().kick(Component.text("The server is currently full!", NamedTextColor.RED));
            }
        });
    }
}
