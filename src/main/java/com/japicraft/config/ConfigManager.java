package com.japicraft.config;

import com.japicraft.Wayfare;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Path PATH = Path.of("config.toml");
    private static final String DISPLAY_NAME = "Wayfare";
    private static final String HOST = "0.0.0.0";
    private static final long PORT = 25565L;
    private static final long MAX_PLAYERS = 50L;
    private static final String MOTD = "Wayfare Server";
    private static final String RESOURCE_PACK_SOURCE = "https://github.com/jirisitera/wayfare/releases/latest/download/wayfare.zip";
    private static final String RESOURCE_PACK_PROMPT = "This server requires a resource pack. Would you like to download it?";
    private static volatile Config INSTANCE;

    public static void reload() {
        try {
            if (!Files.exists(ConfigManager.PATH)) {
                InputStream inputStream = ConfigManager.class.getResourceAsStream("/config.toml");
                if (inputStream == null) {
                    return;
                }
                Files.copy(inputStream, ConfigManager.PATH);
                Wayfare.LOGGER.atInfo().log("Could not find server config, generating a new one from default values.");
            }
            TomlParseResult parsed = Toml.parse(ConfigManager.PATH);
            if (parsed.hasErrors()) {
                parsed.errors().forEach(e -> Wayfare.LOGGER.atError().log("Error while loading config: " + e));
                return;
            }
            ConfigManager.INSTANCE = new Config(
                parsed.getString("displayName", () -> ConfigManager.DISPLAY_NAME),
                parsed.getString("host", () -> ConfigManager.HOST),
                (int) parsed.getLong("port", () -> ConfigManager.PORT),
                (int) parsed.getLong("maxPlayers", () -> ConfigManager.MAX_PLAYERS),
                parsed.getString("motd", () -> ConfigManager.MOTD),
                parsed.getString("resourcePackSource", () -> ConfigManager.RESOURCE_PACK_SOURCE),
                parsed.getString("resourcePackPrompt", () -> ConfigManager.RESOURCE_PACK_PROMPT)
            );
            Wayfare.LOGGER.atInfo().log("Loaded server config to memory.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Config getInstance() {
        if (ConfigManager.INSTANCE == null) {
            ConfigManager.reload();
        }
        return ConfigManager.INSTANCE;
    }
}
