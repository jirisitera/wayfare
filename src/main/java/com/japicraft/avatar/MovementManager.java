package com.japicraft.avatar;

import com.japicraft.server.TagRegistry;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.network.packet.client.play.ClientInputPacket;
import net.minestom.server.timer.TaskSchedule;

public class MovementManager {
    public void register(EventNode<PlayerEvent> eventNode) {
        eventNode.addListener(PlayerPacketEvent.class, event -> {
            if (event.getPacket() instanceof ClientInputPacket input) {
                Player player = event.getPlayer();

                int x = (input.left() ? 1 : 0) - (input.right() ? 1 : 0);
                int z = (input.forward() ? 1 : 0) - (input.backward() ? 1 : 0);
                boolean sneak = input.shift();
                boolean sprint = (input.sprint() || input.jump()) && !sneak;

                MountManager mountManager = TagRegistry.get(player, MountManager.MANAGER_TAG);
                mountManager.update(x, z, sneak, sprint);

                long taskTime = mountManager.nextTask();
                player.scheduler().submitTask(() -> {
                    if (!player.isOnline() || !mountManager.isTask(taskTime)) {
                        return TaskSchedule.stop();
                    }
                    mountManager.update(x, z, sneak, sprint);
                    return TaskSchedule.nextTick();
                });
            }
        });
    }
}
