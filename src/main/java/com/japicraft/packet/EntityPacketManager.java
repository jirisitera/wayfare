package com.japicraft.packet;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.CameraPacket;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;

import java.util.List;

public class EntityPacketManager {
    public static Vec ZERO_VECTOR = new Vec(0);

    public static void spawn(Player player, Entity entity, Pos position) {
        player.sendPacket(new SpawnEntityPacket(
            entity.getEntityId(), entity.getUuid(), entity.getEntityType(),
            position, 0, 0, EntityPacketManager.ZERO_VECTOR
        ));
        player.sendPacket(entity.getMetadataPacket());
    }

    public static void spectate(Player player, Entity entity) {
        player.sendPacket(new CameraPacket(entity.getEntityId()));
    }

    public static void addPassengers(Player player, int vehicleId, List<Integer> passengerIds) {
        player.sendPacket(new SetPassengersPacket(vehicleId, passengerIds));
    }
}
