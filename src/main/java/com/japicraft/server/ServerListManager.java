package com.japicraft.server;

import com.japicraft.Wayfare;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.object.ObjectContents;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.Status;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ServerListManager {
    private static final byte[] FAVICON = ServerListManager.loadIcon();
    private static final Component DESCRIPTION = Component.text("Wayfare Test Server").color(TextColor.color(102, 238, 255));
    private static final Status.VersionInfo VERSION_INFO = new Status.VersionInfo(Wayfare.DISPLAY_NAME, MinecraftServer.PROTOCOL_VERSION);

    public static void register() {
        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, event -> event.setStatus(Status.builder()
            .playerInfo(Status.PlayerInfo.builder()
                .maxPlayers(Wayfare.MAX_PLAYERS)
                .onlinePlayers(MinecraftServer.getConnectionManager().getOnlinePlayerCount())
                .sample(ServerListManager.getSample())
                .build()
            )
            .favicon(ServerListManager.FAVICON)
            .description(ServerListManager.DESCRIPTION)
            .versionInfo(ServerListManager.VERSION_INFO)
            .enforcesSecureChat(true)
            .build()
        ));
    }

    private static Component getSample() {
        List<Component> heads = new ArrayList<>();
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            heads.add(Component.object(ObjectContents.playerHead(player.getUuid())));
        }
        return Component.empty().append(heads);
    }

    private static byte[] loadIcon() {
        try (InputStream stream = Wayfare.class.getResourceAsStream("/icon.png")) {
            if (stream == null) {
                return null;
            }
            return stream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }
}
