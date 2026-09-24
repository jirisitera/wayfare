package com.japicraft.command;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

import java.util.Collection;

public class InstanceCommand extends Command {
    public InstanceCommand() {
        super("instance");
        setCondition((sender, _) -> !(sender instanceof Player player) || player.getPermissionLevel() >= 4);

        setDefaultExecutor((sender, _) -> {
            Collection<Instance> instances = MinecraftServer.getInstanceManager().getInstances();

            sender.sendMessage(Component.text("Loaded instances: " + instances.size()));
            for (Instance instance : instances) {
                String uuid = instance.getUuid().toString();
                long entityCount = instance.getEntities().stream().filter(entity -> !entity.getEntityType().equals(EntityType.PLAYER)).count();
                int playerCount = instance.getPlayers().size();
                sender.sendMessage(Component.text(String.format("- %s (entities: %d, players: %d)", uuid, entityCount, playerCount)));
            }
        });
    }
}
