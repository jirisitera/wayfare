package com.japicraft.avatar;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MountEntity extends Entity {
    public MountEntity(EntityType type) {
        super(type);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void updateNewViewer(@NotNull Player player) {
        super.updateNewViewer(player);
        // force update the passenger list
        if (isActive() && !getPassengers().isEmpty()) {
            player.sendPacket(getPassengersPacket());
        }
    }
}
