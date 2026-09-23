package com.japicraft;

import com.japicraft.avatar.MovementManager;
import com.japicraft.camera.CursorManager;
import com.japicraft.command.InstanceCommand;
import com.japicraft.command.StopCommand;
import com.japicraft.config.Config;
import com.japicraft.config.ConfigManager;
import com.japicraft.player.DisconnectManager;
import com.japicraft.player.PreLoginManager;
import com.japicraft.player.SpawnManager;
import com.japicraft.server.InstanceRegistry;
import com.japicraft.server.ResourcePackManager;
import com.japicraft.server.ServerListManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;

import java.util.Scanner;

public class Wayfare {
    public static final String NAMESPACE = "wayfare";
    public static final String NAMESPACE_SEPARATOR = ":";
    public static final Key FONT = Key.key(Wayfare.NAMESPACE, "default");
    public static final ComponentLogger LOGGER = ComponentLogger.logger(Wayfare.class);

    public static Config getConfig() {
        return ConfigManager.getInstance();
    }

    void main() {
        setupEnvironment();
        MinecraftServer server = MinecraftServer.init(new Auth.Online());
        ConfigManager.reload();
        setupProperties();

        InstanceRegistry instanceRegistry = new InstanceRegistry();

        new SpawnManager().register(instanceRegistry);
        new DisconnectManager().register(instanceRegistry);
        new MovementManager().register(instanceRegistry);

        new CursorManager().register(instanceRegistry);
        new ServerListManager().register();
        new PreLoginManager().register();
        new ResourcePackManager().register();

        LOGGER.atInfo().log(Component.text("All managers loaded!"));

        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new InstanceCommand());
        commandManager.register(new StopCommand());
        LOGGER.atInfo().log(Component.text("All commands loaded!"));

        server.start(Wayfare.getConfig().host(), Wayfare.getConfig().port());
        setupConsole();
    }

    private void setupEnvironment() {
        Runtime.getRuntime().addShutdownHook(new Thread(MinecraftServer::stopCleanly));
    }

    private void setupProperties() {
        MinecraftServer.setBrandName(Wayfare.getConfig().displayName());
    }

    private void setupConsole() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                CommandManager manager = MinecraftServer.getCommandManager();
                manager.execute(manager.getConsoleSender(), command);
            }
        }).start();
    }
}
