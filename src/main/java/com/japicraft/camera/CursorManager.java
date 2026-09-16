package com.japicraft.camera;

import com.japicraft.Wayfare;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.common.ClientKeepAlivePacket;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;
import net.minestom.server.network.packet.client.play.*;

import java.time.Duration;

public class CursorManager {
    private static final String ICON = "🖱";
    private static final Key FONT = Key.key(Wayfare.NAMESPACE, "cursor");
    private static final Component SPRITE = Component.text(CursorManager.ICON).shadowColor(ShadowColor.none()).font(CursorManager.FONT);
    private static final Title.Times TIMES = Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO);

    public static void register() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, event -> {
            switch (event.getPacket()) {
                case ClientPlayerRotationPacket rotation ->
                    event.getPlayer().showTitle(Title.title(CursorManager.SPRITE.color(CursorManager.getCursorColor(rotation.yaw(), rotation.pitch())), Component.empty(), CursorManager.TIMES));
                case ClientAttackPacket _ -> {
                    Player player = event.getPlayer();
                    player.sendMessage("Left clicked!");

                    Pos position = player.getPosition();
                    Coordinates coordinates = CursorManager.getCursorScreenPixel(position.yaw(), position.pitch());

                    if (coordinates.equals(new Coordinates(0, 0))) {
                        player.sendMessage("Clicked in top-left corner!");
                    }
                }
                case ClientInteractEntityPacket interact -> {
                    if (!interact.hand().equals(PlayerHand.MAIN)) {
                        return;
                    }
                    Player player = event.getPlayer();
                    player.sendMessage("Right clicked!");
                }
                case ClientPickItemFromEntityPacket _ -> {
                    Player player = event.getPlayer();
                    player.sendMessage("Middle clicked!");
                }
                case ClientTickEndPacket _, ClientKeepAlivePacket _, ClientInputPacket _,
                     ClientChunkBatchReceivedPacket _,
                     ClientPluginMessagePacket _, ClientChatSessionUpdatePacket _, ClientPlayerLoadedPacket _,
                     ClientPlayerPositionAndRotationPacket _, ClientTeleportConfirmPacket _,
                     ClientAnimationPacket _ -> {
                }
                default -> event.getPlayer().sendMessage(event.getPacket().toString());
            }
        });
    }

    private static TextColor getCursorColor(float yaw, float pitch) {
        Coordinates coordinates = Coordinates.fromRotation(yaw, pitch);
        int x = (int) (coordinates.x() * 4095.0);
        int y = (int) (coordinates.y() * 4095.0);
        return TextColor.color(x / 16, y / 16, x % 16 * 16 + y % 16);
    }

    public static Coordinates getCursorScreenPixel(float yaw, float pitch) {
        Coordinates coordinates = Coordinates.fromRotation(yaw, pitch);
        return new Coordinates(coordinates.x() * 1920.0, coordinates.y() * 1080.0);
    }
}
