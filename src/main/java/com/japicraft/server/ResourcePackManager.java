package com.japicraft.server;

import com.japicraft.Wayfare;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class ResourcePackManager {
    private static String calculateHash(URI uri) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            // download file
            HttpResponse<byte[]> response = client.send(HttpRequest.newBuilder().uri(uri).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
            // calculate SHA1 hash
            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest(response.body()));
        } catch (InterruptedException | IOException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static UUID generateUniqueId(String hash) {
        return UUID.nameUUIDFromBytes(hash.getBytes(StandardCharsets.UTF_8));
    }

    public void register() {
        // prepare resource pack
        URI packURI = URI.create(Wayfare.getConfig().resourcePackSource());
        String packHash = ResourcePackManager.calculateHash(packURI);
        if (packHash == null) {
            return;
        }
        ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
            .prompt(Component.text(Wayfare.getConfig().resourcePackPrompt()))
            .packs(ResourcePackInfo.resourcePackInfo(ResourcePackManager.generateUniqueId(packHash), packURI, packHash))
            .required(true)
            .build();
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> event.getPlayer().sendResourcePacks(request));
        Wayfare.LOGGER.atInfo().log(Component.text("Resource pack loaded!"));
    }
}
