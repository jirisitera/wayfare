package com.japicraft.camera;

import com.japicraft.Wayfare;
import com.japicraft.ability.CooldownManager;
import com.japicraft.avatar.AvatarManager;
import com.japicraft.server.TagRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.network.packet.client.common.ClientKeepAlivePacket;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;
import net.minestom.server.network.packet.client.play.*;
import net.minestom.server.tag.Tag;

import java.time.Duration;

public class CursorManager {
    private static final Component SPRITE = Component.text("🖱").shadowColor(ShadowColor.none()).font(Wayfare.FONT);
    private static final Title.Times TIMES = Title.Times.times(Duration.ZERO, Duration.ofSeconds(20), Duration.ofSeconds(1));
    private static final Tag<Float> INPUT_YAW = Tag.Float("inputYaw").defaultValue(0.0F);
    private static final Tag<Float> INPUT_PITCH = Tag.Float("inputPitch").defaultValue(0.0F);

    private static TextColor getColor(float yaw, float pitch) {
        Coordinates coordinates = Coordinates.fromRotation(yaw, pitch);
        int x = (int) (coordinates.x() * 4095.0);
        int y = (int) (coordinates.y() * 4095.0);
        return TextColor.color(x / 16, y / 16, x % 16 * 16 + y % 16);
    }

    public static void update(Player player, float yaw, float pitch) {
        player.setTag(CursorManager.INPUT_YAW, yaw);
        player.setTag(CursorManager.INPUT_PITCH, pitch);
        player.showTitle(Title.title(Component.text(""), CursorManager.SPRITE.color(CursorManager.getColor(yaw, pitch)), CursorManager.TIMES));
    }

    public void register(EventNode<PlayerEvent> eventNode) {
        eventNode.addListener(PlayerPacketEvent.class, event -> {
            Player player = event.getPlayer();
            switch (event.getPacket()) {
                case ClientPlayerRotationPacket rotation -> {
                    float yaw = rotation.yaw();
                    float pitch = rotation.pitch();
                    if (yaw == player.getTag(CursorManager.INPUT_YAW) && pitch == player.getTag(CursorManager.INPUT_PITCH)) {
                        return;
                    }
                    CursorManager.update(player, yaw, pitch);
                }
                case ClientAttackPacket _ -> {
                    if (CooldownManager.checkAndApply(player)) {
                        return;
                    }
                    TagRegistry.get(player, AvatarManager.MANAGER_TAG).attack(player.getTag(CursorManager.INPUT_YAW), player.getTag(CursorManager.INPUT_PITCH));
                }
                case ClientInteractEntityPacket interact -> {
                    if (interact.hand() != PlayerHand.MAIN || CooldownManager.checkAndApply(player)) {
                        return;
                    }
                    TagRegistry.get(player, AvatarManager.MANAGER_TAG).shoot(player.getTag(CursorManager.INPUT_YAW), player.getTag(CursorManager.INPUT_PITCH), 1);
                }
                case ClientPickItemFromEntityPacket _ -> player.sendMessage("Middle clicked!");
                case ClientTickEndPacket _, ClientKeepAlivePacket _, ClientInputPacket _,
                     ClientChunkBatchReceivedPacket _,
                     ClientPluginMessagePacket _, ClientChatSessionUpdatePacket _, ClientPlayerLoadedPacket _,
                     ClientPlayerPositionAndRotationPacket _, ClientTeleportConfirmPacket _, ClientPunchPacket _ -> {
                }
                default -> player.sendMessage(event.getPacket().toString());
            }
        });
    }
}
