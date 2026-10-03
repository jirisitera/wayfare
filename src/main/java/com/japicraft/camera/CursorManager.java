package com.japicraft.camera;

import com.japicraft.Wayfare;
import com.japicraft.ability.CooldownManager;
import com.japicraft.avatar.AvatarManager;
import com.japicraft.server.TagRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
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
    private static final Component SPRITE = Component.text("🖱").color(NamedTextColor.BLACK).font(Wayfare.FONT);
    private static final Title.Times TIMES = Title.Times.times(Duration.ZERO, Duration.ofSeconds(30), Duration.ZERO);
    private static final Tag<Float> INPUT_YAW = Tag.Float("inputYaw").defaultValue(0.0F);
    private static final Tag<Float> INPUT_PITCH = Tag.Float("inputPitch").defaultValue(0.0F);
    private static final Tag<Boolean> INPUT_LOCK = Tag.Boolean("inputLock").defaultValue(false);

    private static ShadowColor getShadowColor(float yaw, float pitch, float previousYaw, float previousPitch) {
        Coordinates current = Coordinates.fromRotation(yaw, pitch);
        Coordinates previous = Coordinates.fromRotation(previousYaw, previousPitch);
        int currentX = (int) (current.x() * 255.0);
        int currentY = (int) (current.y() * 255.0);
        int previousX = (int) (previous.x() * 255.0);
        int previousY = (int) (previous.y() * 255.0);
        return ShadowColor.shadowColor((previousY << 24) | (currentX << 16) | (currentY << 8) | previousX);
    }

    public static void update(Player player, float yaw, float pitch, float previousYaw, float previousPitch) {
        player.setTag(CursorManager.INPUT_YAW, yaw);
        player.setTag(CursorManager.INPUT_PITCH, pitch);
        player.showTitle(Title.title(Component.empty(), CursorManager.SPRITE.shadowColor(CursorManager.getShadowColor(yaw, pitch, previousYaw, previousPitch)), CursorManager.TIMES));
    }

    public void register(EventNode<PlayerEvent> eventNode) {
        eventNode.addListener(PlayerPacketEvent.class, event -> {
            Player player = event.getPlayer();
            switch (event.getPacket()) {
                case ClientPlayerRotationPacket rotation -> {
                    float previousYaw = player.getTag(CursorManager.INPUT_YAW);
                    float previousPitch = player.getTag(CursorManager.INPUT_PITCH);
                    float yaw = rotation.yaw();
                    float pitch = rotation.pitch();

                    boolean locked = player.getTag(CursorManager.INPUT_LOCK);

                    if (yaw == previousYaw && pitch == previousPitch) {
                        if (!locked) {
                            player.setTag(CursorManager.INPUT_LOCK, true);
                            CursorManager.update(player, yaw, pitch, yaw, pitch);
                        }
                        return;
                    } else if (locked) {
                        player.setTag(CursorManager.INPUT_LOCK, false);
                    }
                    CursorManager.update(player, yaw, pitch, previousYaw, previousPitch);
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
