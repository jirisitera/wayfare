package com.japicraft.packet;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;

public class ParticlePacketManager {
    public static final Pos NO_OFFSET = new Pos(0, 0, 0);

    public static void show(Player player, Particle particle, Point position, Point offset, float speed, int count) {
        player.sendPacket(new ParticlePacket(particle, position, offset, speed, count));
    }
}
