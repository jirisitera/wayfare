package com.japicraft.player;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

public class PingManager {
    private final BossBar display = BossBar.bossBar(Component.empty(), 1.0F, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);

    public void schedule(Player player) {
        display.addViewer(player);
        player.scheduler().submitTask(() -> {
            if (!player.isOnline()) {
                return TaskSchedule.stop();
            }
            display.name(Component.text("Ping: " + player.getLatency() + "ms"));
            return TaskSchedule.seconds(1L);
        });
    }
}
