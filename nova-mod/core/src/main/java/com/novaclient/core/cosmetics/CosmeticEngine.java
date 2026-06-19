package com.novaclient.core.cosmetics;

import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CosmeticEngine {
    private static final CosmeticEngine INSTANCE = new CosmeticEngine();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final List<Cosmetic> registeredCosmetics = new CopyOnWriteArrayList<>();
    private final List<CustomCape> customCapes = new CopyOnWriteArrayList<>();
    private final Map<UUID, PlayerCosmetics> playerCosmeticsCache = new ConcurrentHashMap<>();
    private final Map<String, CosmeticTexture> textureCache = new ConcurrentHashMap<>();
    private Path cosmeticsDir;

    private static final String API_BASE = "https://api.novaclient.com/cosmetics";

    public static CosmeticEngine getInstance() {
        return INSTANCE;
    }

    public void init() {
        cosmeticsDir = Paths.get("novaclient", "cosmetics");
        try {
            Files.createDirectories(cosmeticsDir);
            Files.createDirectories(cosmeticsDir.resolve("custom_capes"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        registerDefaultCosmetics();
        loadLocalData();
        loadCustomCapes();
    }

    private void registerDefaultCosmetics() {
        registeredCosmetics.add(new Cosmetic("nova_default", "Nova Cape", CosmeticType.CAPE, "Default Nova Client cape"));
        registeredCosmetics.add(new Cosmetic("nova_chromatic", "Chromatic Cape", CosmeticType.CAPE, "Rainbow animated cape"));
        registeredCosmetics.add(new Cosmetic("nova_galaxy", "Galaxy Cape", CosmeticType.CAPE, "Galaxy-themed cape"));
        registeredCosmetics.add(new Cosmetic("nova_fire", "Fire Cape", CosmeticType.CAPE, "Animated fire cape"));
        registeredCosmetics.add(new Cosmetic("nova_ender", "Ender Cape", CosmeticType.CAPE, "Ender-themed cape"));
        registeredCosmetics.add(new Cosmetic("wings_angel", "Angel Wings", CosmeticType.WINGS, "White angel wings"));
        registeredCosmetics.add(new Cosmetic("wings_demon", "Demon Wings", CosmeticType.WINGS, "Red demon wings"));
        registeredCosmetics.add(new Cosmetic("wings_butterfly", "Butterfly Wings", CosmeticType.WINGS, "Colorful butterfly wings"));
        registeredCosmetics.add(new Cosmetic("wings_dragon", "Dragon Wings", CosmeticType.WINGS, "Dragon wings"));
        registeredCosmetics.add(new Cosmetic("wings_ender", "Ender Wings", CosmeticType.WINGS, "Ender particle wings"));
        registeredCosmetics.add(new Cosmetic("hat_crown", "Golden Crown", CosmeticType.HAT, "Golden crown"));
        registeredCosmetics.add(new Cosmetic("hat_party", "Party Hat", CosmeticType.HAT, "Colorful party hat"));
        registeredCosmetics.add(new Cosmetic("hat_viking", "Viking Helmet", CosmeticType.HAT, "Norse viking helmet"));
        registeredCosmetics.add(new Cosmetic("hat_wizard", "Wizard Hat", CosmeticType.HAT, "Purple wizard hat"));
        registeredCosmetics.add(new Cosmetic("cloak_royal", "Royal Cloak", CosmeticType.CLOAK, "Purple royal cloak"));
        registeredCosmetics.add(new Cosmetic("cloak_shadow", "Shadow Cloak", CosmeticType.CLOAK, "Dark shadow cloak"));
        registeredCosmetics.add(new Cosmetic("bandana_ninja", "Ninja Bandana", CosmeticType.BANDANA, "Black ninja bandana"));
        registeredCosmetics.add(new Cosmetic("bandana_pirate", "Pirate Bandana", CosmeticType.BANDANA, "Red pirate bandana"));
    }

    public CompletableFuture<List<Cosmetic>> fetchCosmeticsFromAPI() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(API_BASE + "/catalog").openConnection();
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    try (Scanner s = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                        String body = s.useDelimiter("\\A").hasNext() ? s.next() : "";
                        Type listType = new TypeToken<List<Cosmetic>>() {}.getType();
                        return gson.fromJson(body, listType);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        });
    }

    public CompletableFuture<PlayerCosmetics> fetchPlayerCosmetics(UUID playerUUID) {
        if (playerCosmeticsCache.containsKey(playerUUID)) {
            return CompletableFuture.completedFuture(playerCosmeticsCache.get(playerUUID));
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(API_BASE + "/player/" + playerUUID).openConnection();
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    try (Scanner s = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                        String body = s.useDelimiter("\\A").hasNext() ? s.next() : "";
                        PlayerCosmetics cosmetics = gson.fromJson(body, PlayerCosmetics.class);
                        playerCosmeticsCache.put(playerUUID, cosmetics);
                        return cosmetics;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new PlayerCosmetics(playerUUID);
        });
    }

    public CompletableFuture<CosmeticTexture> getTexture(String textureId) {
        if (textureCache.containsKey(textureId)) {
            return CompletableFuture.completedFuture(textureCache.get(textureId));
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(API_BASE + "/texture/" + textureId).openConnection();
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    try (Scanner s = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                        String body = s.useDelimiter("\\A").hasNext() ? s.next() : "";
                        CosmeticTexture texture = gson.fromJson(body, CosmeticTexture.class);
                        textureCache.put(textureId, texture);
                        return texture;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public void setPlayerCosmetics(UUID player, List<String> cosmeticIds) {
        PlayerCosmetics cosmetics = playerCosmeticsCache.computeIfAbsent(player, PlayerCosmetics::new);
        cosmeticIds.forEach(cosmetics::equip);
        playerCosmeticsCache.put(player, cosmetics);
        saveLocalData();
    }

    public List<Cosmetic> getRegisteredCosmetics() {
        return registeredCosmetics;
    }

    public List<Cosmetic> getCosmeticsByType(CosmeticType type) {
        return registeredCosmetics.stream()
            .filter(c -> c.getType() == type)
            .collect(Collectors.toList());
    }

    public Optional<Cosmetic> getCosmetic(String id) {
        return registeredCosmetics.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst();
    }

    private void loadLocalData() {
        Path cacheFile = cosmeticsDir.resolve("cache.json");
        if (Files.exists(cacheFile)) {
            try (Reader reader = new InputStreamReader(new FileInputStream(cacheFile.toFile()), StandardCharsets.UTF_8)) {
                JsonObject root = gson.fromJson(reader, JsonObject.class);
                if (root != null && root.has("textures")) {
                    Type mapType = new TypeToken<Map<String, CosmeticTexture>>() {}.getType();
                    textureCache.putAll(gson.fromJson(root.get("textures"), mapType));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveLocalData() {
        Path cacheFile = cosmeticsDir.resolve("cache.json");
        JsonObject root = new JsonObject();
        root.add("textures", gson.toJsonTree(textureCache));
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(cacheFile.toFile()), StandardCharsets.UTF_8)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CustomCape addCustomCape(String name, String sourceFilePath) {
        String id = "custom_cape_" + System.currentTimeMillis();
        Path destPath = cosmeticsDir.resolve("custom_capes").resolve(id + ".png");
        try {
            Files.copy(Paths.get(sourceFilePath), destPath, StandardCopyOption.REPLACE_EXISTING);
            CustomCape cape = new CustomCape(id, name, destPath.toString());
            customCapes.add(cape);
            saveCustomCapes();
            Cosmetic cosmetic = new Cosmetic(id, name, CosmeticType.CAPE, "Custom uploaded cape");
            registeredCosmetics.add(cosmetic);
            return cape;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean removeCustomCape(String capeId) {
        boolean removed = customCapes.removeIf(c -> c.getId().equals(capeId));
        if (removed) {
            try {
                Path capePath = cosmeticsDir.resolve("custom_capes").resolve(capeId + ".png");
                Files.deleteIfExists(capePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            registeredCosmetics.removeIf(c -> c.getId().equals(capeId));
            saveCustomCapes();
        }
        return removed;
    }

    public List<CustomCape> getCustomCapes() {
        return new ArrayList<>(customCapes);
    }

    private void loadCustomCapes() {
        Path capesFile = cosmeticsDir.resolve("custom_capes.json");
        if (Files.exists(capesFile)) {
            try (Reader reader = new InputStreamReader(new FileInputStream(capesFile.toFile()), StandardCharsets.UTF_8)) {
                JsonObject root = gson.fromJson(reader, JsonObject.class);
                if (root != null && root.has("capes")) {
                    for (com.google.gson.JsonElement elem : root.getAsJsonArray("capes")) {
                        CustomCape cape = CustomCape.fromJson(elem.getAsJsonObject());
                        customCapes.add(cape);
                        registeredCosmetics.add(new Cosmetic(cape.getId(), cape.getName(), CosmeticType.CAPE, "Custom uploaded cape"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCustomCapes() {
        Path capesFile = cosmeticsDir.resolve("custom_capes.json");
        JsonObject root = new JsonObject();
        com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
        for (CustomCape cape : customCapes) {
            arr.add(cape.toJson());
        }
        root.add("capes", arr);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(capesFile.toFile()), StandardCharsets.UTF_8)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
