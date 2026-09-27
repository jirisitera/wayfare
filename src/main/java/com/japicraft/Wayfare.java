package com.japicraft;

import com.japicraft.avatar.MovementManager;
import com.japicraft.camera.CursorManager;
import com.japicraft.command.InstanceCommand;
import com.japicraft.command.StopCommand;
import com.japicraft.config.Config;
import com.japicraft.config.ConfigManager;
import com.japicraft.player.ConfigurationManager;
import com.japicraft.player.DisconnectManager;
import com.japicraft.player.PreLoginManager;
import com.japicraft.player.SpawnManager;
import com.japicraft.server.ResourcePackManager;
import com.japicraft.server.ServerListManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.PlayerEvent;

import java.util.Scanner;

public class Wayfare {
    public static final String NAMESPACE = "wayfare";
    public static final String NAMESPACE_SEPARATOR = ":";
    public static final Key FONT = Key.key(Wayfare.NAMESPACE, "default");
    public static final ComponentLogger LOGGER = ComponentLogger.logger(Wayfare.class);
    public static CommandSender CONSOLE;

    public static Config getConfig() {
        return ConfigManager.getInstance();
    }

    void main() {
        MinecraftServer server = MinecraftServer.init(new Auth.Offline());

        ConfigManager.reload();

        prepareEnvironment();
        prepareProperties();
        prepareManagers();
        prepareCommands();
        prepareConsole();

        server.start(Wayfare.getConfig().host(), Wayfare.getConfig().port());
    }

    private void prepareEnvironment() {
        Runtime.getRuntime().addShutdownHook(new Thread(MinecraftServer::stopCleanly));
        LOGGER.atInfo().log(Component.text("Loaded JVM environment."));
    }

    private void prepareProperties() {
        MinecraftServer.setBrandName(Wayfare.getConfig().displayName());
        LOGGER.atInfo().log(Component.text("Loaded server properties."));
    }

    private void prepareManagers() {
        EventNode<PlayerEvent> playerEventNode = EventNode.type("player", EventFilter.PLAYER);

        MinecraftServer.getGlobalEventHandler().addChild(playerEventNode);

        new ConfigurationManager().register(playerEventNode);
        new SpawnManager().register(playerEventNode);
        new DisconnectManager().register(playerEventNode);
        new MovementManager().register(playerEventNode);
        new CursorManager().register(playerEventNode);
        new ResourcePackManager().register(playerEventNode);

        LOGGER.atInfo().log(Component.text("Loaded managers for player events."));

        new ServerListManager().register();
        new PreLoginManager().register();

        LOGGER.atInfo().log(Component.text("Loaded managers for server events."));
    }

    private void prepareCommands() {
        CommandManager commandManager = MinecraftServer.getCommandManager();

        CONSOLE = commandManager.getConsoleSender();

        commandManager.register(new InstanceCommand());
        commandManager.register(new StopCommand());

        LOGGER.atInfo().log(Component.text("Loaded console commands."));
    }

    private void prepareConsole() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                MinecraftServer.getCommandManager().execute(CONSOLE, scanner.nextLine());
            }
        }).start();
    }
}
