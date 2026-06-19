package com.novaclient.module;

public class Module {

    private final String name;
    private final ModuleManager.Category category;
    private final String description;
    private boolean enabled = false;
    private int keybind = -1;

    public Module(String name, ModuleManager.Category category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public String getName() { return name; }
    public ModuleManager.Category getCategory() { return category; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean v) { this.enabled = v; }
    public void toggle() { this.enabled = !this.enabled; }
    public int getKeybind() { return keybind; }
    public void setKeybind(int k) { this.keybind = k; }
}
