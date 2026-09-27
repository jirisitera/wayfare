package com.japicraft.packet;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.network.packet.server.play.CameraPacket;
import net.minestom.server.network.packet.server.play.EntityAttributesPacket;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;

import java.util.List;

public class EntityPacketManager {
    public static final Vec ZERO_VECTOR = new Vec(0);
    public static final List<AttributeModifier> NO_MODIFIERS = List.of();

    public static void spawn(Player player, Entity entity, Pos position) {
        player.sendPacket(new SpawnEntityPacket(entity.getEntityId(), entity.getUuid(), entity.getEntityType(), position, 0, 0, EntityPacketManager.ZERO_VECTOR));
        player.sendPacket(entity.getMetadataPacket());
    }

    public static void spectate(Player player, int entityId) {
        player.sendPacket(new CameraPacket(entityId));
    }

    public static void addPassengers(Player player, int vehicleId, List<Integer> passengerIds) {
        player.sendPacket(new SetPassengersPacket(vehicleId, passengerIds));
    }

    public static void scale(Player player, int entityId, double scale) {
        player.sendPacket(new EntityAttributesPacket(entityId, List.of(
            new EntityAttributesPacket.Property(Attribute.SCALE, scale, EntityPacketManager.NO_MODIFIERS),
            new EntityAttributesPacket.Property(Attribute.CAMERA_DISTANCE, 4.0 / scale, EntityPacketManager.NO_MODIFIERS)
        )));
    }
}
