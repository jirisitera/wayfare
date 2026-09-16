package com.japicraft.avatar;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientInputPacket;
import net.minestom.server.tag.Tag;

public class MovementManager {
    public static final Tag<Integer> INPUT_X = Tag.Integer("inputX").defaultValue(0);
    public static final Tag<Integer> INPUT_Z = Tag.Integer("inputZ").defaultValue(0);
    public static final Tag<Boolean> INPUT_SNEAK = Tag.Boolean("inputSneak").defaultValue(false);
    public static final Tag<Boolean> INPUT_SPRINT = Tag.Boolean("inputSprint").defaultValue(false);

    public static void register() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, event -> {
            if (event.getPacket() instanceof ClientInputPacket input) {
                Player player = event.getPlayer();
                player.setTag(INPUT_X, (input.left() ? 1 : 0) - (input.right() ? 1 : 0));
                player.setTag(INPUT_Z, (input.forward() ? 1 : 0) - (input.backward() ? 1 : 0));
                player.setTag(INPUT_SNEAK, input.shift());
                player.setTag(INPUT_SPRINT, input.sprint());
            }
        });
    }
}
