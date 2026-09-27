package com.japicraft.game;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.tag.Tag;

public class SidebarManager {
    public static final Tag<SidebarManager> MANAGER_TAG = Tag.Transient("wayfare:sidebarManager");
    private final Sidebar board = new Sidebar(Component.text("Wayfare"));
    private final Player player;

    public SidebarManager(Player player) {
        this.player = player;
        board.createLine(new Sidebar.ScoreboardLine("points", Component.text("Points: 0"), 1, Sidebar.NumberFormat.blank()));
        board.addViewer(player);
    }

    public void update() {
        board.updateLineContent("points", Component.text("Points: " + player.getTag(PointManager.STORAGE)));
    }
}
