package com.japicraft.command;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop");
        setCondition((sender, _) -> !(sender instanceof Player player) || player.getPermissionLevel() >= 4);
        setDefaultExecutor((_, _) -> {
            MinecraftServer.stopCleanly();
            System.exit(0);
        });
    }
}
