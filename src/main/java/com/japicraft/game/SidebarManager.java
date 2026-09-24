package com.japicraft.game;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Sidebar;

public class SidebarManager {
    private static final Sidebar BOARD = new Sidebar(Component.text("Wayfare"));

    public static void initialize() {
        BOARD.createLine(new Sidebar.ScoreboardLine("points", Component.text(""), 1));
    }

    public static void addViewer(Player player) {
        BOARD.addViewer(player);
    }

    public static void removeViewer(Player player) {
        BOARD.removeViewer(player);
    }

    public static void update() {

    }
}
