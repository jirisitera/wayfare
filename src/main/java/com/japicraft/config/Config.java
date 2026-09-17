package com.japicraft.config;

public record Config(
    String displayName,
    String host,
    int port,
    int maxPlayers,
    String motd,
    String resourcePackSource,
    String resourcePackPrompt
) {
}
