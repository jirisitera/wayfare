package com.japicraft.camera;

import com.japicraft.Wayfare;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.common.ClientKeepAlivePacket;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;
import net.minestom.server.network.packet.client.play.*;
import net.minestom.server.tag.Tag;

import java.time.Duration;

public class CursorManager {
    private static final String ICON = "🖱";
    private static final Key FONT = Key.key(Wayfare.NAMESPACE, "cursor");
    private static final Component SPRITE = Component.text(CursorManager.ICON).shadowColor(ShadowColor.none()).font(CursorManager.FONT);
    private static final Title.Times TIMES = Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO);
    private static final Tag<Float> INPUT_YAW = Tag.Float("inputYaw").defaultValue(0.0F);
    private static final Tag<Float> INPUT_PITCH = Tag.Float("inputPitch").defaultValue(0.0F);

    private static TextColor getColor(float yaw, float pitch) {
        Coordinates coordinates = Coordinates.fromRotation(yaw, pitch);
        int x = (int) (coordinates.x() * 4095.0);
        int y = (int) (coordinates.y() * 4095.0);
        return TextColor.color(x / 16, y / 16, x % 16 * 16 + y % 16);
    }

    private static boolean isTopSide(Coordinates coordinates) {
        return coordinates.y() <= 0.5;
    }

    private static boolean isBottomSide(Coordinates coordinates) {
        return coordinates.y() >= 0.5;
    }

    private static boolean isLeftSide(Coordinates coordinates) {
        return coordinates.x() <= 0.5;
    }

    private static boolean isRightSide(Coordinates coordinates) {
        return coordinates.x() >= 0.5;
    }

    public static void update(Player player, float yaw, float pitch) {
        player.setTag(CursorManager.INPUT_YAW, yaw);
        player.setTag(CursorManager.INPUT_PITCH, pitch);
        player.showTitle(Title.title(CursorManager.SPRITE.color(CursorManager.getColor(yaw, pitch)), Component.empty(), CursorManager.TIMES));
    }

    public void register() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, event -> {
            switch (event.getPacket()) {
                case ClientPlayerRotationPacket rotation -> {
                    Player player = event.getPlayer();
                    float yaw = rotation.yaw();
                    float pitch = rotation.pitch();
                    if (yaw == player.getTag(CursorManager.INPUT_YAW) && pitch == player.getTag(CursorManager.INPUT_PITCH)) {
                        return;
                    }
                    CursorManager.update(player, yaw, pitch);
                }
                case ClientAttackPacket _ -> {
                    Player player = event.getPlayer();
                    player.sendMessage("Left clicked!");
                    Coordinates coordinates = Coordinates.fromRotation(player.getTag(CursorManager.INPUT_YAW), player.getTag(CursorManager.INPUT_PITCH));
                    if (CursorManager.isTopSide(coordinates)) {
                        player.sendMessage("(top side)");
                    }
                    if (CursorManager.isBottomSide(coordinates)) {
                        player.sendMessage("(bottom side)");
                    }
                    if (CursorManager.isLeftSide(coordinates)) {
                        player.sendMessage("(left side)");
                    }
                    if (CursorManager.isRightSide(coordinates)) {
                        player.sendMessage("(right side)");
                    }
                }
                case ClientInteractEntityPacket interact -> {
                    if (interact.hand() == PlayerHand.MAIN) {
                        event.getPlayer().sendMessage("Right clicked!");
                    }
                }
                case ClientPickItemFromEntityPacket _ -> {
                    Player player = event.getPlayer();
                    player.sendMessage("Middle clicked!");
                }
                case ClientTickEndPacket _, ClientKeepAlivePacket _, ClientInputPacket _,
                     ClientChunkBatchReceivedPacket _,
                     ClientPluginMessagePacket _, ClientChatSessionUpdatePacket _, ClientPlayerLoadedPacket _,
                     ClientPlayerPositionAndRotationPacket _, ClientTeleportConfirmPacket _, ClientPunchPacket _ -> {
                }
                default -> event.getPlayer().sendMessage(event.getPacket().toString());
            }
        });
    }
}
