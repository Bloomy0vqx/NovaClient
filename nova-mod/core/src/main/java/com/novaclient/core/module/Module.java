package com.novaclient.core.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.novaclient.core.module.setting.Setting;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected final String name;
    protected final String description;
    protected final ModuleCategory category;
    protected final int defaultKey;
    protected boolean enabled;
    protected boolean expanded;
    protected int keyBind;
    protected final List<Setting> settings = new ArrayList<>();

    public Module(String name, String description, ModuleCategory category, int defaultKey) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.defaultKey = defaultKey;
        this.keyBind = defaultKey;
        this.enabled = false;
        this.expanded = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ModuleCategory getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKeyBind() { return keyBind; }
    public void setKeyBind(int key) { this.keyBind = key; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
    public List<Setting> getSettings() { return settings; }

    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }

    public void enable() {
        enabled = true;
        onEnable();
    }

    public void disable() {
        enabled = false;
        onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
    public void onRender2D(float partialTicks) {}
    public void onRender3D(float partialTicks) {}
    public void onScreenClose() {}

    protected void addSetting(Setting setting) {
        settings.add(setting);
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("enabled", enabled);
        obj.addProperty("keyBind", keyBind);

        JsonArray settingsArray = new JsonArray();
        for (Setting setting : settings) {
            JsonObject settingObj = new JsonObject();
            settingObj.addProperty("name", setting.getName());
            settingObj.add("value", setting.toJson());
            settingsArray.add(settingObj);
        }
        obj.add("settings", settingsArray);
        return obj;
    }

    public void fromJson(JsonObject obj) {
        if (obj.has("enabled")) {
            this.enabled = obj.get("enabled").getAsBoolean();
        }
        if (obj.has("keyBind")) {
            this.keyBind = obj.get("keyBind").getAsInt();
        }
        if (obj.has("settings")) {
            JsonArray settingsArray = obj.getAsJsonArray("settings");
            for (int i = 0; i < settingsArray.size(); i++) {
                JsonObject settingObj = settingsArray.get(i).getAsJsonObject();
                String settingName = settingObj.get("name").getAsString();
                for (Setting setting : settings) {
                    if (setting.getName().equals(settingName)) {
                        setting.fromJson(settingObj.get("value"));
                        break;
                    }
                }
            }
        }
        if (enabled) onEnable();
    }
}
