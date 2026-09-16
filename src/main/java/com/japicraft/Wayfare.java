package com.japicraft;

import com.japicraft.avatar.MovementManager;
import com.japicraft.camera.CursorManager;
import com.japicraft.command.InstanceCommand;
import com.japicraft.player.ConfigurationManager;
import com.japicraft.player.DisconnectManager;
import com.japicraft.player.PreLoginManager;
import com.japicraft.player.SpawnManager;
import com.japicraft.server.InstanceRegistry;
import com.japicraft.server.ServerListManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

import java.util.Scanner;

public class Wayfare {
    public static final String NAMESPACE = "wayfare";
    public static final String DISPLAY_NAME = "Wayfare";
    public static final int MAX_PLAYERS = 100;
    public static final ComponentLogger LOGGER = ComponentLogger.logger(Wayfare.class);
    private static final String HOST = "0.0.0.0";
    private static final int PORT = 25565;
    public static RegistryKey<DimensionType> DIMENSION;

    void main() {
        setupEnvironment();
        MinecraftServer server = MinecraftServer.init(new Auth.Online());
        setupProperties();

        InstanceRegistry instanceRegistry = new InstanceRegistry();

        ConfigurationManager.register(instanceRegistry);
        MovementManager.register();
        CursorManager.register();
        ServerListManager.register();
        PreLoginManager.register();

        new SpawnManager(instanceRegistry);
        new DisconnectManager(instanceRegistry);
        LOGGER.atInfo().log(Component.text("All managers loaded!"));

        MinecraftServer.getCommandManager().register(new InstanceCommand());
        LOGGER.atInfo().log(Component.text("All commands loaded!"));

        server.start(HOST, PORT);
        setupConsole();
    }

    private void setupEnvironment() {
        Runtime.getRuntime().addShutdownHook(new Thread(MinecraftServer::stopCleanly, "Minestom-Shutdown-Hook"));
    }

    private void setupProperties() {
        DIMENSION = MinecraftServer.getDimensionTypeRegistry().register(Wayfare.NAMESPACE + ":private_instance", DimensionType.builder().build());
        MinecraftServer.setBrandName(Wayfare.DISPLAY_NAME);
    }

    private void setupConsole() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                CommandManager manager = MinecraftServer.getCommandManager();
                manager.execute(manager.getConsoleSender(), command);
            }
        }, "Console-Reader").start();
    }
}
