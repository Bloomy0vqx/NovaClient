package com.novaclient.core.config;

import com.google.gson.*;
import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleManager;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class ConfigManager {
    private static final ConfigManager INSTANCE = new ConfigManager();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Path configDir;
    private Path configFile;

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        configDir = Paths.get("novaclient");
        configFile = configDir.resolve("config.json");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        JsonObject root = new JsonObject();
        JsonArray modulesArray = new JsonArray();

        for (Module module : ModuleManager.getInstance().getModules()) {
            modulesArray.add(module.toJson());
        }

        root.add("modules", modulesArray);
        root.addProperty("version", "1.0.0");

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile.toFile()), StandardCharsets.UTF_8)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!Files.exists(configFile)) return;

        try (Reader reader = new InputStreamReader(new FileInputStream(configFile.toFile()), StandardCharsets.UTF_8)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("modules")) return;

            JsonArray modulesArray = root.getAsJsonArray("modules");
            for (JsonElement element : modulesArray) {
                JsonObject moduleObj = element.getAsJsonObject();
                String name = moduleObj.get("name").getAsString();
                ModuleManager.getInstance().getModule(name).ifPresent(m -> m.fromJson(moduleObj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        for (Module module : ModuleManager.getInstance().getModules()) {
            module.disable();
            module.setKeyBind(0);
        }
        save();
    }
}
