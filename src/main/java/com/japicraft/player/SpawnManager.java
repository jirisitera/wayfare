package com.japicraft.player;

import com.japicraft.ability.BulletManager;
import com.japicraft.avatar.AvatarManager;
import com.japicraft.avatar.MountManager;
import com.japicraft.camera.CameraManager;
import com.japicraft.camera.CursorManager;
import com.japicraft.game.MenuManager;
import com.japicraft.game.PointManager;
import com.japicraft.game.SidebarManager;
import com.japicraft.server.TagRegistry;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;

public class SpawnManager {
    public static final Pos ORIGIN = new Pos(0, 0, 0, 0, 0);

    public void register(EventNode<PlayerEvent> eventNode) {
        eventNode.addListener(PlayerSpawnEvent.class, event -> {
            // setup player
            Player player = event.getPlayer();
            player.setInvisible(true);
            player.setGameMode(GameMode.ADVENTURE);

            // create referenced managers
            TagRegistry.getOrCreate(player, SidebarManager.MANAGER_TAG, SidebarManager::new);
            AvatarManager avatarManager = TagRegistry.getOrCreate(player, AvatarManager.MANAGER_TAG, AvatarManager::new);
            MountManager mountManager = TagRegistry.getOrCreate(player, MountManager.MANAGER_TAG, MountManager::new);

            // create standalone managers
            new CameraManager(player);
            new PingManager(player);

            // setup environment
            PointManager.schedule(player);
            BulletManager.scheduleSpawning(player);
            CursorManager.update(player, 0.0F, 0.0F);
            mountManager.addPassenger(avatarManager.getAvatar());

            // show tutorial
            MenuManager.openMain(player);
        });
    }
}
