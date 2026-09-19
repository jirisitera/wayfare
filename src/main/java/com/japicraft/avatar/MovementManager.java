package com.japicraft.avatar;

import com.japicraft.server.InstanceRegistry;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientInputPacket;
import net.minestom.server.timer.TaskSchedule;

public class MovementManager {
    public void register(InstanceRegistry registry) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, event -> {
            if (event.getPacket() instanceof ClientInputPacket input) {
                Player player = event.getPlayer();

                int x = (input.left() ? 1 : 0) - (input.right() ? 1 : 0);
                int z = (input.forward() ? 1 : 0) - (input.backward() ? 1 : 0);
                boolean sneak = input.shift();
                boolean sprint = (input.sprint() || input.jump()) && !sneak;

                AvatarManager avatarManager = registry.getOrCreate(event.getPlayer().getUuid()).getAvatarManager();
                avatarManager.update(x, z, sneak, sprint);

                long taskTime = avatarManager.nextTask();
                player.scheduler().submitTask(() -> {
                    if (!player.isOnline() || !avatarManager.isTask(taskTime)) {
                        return TaskSchedule.stop();
                    }
                    avatarManager.update(x, z, sneak, sprint);
                    return TaskSchedule.nextTick();
                });
            }
        });
    }
}
